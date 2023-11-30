package com.example.boogimarket

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.ActivityMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import java.util.*

class MyPageFragment : Fragment() {
    private lateinit var binding: ActivityMypageBinding
  //  private lateinit var storageRef: StorageReference
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userId: String? = null
    private var db = FirebaseFirestore.getInstance()
//    private var selectedImageUri: Uri? = null
//
//    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        if (uri != null) {
//            selectedImageUri = uri
//            binding.mypageImage.setImageURI(uri)
//        }
//    }
//    private fun uploadImage(imageUri: Uri, documentId: String) {
//        val imageRef = storageRef.child(imageFileName(imageUri))
//        val currentUser = mAuth.currentUser?.uid
//        imageRef.putFile(imageUri)
//            .addOnSuccessListener {
//                // 이미지 업로드 성공 시 다운로드 URL을 Firestore에 저장
//                imageRef.downloadUrl.addOnSuccessListener { uri ->
//                    db?.collection("users")?.whereEqualTo("userId",currentUser)
//                        ?.get()
//                        ?.addOnSuccessListener { result ->
//                            for (document in result) {
//                                db.collection("users").document(document.id)
//                                    ?.update("imgUri", uri.toString())
//                            }
//                        }
//                }
//            }
//            .addOnFailureListener {
//                // 업로드 실패 처리
//                Toast.makeText(context, "프로필 사진을 바꾸는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun imageFileName(imageUri: Uri): String {
//        // 이미지의 MIME 타입을 가져옴
//        val resolver: ContentResolver = requireContext().contentResolver
//        val mimeType = resolver.getType(imageUri)
//
//        // UUID를 사용하여 고유한 파일 이름 생성
//        return "${UUID.randomUUID()}.${
//            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
//                ?: "jpg" // 기본적으로 jpg로 설정
//        }"
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser
        userId = currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userName = document.getString("name")
                        val userEmail = document.getString("email")
                        val userBirth = document.getString("birth")

                        binding.mypageName.text = userName
                        binding.mypageEmail.text = userEmail
                        binding.mypageBirth.text = userBirth
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "데이터 가져오기 실패", exception)
                }
        } else {
            Log.d(TAG, "false")
        }

        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


//        // ActionBar 설정
//        (activity as AppCompatActivity?)?.supportActionBar?.title = "MyPageFragment"
//
//        // 뒤로 가기 버튼 설정
//        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)



        binding.mysellBtn.setOnClickListener{
            //액티비티의 이동은 intent
            val intent: Intent = Intent(requireContext(), MySellActivity::class.java)
            startActivity(intent)
        }


    }
}
