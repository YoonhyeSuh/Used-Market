package com.example.boogimarket


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


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

            val chatFragment = ChatFragment()

            val bundle = Bundle()

            if(mAuth.currentUser?.uid != userId) {

                bundle.putString("userId", userId)
                chatFragment.arguments = bundle

                val intent = Intent(this@DetailsActivity, ChatActivity::class.java)

                val name = binding.textViewName.text


                intent.putExtra("name", name)
                intent.putExtra("uId", userId)

                startActivity(intent)
            }


        }



    }
}

