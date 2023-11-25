package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityModifyBinding
import com.google.firebase.firestore.FirebaseFirestore

class ModifyActivity: AppCompatActivity(){

    private  lateinit var binding: ActivityModifyBinding
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra("documentId")
        db = FirebaseFirestore.getInstance()

        supportActionBar?.title = " 게시글 수정"

        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonEditPost.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val price = binding.editTextPrice.text.toString()
            val explain = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()

            val updates = hashMapOf(
                "explain" to explain,
                "location" to location,
                "price" to price,
                "title" to title
            )

            if (documentId != null) {
                db.collection("post").document(documentId)
                    .update(updates as Map<String, Any>)
                    .addOnSuccessListener {
                        // 수정 성공
                        Toast.makeText(this, "수정 성공했습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // 수정 실패
                        Toast.makeText(this, "수정 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }




    }
}