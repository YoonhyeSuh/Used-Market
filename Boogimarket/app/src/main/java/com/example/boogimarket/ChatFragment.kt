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
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.toObject
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

        //val currentUserUid = mAuth.currentUser?.uid
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

//        // 현재 사용자와 채팅한 사용자의 목록 가져오기
//        if (currentUserUid != null) {
//            db.collection("chats")
//                .whereArrayContains("participants", currentUserUid)
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        val participants = document.get("participants") as List<String>
//                        val otherUserId = participants.firstOrNull { it != currentUserUid }
//                        if (otherUserId != null) {
//                            // otherUserId를 사용하여 사용자 정보 가져오기
//                            db.collection("users").document(otherUserId)
//                                .get()
//                                .addOnSuccessListener { userDocument ->
//                                    val otherUser = userDocument.toObject(User::class.java)
//                                    if (otherUser != null) {
//                                        userList.add(otherUser)
//                                        adapter.notifyDataSetChanged()
//                                    }
//                                }
//                                .addOnFailureListener { exception ->
//                                    // 에러 처리
//                                }
//                        }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    // 에러 처리
//                }
//        }
    }
}

