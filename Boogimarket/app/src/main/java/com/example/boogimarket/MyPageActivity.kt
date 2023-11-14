package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MyPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //회원가입 정보 읽고 쓰기
        //하..^^

        //메뉴바에서 navigation 연결을 아직 안해 확인 불가
        //아이콘 버튼 클릭시 로그아웃
        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //액션바 이름 설정
        supportActionBar?.title = "MyPageActivity"
        //뒤로 가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}