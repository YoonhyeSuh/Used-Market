package com.example.boogimarket


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
                    db.collection("post")
                        .document(documentId)
                        .update("sold",true)
                        .addOnSuccessListener {
                            // Update successful
                            Toast.makeText(this, "Item marked as sold", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Toast.makeText(this, "Failed to mark item as sold", Toast.LENGTH_SHORT).show()
                        }
                }

            }
        }




    }
}