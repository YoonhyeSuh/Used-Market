package com.example.boogimarket


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.boogimarket.databinding.WriteProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WriteDialog : BottomSheetDialogFragment() {
    private lateinit var binding: WriteProductBinding

    private lateinit var auth: FirebaseAuth
    private var firestore: FirebaseFirestore? = null
    //private var storage: FirebaseStorage? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WriteProductBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

   /* private fun ImageUpload(view: View, i: Int, id: String) {

    }*/
}
