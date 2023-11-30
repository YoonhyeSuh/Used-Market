package com.example.boogimarket

import android.content.ContentValues
import android.os.Bundle
import android.os.PersistableBundle
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

class MySellActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyselllistBinding
    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userId: String? = null
    private var db = FirebaseFirestore.getInstance()

    // 게시물을 저장할 리스트 선언
    private var postList = ArrayList<Post>()
    private lateinit var adapter: MySellListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyselllistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser

        // RecyclerView 및 어댑터 초기화
        postList = ArrayList()
        adapter = MySellListAdapter(postList)
        binding.userSellListView.layoutManager = LinearLayoutManager(this)
        binding.userSellListView.adapter = adapter

        getPostsByUser()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun getPostsByUser() {
        userId = currentUser?.uid

        if(userId != null){

            db.collection("post")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val title = document.getString("title")
                        val price = document.getString("price")
                        val imageUrl = document.getString("imgUri")
                        val sold = document.getBoolean("sold") ?: false
                        val location = document.getString("location")
                        val post = Post(title, imageUrl, price, sold, location)
                        postList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "데이터 가져오기 실패", exception)
                }
        }else {
            Log.e(ContentValues.TAG, "Current user is null")
        }
    }
}
