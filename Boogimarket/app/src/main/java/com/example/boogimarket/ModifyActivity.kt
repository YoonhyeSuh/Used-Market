package com.example.boogimarket

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import com.example.boogimarket.databinding.ActivityModifyBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class ModifyActivity: AppCompatActivity(){

    private  lateinit var binding: ActivityModifyBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imageViewProduct.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra("documentId")
        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("images")

        //액션바 이름 설정
        supportActionBar?.title = " 게시글 수정"
        //뒤로 가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (documentId != null) {
            db.collection("post").document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val setimage = document.getString("imgUri")
                        val settitle = document.getString("title")
                        val setprice = document.getString("price")
                        val setexplain = document.getString("explain")
                        val setlocation = document.getString("location")

                        // EditText 등에 값을 설정하려면 해당 값을 설정해주면 됩니다.
                        Picasso.get()
                            .load(setimage)
                            .into(binding.imageViewProduct)
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

        binding.imageViewProduct.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.buttonEditPost.setOnClickListener {
            val image = binding.imageViewProduct.imageAlpha.toString()
            val title = binding.editTextTitle.text.toString()
            val price = binding.editTextPrice.text.toString()
            val explain = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()

            val updates = hashMapOf(
                "imgUri" to image,
                "explain" to explain,
                "location" to location,
                "price" to price,
                "title" to title
            )

            if (documentId != null) {
                // 이미지 업로드
                if (selectedImageUri != null) {
                    uploadImage(selectedImageUri!!, documentId)
                }else{
                    updates.remove("imgUri")
                }
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
    private fun uploadImage(imageUri: Uri, documentId: String) {
        val imageRef = storageRef.child(imageFileName(imageUri))

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // 이미지 업로드 성공 시 다운로드 URL을 Firestore에 저장
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    db?.collection("post")?.document(documentId)
                        ?.update("imgUri", uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                // 업로드 실패 처리
            }
    }

    private fun imageFileName(imageUri: Uri): String {
        // 이미지의 MIME 타입을 가져옴
        val resolver: ContentResolver = this.contentResolver
        val mimeType = resolver.getType(imageUri)

        // UUID를 사용하여 고유한 파일 이름 생성
        return "${UUID.randomUUID()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                ?: "jpg" // 기본적으로 jpg로 설정
        }"
    }
}