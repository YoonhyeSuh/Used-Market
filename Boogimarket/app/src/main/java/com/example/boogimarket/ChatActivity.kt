package com.example.boogimarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boogimarket.databinding.ActivityChatroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding: ActivityChatroomBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var receiverRoom: String
    private lateinit var senderRoom: String
    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerMessages.adapter = messageAdapter

        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val senderUid = mAuth.currentUser?.uid

//        senderRoom = "chat_$receiverUid$senderUid"
//        receiverRoom = "chat_$senderUid$receiverUid"

        val chatRoomId = if (senderUid!! < receiverUid) {
            "chat_$senderUid$receiverUid"
        } else {
            "chat_$receiverUid$senderUid"
        }

        binding.txtTitle.text = receiverName

        binding.btnSubmit.setOnClickListener {
            val message = binding.edtMessage.text.toString()
            val messageObject = Message(message, senderUid)
            messageObject.timestamp = FieldValue.serverTimestamp()

//            if (senderUid != null) {
//                db.collection("chats").document(senderRoom)
//                    .collection("messages").add(messageObject)
//                    .addOnSuccessListener {
//                        db.collection("chats").document(receiverRoom)
//                            .collection("messages").add(messageObject)
//                    }
//            }

            if (senderUid != null) {
                db.collection("chats").document(chatRoomId)
                    .collection("messages").add(messageObject)
            }

            binding.edtMessage.setText("")
        }

        if (senderUid != null) {
            db.collection("chats").document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING) // ASCENDING or DESCENDING
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Handle error
                        return@addSnapshotListener
                    }

                    messageList.clear()
                    snapshot?.forEach { document ->
                        val message = document.toObject(Message::class.java)
                        messageList.add(message)
                    }

                    messageAdapter.notifyDataSetChanged()
                }
        }
    }
}
