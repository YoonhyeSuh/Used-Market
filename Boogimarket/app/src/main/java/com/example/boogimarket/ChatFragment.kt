package com.example.boogimarket

import android.os.Bundle
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
        mAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        userList = ArrayList()
        adapter = UserAdapter(requireContext(), userList)
        binding.userChatlistView.layoutManager = LinearLayoutManager(requireContext())
        binding.userChatlistView.adapter = adapter

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val currentUser = document.toObject(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser.userId) {
                        userList.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 에러 처리
            }
    }
}