package com.example.boogimarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boogimarket.databinding.ActivityChatroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    //대화할 상대를 선택시 담을 정보 변수
    private lateinit var receiverName:String
    private lateinit var receiverUid:String

    //바인딩 객체
    private  lateinit var binding: ActivityChatroomBinding

    lateinit var mAuth: FirebaseAuth//인증객체
    lateinit var  mDbRef: DatabaseReference// DB 객체

    private lateinit var receiverRoom: String// 받는 대화방
    private lateinit var senderRoom: String// 보낸 대화방

    private lateinit var messageList: ArrayList<Message>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        messageList= ArrayList()
        val messageAdapter: MessageAdapter  = MessageAdapter(this,messageList)

        //RecyclerView
        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerMessages.adapter = messageAdapter

        //넘어온 데이터 변수에 담기// 이미지랑  저기에 추가
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance("https://boogimarket-000-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


        // 접속자 uid
        val senderUid = mAuth.currentUser?.uid

        //보낸이 방
        senderRoom = receiverUid + senderUid
        //받는이 방
        receiverRoom = senderUid + receiverUid

    //액션바에 상대방 이름 보여주기
       // supportActionBar?.title = receiverName
        binding.txtTitle.setText(receiverName)
    //메세지 전송 버튼 이벤트 -> db 저장 후 화면에 보여짐
        binding.btnSubmit.setOnClickListener {

            val message = binding.edtMessage.text.toString()

            val messageObject = Message(message,senderUid)

            //데이터 저장//chat 공간안에 보낸이 안에 message 안에 대화 내용 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //저장 성공시//chat 공간안에 받는이이 안에 message 안에 대화 내용 저장
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            //입력부분 초기화
            binding.edtMessage.setText("")

        }

        //메세지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages" )
            .addValueEventListener(object: ValueEventListener{
                //안의 데이터가 변경돠면 ondatachange가 실행
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshat in snapshot.children) {
                        val message = postSnapshat.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    //적용
                    messageAdapter.notifyDataSetChanged()

                }
                //오류 발생시 실행
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



    }
}