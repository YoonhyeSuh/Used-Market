package com.example.boogimarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.boogimarket.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var mAuth: FirebaseAuth

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        firestore = FirebaseFirestore.getInstance()

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

        //액션바 이름 설정
        supportActionBar?.title = "RegisterActivity"
        //뒤로 가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //회원가입
    private fun signUp(email: String, password: String, name: String, birth: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //성공시 메인액티비티 이동
                    //성공시 메시지 실행
                    //인텐트 사용해서 메인 액티비티로 이동

                    val user = hashMapOf(
                        "name" to name,
                        "birth" to birth,
                        "email" to email,
                        "userId" to mAuth.currentUser?.uid // userId 추가
                    )
                    //사용자 정보 저장
                    addUserToFirestore(user)
                } else {
                    //실패시 실행
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun addUserToFirestore(user: HashMap<String, String?>){
        // "users"라는 컬렉션에 사용자 정보 추가
        firestore.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }
}