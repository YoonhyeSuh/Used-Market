package com.example.boogimarket

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.boogimarket.databinding.ActivityMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment() {
    private lateinit var binding: ActivityMypageBinding

    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var userId: String? = null
    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser
        userId = currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userName = document.getString("name")
                        val userEmail = document.getString("email")
                        val userBirth = document.getString("birth")

                        binding.mypageName.text = userName
                        binding.mypageEmail.text = userEmail
                        binding.mypageBirth.text = userBirth
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "데이터 가져오기 실패", exception)
                }
        } else {
            Log.d(TAG, "false")
        }

        binding.logoutBtn.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

//        // ActionBar 설정
//        (activity as AppCompatActivity?)?.supportActionBar?.title = "MyPageFragment"
//
//        // 뒤로 가기 버튼 설정
//        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
