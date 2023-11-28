package com.example.boogimarket

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class UserAdapter(private val context: Context, private val userList: ArrayList<Pair<User, String>>):
RecyclerView.Adapter<UserAdapter.userViewHolder>(){
    //화면 설정

    //userlayout을 연결하는 기능을 구현
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        // 뷰 안에 챗 리스트 디자인 넣기
        val view: View = LayoutInflater.from(context).inflate(R.layout.chatlist_design, parent, false)

        return userViewHolder(view)//uset_layout을 넣은 뷰를 뷰홀더에 넣었다.
    }
    //UserList의 갯수를 돌려줌
    override fun getItemCount(): Int {
       return userList.size
    }
    //데이터 설정
    //data를 연결해주는 함수
    override fun onBindViewHolder(holder: userViewHolder, position: Int) {


        val currentUserPair = userList[position]
        val currentUser = currentUserPair.first // Retrieve the User object from the Pair
        val documentId = currentUserPair.second // Retrieve the document ID from the Pair

        holder.nameText.text = currentUser.name // Assuming 'name' is a field in the User object

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uId", currentUser.userId)
            intent.putExtra("documentId", documentId) // Pass the document ID to the next activity
            context.startActivity(intent)
        }
    }

    // 파라미터로 chatlist_design을 받았다
    class userViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //user_layout을 받아서 텍스트 뷰에 객체를 만듬
        val nameText: TextView = itemView.findViewById(R.id.name_text)//따라서 user_layout의 nametext에 접근 가능

    }
}
