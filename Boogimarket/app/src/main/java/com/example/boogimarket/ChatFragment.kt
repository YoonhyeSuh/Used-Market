package com.example.boogimarket

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boogimarket.databinding.ActivityChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment(R.layout.activity_chatlist) {
    private lateinit var binding: ActivityChatlistBinding
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userList: ArrayList<User>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityChatlistBinding.bind(view)

        val uId = arguments?.getString("userId")
        if (uId != null) {
            print(uId)
        }

        mAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        userList = ArrayList()
        adapter = UserAdapter(requireContext(), userList)
        binding.userChatlistView.layoutManager = LinearLayoutManager(requireContext())
        binding.userChatlistView.adapter = adapter

        // 전달된 userId를 갖고 있는 사용자 문서를 가져옵니다.
        if (uId != null) {
            db.collection("users")
                .whereEqualTo("userId", uId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val currentUser = document.toObject(User::class.java)
                        userList.add(currentUser)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // 실패 처리
                }
        }
    }
}