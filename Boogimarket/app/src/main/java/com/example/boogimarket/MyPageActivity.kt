package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityMypageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//1120 로그아웃과 액션바 코드 고쳐봤지만 안된다^^
class MyPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMypageBinding

    //아이콘 버튼 클릭시 로그아웃
    fun onIconClick() {
        Firebase.auth.signOut()
        val intent = Intent(this@MyPageActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //회원가입 정보 읽고 쓰기
        //하..^^
        //1120 코드 고쳐봤지만 로그아웃 액션바 안됨,,

        binding.logoutBtn.setOnClickListener {
            onIconClick()
        }

        //액션바 이름 설정
        supportActionBar?.title = "MyPageActivity"
        //뒤로 가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}