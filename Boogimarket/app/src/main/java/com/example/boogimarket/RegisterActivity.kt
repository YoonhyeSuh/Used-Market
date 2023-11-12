package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        //회원가입 버튼 회원가입 기능 구현
        binding.signUpBtn.setOnClickListener {

            //trim() 공백 제거
            //이메일, 비밀번호, 이름, 생일 저장
            val email = binding.email.text.toString().trim()
            val pw = binding.pw.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val birth = binding.birth.text.toString().trim()

            //signUp 함수 호출 회원 정보 등록
            //이메일과 비밀번호 인자로 넘김
            signUp(email, pw, name, birth)
        }
    }

    private fun signUp(email: String, pw: String, name: String, birth: String){
        mAuth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //성공시 메인액티비티 이동
                    //성공시 메시지 실행
                    //인텐트 사용해서 메인 액티비티로 이동
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    //실패시 실행
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}