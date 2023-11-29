package com.example.boogimarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // 프래그먼트 인스턴스 생성
        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val myPageFragment = MyPageFragment()

        // Bottom Navigation 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu_bottom_navigation)

        // 초기 프래그먼트를 HomeFragment로 설정
        replaceFragment(homeFragment)

        // Bottom Navigation 아이템 선택 처리
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(homeFragment) // HomeFragment로 전환
                R.id.chat -> replaceFragment(chatFragment) // ChatFragment로 전환
                R.id.myInfo -> replaceFragment(myPageFragment) // MyPageFragment로 전환
            }
            true
        }
    }

    // 현재 프래그먼트를 지정된 프래그먼트로 교체
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.meun_frame_layout, fragment)
                commit()
            }
    }
}