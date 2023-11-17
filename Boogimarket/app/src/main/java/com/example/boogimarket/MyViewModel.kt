package com.example.boogimarket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// icon : 상품 이미지, product : 상품, location : 거래 장소, price : 상품 가격, isSold : 상품 판매 상태 여부(isSold = true 시 sold out)
// 판매자 추가 필요
data class Item(val icon: String, val product: String, val location: String, val price: String, val sold: Boolean = false)

class MyViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    private val items = ArrayList<Item>()

    // itemsListData의 현재 상태를 반환하는 함수
    fun getCurrentItems() = itemsListData.value ?: ArrayList()

    // 판매된 상품 + 가격순 정렬 함수
    fun updateItems(isSold: Boolean? = null, sortByPrice: Boolean? = null) {
        var updatedItems: List<Item> = items // 상태정보가 변경된 상품 데이터를 담을 새 리스트 생성

        // 판매된 상품 필터 적용
        isSold?.let { state ->
            updatedItems = updatedItems.filter { it.sold == state } // isSold 값에 따라 filtering
        }

        // 가격순 정렬
        sortByPrice?.let { state ->
            updatedItems = if (state)
                updatedItems.sortedBy { it.price.toInt() } // state값이 true인 경우 낮은 가격순 정렬
            else
                updatedItems.sortedByDescending { it.price.toInt() } // state값이 false인 경우 높은 가격순 정렬
        }

        itemsListData.value = ArrayList(updatedItems)
    }

    /*
    fun getItem(pos: Int) = items[pos]
    private val itemsSize
        get() = items.size
    */
}