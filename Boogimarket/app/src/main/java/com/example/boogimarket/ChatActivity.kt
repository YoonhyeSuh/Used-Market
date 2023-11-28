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
        val documentId = intent.getStringExtra("documentId").toString()

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val senderUid = mAuth.currentUser?.uid

        val chatRoomId = documentId

        binding.txtTitle.text = receiverName

        binding.btnSubmit.setOnClickListener {
            //사용자로부터 메세지를 받고, 냅다 데이터클래스 형태로  만들어
            val message = binding.edtMessage.text.toString()
            val messageObject = Message(message, senderUid, receiverUid)
            messageObject.timestamp = FieldValue.serverTimestamp()



            if (senderUid != null) {

                //db에 저장 같은 방을 쓰게끔 구현 걍 사용자가 메세지를 치면 냅다 여기로 들어가
                db.collection("chats").document(chatRoomId)
                    .collection("messages").add(messageObject)
            }

            binding.edtMessage.setText("")
        }

        if (senderUid != null) {
            //아까 위에 방의 데이터를 타임스탬프로 시간을 기록해 시간 순으로 나열 -> 얼추 채팅임
            db.collection("chats").document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING) // ASCENDING or DESCENDING
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Handle error
                        return@addSnapshotListener
                    }
                    //에러 났으니깐 메세지 리스트 초기화
                    messageList.clear()
                    snapshot?.forEach { document ->
                        //에러 안나면 메세지 리스트의 데이터 클래스 형태로 메시지 리스트에 넣기 메세지 리스트 어댑터에 줄 데이터임
                        val message = document.toObject(Message::class.java)
                        messageList.add(message)
                    }

                    messageAdapter.notifyDataSetChanged()
                }
        }
    }
}
