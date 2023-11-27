package com.example.boogimarket


import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import com.example.boogimarket.databinding.WriteProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class WriteDialog : BottomSheetDialogFragment() {
    private lateinit var binding: WriteProductBinding

    private lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.selectedImageView.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WriteProductBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("images")

        binding.imageAdd.setOnClickListener {
            getContent.launch("image/*")
        }
        // 작성 완료 버튼 클릭 시 작성한 데이터를 Firestore에 저장
        //jhr 후에 이미지 추가 예정
        binding.writeFinish.setOnClickListener {
            var postInfo = ProductInformation()
            postInfo.email = auth.currentUser?.email
            postInfo.userId = auth.uid.toString()
            postInfo.title = binding.writeTitle.text.toString()
            postInfo.price = binding.writePrice.text.toString()
            postInfo.location = binding.writeLocation.text.toString()
            postInfo.explain = binding.writeExplain.text.toString()

            val newDocumentRef = firestore?.collection("post")?.document()
            val documentId = newDocumentRef?.id
            //먼저 생성될 다큐먼트 아이디를 가져오고 그 다큐먼트 안에 set을 하는 방식으로 바꿈
            if (documentId != null) {
                postInfo.documentID= documentId

                // 이미지 업로드
                if (selectedImageUri != null) {
                    uploadImage(selectedImageUri!!, documentId)
                }

                newDocumentRef.set(postInfo)


            }


            val ft = parentFragmentManager.beginTransaction()

            ft.detach(this).attach(this).commit()
            dismiss()
        }

        // 작성을 취소 하는 X버튼 클릭
        binding.writeCancel.setOnClickListener {
            dismiss()
        }

        return view
    }
    private fun uploadImage(imageUri: Uri, documentId: String) {
        val imageRef = storageRef.child(imageFileName(imageUri))

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // 이미지 업로드 성공 시 다운로드 URL을 Firestore에 저장
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore?.collection("post")?.document(documentId)
                        ?.update("imgUri", uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                // 업로드 실패 처리
            }
    }

    private fun imageFileName(imageUri: Uri): String {
        // 이미지의 MIME 타입을 가져옴
        val resolver: ContentResolver = requireContext().contentResolver
        val mimeType = resolver.getType(imageUri)

        // UUID를 사용하여 고유한 파일 이름 생성
        return "${UUID.randomUUID()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                ?: "jpg" // 기본적으로 jpg로 설정
        }"
    }

}