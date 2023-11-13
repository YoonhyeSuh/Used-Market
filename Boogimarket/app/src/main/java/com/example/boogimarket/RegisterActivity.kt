package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var mAuth: FirebaseAuth

    //실시간 데이터 베이스
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        //db 초기화
        mDbRef = Firebase.database.reference

        //회원가입 버튼 회원가입 기능 구현
        binding.signUpBtn.setOnClickListener {

            //trim() 공백 제거
            //이메일, 비밀번호, 이름, 생일 저장
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val birth = binding.birth.text.toString().trim()

            //signUp 함수 호출 회원 정보 등록
            //이메일과 비밀번호 인자로 넘김
            signUp(email, password, name, birth)
        }
    }

    //회원가입
    private fun signUp(email: String, password: String, name: String, birth: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //성공시 메인액티비티 이동
                    //성공시 메시지 실행
                    //인텐트 사용해서 메인 액티비티로 이동
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)

                    //사용자 정보 저장
                    addUserToDatabase(name, birth, email, mAuth.currentUser?.uid!!)

                } else {
                    //실패시 실행
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, birth: String, email: String, userId: String ){
        mDbRef.child("user").child(userId).setValue(User(name, birth, email, userId))
    }
}