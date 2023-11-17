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
    private val viewModel by viewModels<MyViewModel>()

    private lateinit var soldCheckBox: CheckBox // 판매된 상품 체크 여부
    private lateinit var priceRadioGroup: RadioGroup // 가격순 정렬 라디오 버튼 그룹

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

        }
        return true

    }

    // 판매된 상품 & 가격순 정렬 Filter 함수
    private fun updateItemsFilters() {
        val isSold = if (soldCheckBox.isChecked) false else null

        val sortPrice: Boolean? = when (priceRadioGroup.checkedRadioButtonId) {
            // R.id.lowPrice -> true // 낮은 가격순
            // R.id.highPrice -> false // 높은 가격순
            else -> null
        }

        //viewModel.updateItems(isSold, sortPrice)
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