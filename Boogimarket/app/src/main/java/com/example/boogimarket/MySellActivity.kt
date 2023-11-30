package com.example.boogimarket

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boogimarket.databinding.ActivityMyselllistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MySellActivity : AppCompatActivity(), MySellListAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMyselllistBinding
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userId: String? = null
    private var db = FirebaseFirestore.getInstance()

    // 게시물을 저장할 리스트 선언
    private var postList = ArrayList<ProductInformation>()
    private lateinit var adapter: MySellListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyselllistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser

        // RecyclerView 및 어댑터 초기화
        postList = ArrayList()
        adapter = MySellListAdapter(postList, this)
        binding.userSellListView.layoutManager = LinearLayoutManager(this)
        binding.userSellListView.adapter = adapter

        getPostsByUser()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(clickedItem: ProductInformation) {
        // DetailsActivity 시작을 위한 인텐트 생성
        val intent = Intent(this, DetailsActivity::class.java)

        // DetailsActivity에 데이터 전달
        intent.putExtra("item_title", clickedItem.title)
        intent.putExtra("item_location", clickedItem.location)
        intent.putExtra("item_price", clickedItem.price)
        intent.putExtra("item_explain", clickedItem.explain)
        intent.putExtra("item_sold", clickedItem.sold)
        intent.putExtra("item_userId", clickedItem.userId)
        intent.putExtra("item_imgUrl", clickedItem.imgUri)
        intent.putExtra("item_documentId", clickedItem.documentID)

        // DetailsActivity 시작
        startActivity(intent)
    }

    private fun getPostsByUser() {
        userId = currentUser?.uid

        if(userId != null) {
            db.collection("post")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // 사용자가 작성한 글이 없는 경우
                        Toast.makeText(this, "작성한 글이 없습니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in documents) {
                            val title = document.getString("title")
                            val price = document.getString("price")
                            val imageUrl = document.getString("imgUri")
                            val sold = document.getBoolean("sold") ?: false
                            val location = document.getString("location")
                            val email = document.getString("email")
                            val userId = document.getString("userId")
                            val documentId = document.getString("documentID")
                            val explain = document.getString("explain")
                            val post = ProductInformation(
                                email,
                                userId,
                                imageUrl,
                                title,
                                price,
                                location,
                                explain,
                                sold,
                                documentId
                            )
                            postList.add(post)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "데이터 가져오기 실패", exception)
                }
        } else {
            Log.e(ContentValues.TAG, "Current user is null")
        }
    }
}
