package com.example.boogimarket

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        if (documentId != null) {
            db.collection("post").document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val settitle = document.getString("title")
                        val setprice = document.getString("price")
                        val setexplain = document.getString("explain")
                        val setlocation = document.getString("location")

                        // EditText 등에 값을 설정하려면 해당 값을 설정해주면 됩니다.
                        binding.editTextPrice.setText(setprice)
                        binding.editTextTitle.setText(settitle)
                        binding.editTextLocation.setText(setlocation)
                        binding.editTextDescription.setText(setexplain)
                        // 나머지 값들도 동일한 방식으로 설정해주세요.

                        // 추가 작업이 필요하다면 여기서 처리하세요.
                    } else {

                    }
                }
                .addOnFailureListener { 

                }
        }

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