package com.example.boogimarket


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.boogimarket.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class DetailsActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        // Extracting data from the intent
        val title = intent.getStringExtra("item_title")
        val location = intent.getStringExtra("item_location")
        val price = intent.getStringExtra("item_price")
        val explain = intent.getStringExtra("item_explain")
        val sold = intent.getBooleanExtra("item_sold", false)
        val userId = intent.getStringExtra("item_userId")
        val imgUrl = intent.getStringExtra("item_imgUrl")
        val documentId = intent.getStringExtra("item_documentId")

        // Retrieve other data as needed



        // Create an instance of the generated binding class for the layout
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = " 상세정보"


        binding.textViewTitle.setText("상품명: "+title).toString()
        binding.textViewExplain.setText("설명: "+ explain).toString()
        binding.textViewPrice.setText("가격: "+price).toString()
        binding.textViewLocation.setText("거래 장소: " +location).toString()

        Picasso.get()
            .load(imgUrl)
            .into(binding.imageViewProduct)


        if (sold) {
            binding.textViewSoldStatus.text = " 팔림"
        } else {
            binding.textViewSoldStatus.text = "구매 가능"
        }

        db.collection("users")
            .whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    val name = document.getString("name")
                    binding.textViewName.text = name
                }
            }


        binding.buttonCompleteTransaction.setOnClickListener {

            if(userId != null && mAuth.currentUser?.uid == userId){
                if (documentId != null) {

                    //솔드 필드 업데이트
                    db.collection("post")
                        .document(documentId)
                        .update("sold",true)
                        .addOnSuccessListener {
                            // Update successful
                            Toast.makeText(this, "거래 성사!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Toast.makeText(this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                }

            }else {
                // Display toast if the current user is not the creator
                Toast.makeText(this, "오직 작성자만이 클릭할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageEdit.setOnClickListener{

            if(userId != null && mAuth.currentUser?.uid == userId){
                val intent = Intent(this@DetailsActivity, ModifyActivity::class.java)

                intent.putExtra("documentId", documentId)

                startActivity(intent)
            }else {
                // Display toast if the current user is not the creator
                Toast.makeText(this, "오직 작성자만이 클릭할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonChat.setOnClickListener{
            if(mAuth.currentUser?.uid != userId) {


                val intent = Intent(this@DetailsActivity, ChatActivity::class.java)

                val name = binding.textViewName.text


                intent.putExtra("name", name)
                intent.putExtra("uId", userId)

                startActivity(intent)
            }


        }

        //게시물 삭제
        binding.deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        //액션바 이름 설정
        supportActionBar?.title = "DetailsActivity"
        //뒤로 가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showDeleteConfirmationDialog() {
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
    }

    private fun deletePost(documentId: String) {
        db.collection("post")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공 시
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
}

