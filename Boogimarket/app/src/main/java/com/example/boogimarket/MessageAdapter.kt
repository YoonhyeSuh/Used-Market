package com.example.boogimarket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageAdapter(private val context: Context, private val messageList:ArrayList<Message> ) :
    //보낸쪽 뷰홀더와 받는 쪽 뷰홀더 만들기
    RecyclerView.Adapter<RecyclerView.ViewHolder>()//받는쪽 주는 쪽 두개이기에 이렇게 리사이클러 뷰홀더로 설정
    {

        //메세지에 따라 어떤 뷰홀더를 사용할지 정하기 위한 변수 두개
        private val receive = 1 // 받는 타입
        private val send = 2// 보내는 타입

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //화면 연결// getItemViewType 리턴 값이 여기로 들어감
            return if(viewType==1){//받는 화면
                val view: View = LayoutInflater.from(context).inflate(R.layout.chat_design_yours, parent, false)
                ReceiveViewHolder(view)
            }else{//보내는 화면
                val view: View = LayoutInflater.from(context).inflate(R.layout.chat_design_mine, parent, false)
                SendViewHolder(view)

            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            //현재 메시지
            val currentMessage = messageList[position]

            if(holder.javaClass== SendViewHolder::class.java){
                val viewHolder =holder as SendViewHolder
                viewHolder.sendMessage.text = currentMessage.message

            }else{
                val viewHolder =holder as ReceiveViewHolder
                viewHolder.receiveMessage.text = currentMessage.message
            }
        }

        override fun getItemCount(): Int {
            return messageList.size
        }

        override fun getItemViewType(position: Int): Int {//어떤 뷰홀더를 사용할지 기능 구현
           //메세지 값
            val currentMessage = messageList[position]
                // 접속자 uid와 current senduid를 비교
            return  if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
                send
            }else{
                receive
            }
        }

        class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val sendMessage: TextView =
                itemView.findViewById(R.id.send_txt_message)//보낸쪽 뷰를 전달 받아 객체로 저장


        }

        class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val receiveMessage: TextView = itemView.findViewById(R.id.recive_txt_message)
        }

    }
