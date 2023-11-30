package com.example.boogimarket

// 게시물을 나타내는 데이터 클래스 생성
data class Post(
    val title: String?,
    val imageUrl: String?,
    val price: String?,
    val sold: Boolean?,
    val location: String?
)