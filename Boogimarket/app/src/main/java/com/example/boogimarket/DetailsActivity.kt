package com.example.boogimarket


import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.example.boogimarket.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        // 인텐트에서 데이터 추출
        val title = intent.getStringExtra("item_title")
        val location = intent.getStringExtra("item_location")
        val price = intent.getStringExtra("item_price")
        val explain = intent.getStringExtra("item_explain")
        val sold = intent.getBooleanExtra("item_sold", false)
        val userId = intent.getStringExtra("item_userId")
        val imgUrl = intent.getStringExtra("item_imgUrl")
        val documentId = intent.getStringExtra("item_documentId")

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = " 상세정보"

        binding.textViewTitle.setText(title).toString()
        binding.textViewExplain.setText("설명: " + explain).toString()
        binding.textViewPrice.setText("가격: " + price + "원").toString()
        binding.textViewLocation.setText("거래 장소: " + location).toString()

        if (imgUrl != "" && imgUrl != null) {
            Picasso.get()
                .load(imgUrl)
                .into(binding.imageViewProduct)
        }

        if (sold) {
            binding.textViewSoldStatus.setText("팔림")
        } else {
            binding.textViewSoldStatus.setText("거래 가능")
        }

        db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name")
                    binding.textViewName.text = name
                }
            }


        binding.buttonCompleteTransaction.setOnClickListener {

            if (userId != null && mAuth.currentUser?.uid == userId) {
                if (documentId != null) {

                    //솔드 필드 업데이트
                    db.collection("post")
                        .document(documentId)
                        .update("sold", true)
                        .addOnSuccessListener {
                            // Update successful
                            Toast.makeText(this, "거래 성사!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Toast.makeText(this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                }

            } else {
                // Display toast if the current user is not the creator
                Toast.makeText(this, "오직 작성자만이 클릭할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageEdit.setOnClickListener {

            if (userId != null && mAuth.currentUser?.uid == userId) {
                val intent = Intent(this@DetailsActivity, ModifyActivity::class.java)

                intent.putExtra("documentId", documentId)

                startActivity(intent)
            } else {
                // Display toast if the current user is not the creator
                Toast.makeText(this, "오직 작성자만이 클릭할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonChat.setOnClickListener {
            val current = mAuth.currentUser?.uid
            if (current != null && current != userId) {

                val log = chatlog(documentId)

                if (documentId != null) {
                    db.collection("chatlog").document(current).collection("log").add(log)
                }


                val intent = Intent(this@DetailsActivity, ChatActivity::class.java)

                val name = binding.textViewName.text


                intent.putExtra("name", name)
                intent.putExtra("uId", userId)
                intent.putExtra("documentId", documentId)

                startActivity(intent)
            }


        }

        //게시물 삭제
        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val userId = intent.getStringExtra("item_userId")
        if (userId != null && mAuth.currentUser?.uid == userId) {
            val builder = AlertDialog.Builder(this)
            val documentId = intent.getStringExtra("item_documentId")
            builder.setTitle("게시물 삭제")
                .setMessage("정말로 이 게시물을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    // 확인 버튼을 눌렀을 때의 동작
                    deletePost(documentId.toString())
                }
                .setNegativeButton("취소") { _, _ ->
                    // 취소 버튼을 눌렀을 때의 동작
                }
                .show()
        } else {
            // Display toast if the current user is not the creator
            Toast.makeText(this, "오직 작성자만이 클릭할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deletePost(documentId: String) {
        db.collection("post")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공 시
                findSender(documentId)
                Toast.makeText(this, "게시물이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 게시물이 삭제되면 이전 화면(HomeFragment 등)으로 돌아가도록 처리
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // 삭제 실패 시
                Toast.makeText(this, "게시물 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun findSender(documentId: String) {

        val user = mAuth.currentUser?.uid
        db.collection("chats").document(documentId).collection("messages").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val sId = document.getString("sendId")
                    val rId = document.getString("receiveId")

                    if (sId != user) {
                        val sendUser = sId
                        if (sendUser != null) {
                            deleteChatlog(documentId, sendUser)
                        }
                    } else {
                        val sendUser = rId
                        if (sendUser != null) {
                            deleteChatlog(documentId, sendUser)
                        }
                    }
                }

            }
    }

    private fun deleteChatlog(documentId: String, sendUser: String) {


        val chatLogRef = db.collection("chatlog").document(sendUser).collection("log")
        chatLogRef.whereEqualTo("documentId", documentId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // 업데이트할 문서에 대한 참조
                    chatLogRef.document(document.id).delete()

                    deleteChatRoom(documentId)

                }
            }
    }

    private fun deleteChatRoom(documentId: String) {
        val chatRef = db.collection("chats").document(documentId)

        chatRef.collection("messages").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    chatRef.collection("messages").document(document.id).delete()
                }
                chatRef.delete()
            }
    }
}


//
//val updates = hashMapOf<String, Any>(
//    "documentId" to FieldValue.delete()
//    // 실제 필드 이름으로 "fieldNameToDelete"를 대체하세요
//)
//
//// 지정된 필드를 삭제하는 업데이트 수행
//docRef.update(updates)
//.addOnSuccessListener {
//    // 삭제 성공
//}
//.addOnFailureListener { e ->
//    // 필요시 실패 처리


