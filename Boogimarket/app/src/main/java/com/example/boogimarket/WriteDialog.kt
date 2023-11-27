package com.example.boogimarket


import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.boogimarket.databinding.ActivityMypageBinding
import com.example.boogimarket.databinding.ItemLayoutBinding
import com.example.boogimarket.databinding.WriteProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class WriteDialog : BottomSheetDialogFragment() {
    private lateinit var binding: WriteProductBinding
    private lateinit var db: FirebaseFirestore
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
        db = FirebaseFirestore.getInstance() // db를 초기화

        binding.imageAdd.setOnClickListener {
           getContent.launch("image/*")
        }
        // 작성 완료 버튼 클릭 시 작성한 데이터를 Firestore에 저장
        //jhr 후에 이미지 추가 예정
        binding.writeFinish.setOnClickListener {

            var postInfo = ProductInformation()
            postInfo.timestamp = System.currentTimeMillis() // 현재 시간을 milliseconds로 저장
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

                // timestamp 설정
                postInfo.timestamp = System.currentTimeMillis()

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
            .addOnSuccessListener { uploadTask ->
                // 이미지 업로드 성공 시
                // 다운로드 URL을 가져오기 위해 이미지가 저장된 경로에서 다운로드 URL을 가져옴
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

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
        var post: ArrayList<ProductInformation> = arrayListOf()

        init {
            // Firestore에서 데이터를 가져오는 부분
            db.collection("post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    post.clear()
                    for (snapshot in documents) {
                        val item = snapshot.toObject(ProductInformation::class.java)
                        post.add(item!!)
                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // 에러 처리
                }
        }

        inner class ViewHolder(private val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            // ViewHolder 클래스에서 View를 초기화하는 코드를 추가
            init {
                // Set click listener for the ViewHolder's view
                binding.root.setOnClickListener(this)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val viewHolder = holder
            viewHolder.bind(post[position])
        }

        override fun getItemCount(): Int {
            // 아이템 수 반환
            return post.size
        }
    }


}
