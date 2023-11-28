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
    private lateinit var userList: ArrayList<Pair<User, String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityChatlistBinding.bind(view)

        mAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        userList = ArrayList()
        adapter = UserAdapter(requireContext(), userList, db)

        binding.userChatlistView.layoutManager = LinearLayoutManager(requireContext())
        binding.userChatlistView.adapter = adapter



        val currentUser = mAuth.currentUser?.uid

        if (currentUser != null) {
            loadUserChatsR(currentUser)
            loadUserChatS(currentUser)
        }
    }
//post에서 document id 가져오기
    //메세지 받는 사람
    private fun loadUserChatsR(currentUser: String) {
        db.collection("post")
            .whereEqualTo("userId", currentUser)
            .get()
            .addOnSuccessListener { postResult ->
                val postList = ArrayList<String>()
                for (postDocument in postResult) {
                    val documentId = postDocument.getString("documentID")
                    documentId?.let { postList.add(it) }
                }
                if(postList.size > 0) {
                    loadOtherUsers(currentUser, postList)
                }
            }
            .addOnFailureListener {


            }
    }
    //보내는 사람
    private fun loadUserChatS(currentUser: String){
        db.collection("chatlog").document(currentUser).collection("log")
            .get()
            .addOnSuccessListener { result -> val postList = ArrayList<String>()
                for(document in result){
                    val documentId = document.getString("documentId")
                    documentId?.let { postList.add(it) }
                }
                if(postList.size > 0) {
                    loadOtherUsers(currentUser, postList)
                }
            }
            .addOnFailureListener {exception ->
                Log.e("ChatFragment", "Error fetching messages: $exception")
            }
    }
//chats 에서 상대방 id 가져오기
    private fun loadOtherUsers(currentUser: String, postList: ArrayList<String>) {
        for (documentId in postList) {
            db.collection("chats").document(documentId)
                .collection("messages")
                .get()
                .addOnSuccessListener { messageResult ->
                    val otherList = ArrayList<String>()
                    for (messageDocument in messageResult) {
                        val sendId = messageDocument.getString("sendId")
                        val receiverId = messageDocument.getString("receiveId")

                        if (sendId != null && sendId != currentUser) {
                            otherList.add(sendId)
                        }
                        if (receiverId != null && receiverId != currentUser) {
                            otherList.add(receiverId)
                        }
                    }
                    loadUsers(currentUser, otherList, documentId)
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatFragment", "Error fetching messages: $exception")
                }
        }
    }
//유저 데베에서 유저 정보 가져오고 유저리스트에 넣기
    private fun loadUsers(currentUser: String, otherList: ArrayList<String>, documentId: String) {
        for (uId in otherList.distinct()) {
            db.collection("users")
                .whereEqualTo("userId", uId)
                .get()
                .addOnSuccessListener { userResult ->
                    for (userDocument in userResult) {
                        val otherUser = userDocument.toObject(User::class.java)
                        if (currentUser != otherUser.userId) {
                            userList.add(Pair(otherUser, documentId))
                        }
                    }
                    adapter.notifyDataSetChanged()

                    binding.userChatlistView.scrollToPosition(adapter.itemCount - 1)
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatFragment", "Error fetching user data: $exception")
                }
        }
    }
}





