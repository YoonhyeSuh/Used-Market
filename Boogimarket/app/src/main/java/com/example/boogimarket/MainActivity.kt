package com.example.boogimarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyViewModel>()

    private lateinit var soldCheckBox: CheckBox // 판매된 상품 체크 여부
    private lateinit var priceRadioGroup: RadioGroup // 가격순 정렬 라디오 버튼 그룹

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
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

}