package com.example.boogimarket

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
private lateinit var mAuth: FirebaseAuth

class UserAdapter(private val context: Context, private val userList: ArrayList<Pair<User, String>>
,private val db: FirebaseFirestore ):
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
        val currentUser = currentUserPair.first
        val documentId = currentUserPair.second

        holder.nameText.text = currentUser.name
        db.collection("post").document(documentId).get()
            .addOnSuccessListener { result ->

                if (result != null) {
                    val setProduct =

                        result.getString("title")
                    val setPImage =
                        result.getString("imgUri")

                    holder.production.text =
                        setProduct
                    if(setPImage != null && setPImage != "") {
                        Picasso.get()
                            .load(setPImage) // Load image using Picasso
                            .into(holder.image) // Set loaded image to ImageView 'image' in your ViewHolder
                    }
                }
            }



        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uId", currentUser.userId)
            intent.putExtra("documentId", documentId) // Pass the document ID to the next activity
            context.startActivity(intent)
        }
//        //아이템 뷰를 꾹누르면 나가게 해줘
//        holder.itemView.setOnLongClickListener {
//            showLeaveChatConfirmation(currentUser, documentId)
//            true
//        }
    }

//    private fun showLeaveChatConfirmation(currentUser: User, documentId: String) {
//        androidx.appcompat.app.AlertDialog.Builder(context)
//            .setMessage("${currentUser.name}과의 채팅을 정말 나가시겠습니까?")
//            .setPositiveButton("나가기") { _, _ ->
//                leaveChatRoom(documentId)
//            }
//            .setNegativeButton("취소",null)
//            .show()
//    }


    // 파라미터로 chatlist_design을 받았다
    class userViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        //user_layout을 받아서 텍스트 뷰에 객체를 만듬
        val nameText: TextView = itemView.findViewById(R.id.name_text)//따라서 user_layout의 nametext에 접근 가능
        val production : TextView = itemView.findViewById(R.id.product_text)
        val image : ImageView = itemView.findViewById(R.id.product_image)

    }
}


