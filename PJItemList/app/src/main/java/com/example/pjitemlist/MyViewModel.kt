package com.example.pjitemlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val icon: String, val firstName: String, val lastName: String, val price: String)

enum class ItemEvent { ADD, UPDATE, DELETE }

class MyViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    private val items = ArrayList<Item>()
    var itemsEvent = ItemEvent.ADD
    var itemsEventPos = -1

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    init {
        items.add(Item("casio", "카시오 시계", "서대문구 연희동", "30000원"))
        items.add(Item("person outline", "실내 자전거 나눔", "서교동","나눔"))
        items.add(Item("person pin", "연세대학교 학잠(사이즈 S)", "서교동", "20000원"))
        items.add(Item("pepero", "아몬드빼빼로", "신수동", "1000원"))
        items.add(Item("javabook", "명품 JAVA Programming", "대흥동", "35000원"))
        items.add(Item("osbook", "명품 운영체제", "성북구", "20000원"))
        itemsListData.value = items
    }

    fun getItem(pos: Int) =  items[pos]
    val itemsSize
        get() = items.size

    fun addItem(item: Item) {
        itemsEvent = ItemEvent.ADD
        itemsEventPos = itemsSize
        items.add(item)
        itemsListData.value = items // let the observer know the livedata changed
    }

    fun updateItem(pos: Int, item: Item) {
        itemsEvent = ItemEvent.UPDATE
        itemsEventPos = pos
        items[pos] = item
        itemsListData.value = items // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    fun deleteItem(pos: Int) {
        itemsEvent = ItemEvent.DELETE
        itemsEventPos = pos
        items.removeAt(pos)
        itemsListData.value = items
    }

    companion object {
        val icons = sortedMapOf(
            "casio" to R.drawable.casio,
            "person outline" to R.drawable.apple3,
            "person pin" to R.drawable.apple2,
            "pepero" to R.drawable.pepero,
            "osbook" to R.drawable.osbook,
            "javabook" to R.drawable.osbook
        )
    }
}