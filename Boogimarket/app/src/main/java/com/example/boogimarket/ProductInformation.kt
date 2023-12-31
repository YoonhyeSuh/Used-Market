package com.example.boogimarket

data class ProductInformation (
    var email : String?=null,
    var userId : String?=null,
    var imgUri :String?="https://firebasestorage.googleapis.com/v0/b/boogimarket-000.appspot.com/o/images%2Fchatlist.jpg?alt=media&token=61af7641-4613-4c3a-927b-f1a5714413c5", // 상품 이미지 Uri
    var title : String?=null, // 상품 이름
    var price : String?=null, // 상품 가격
    var location : String? =null, // 거래 장소
    var explain : String?=null, // 상품 상세 설명
    var sold : Boolean = false, // 상품 판매 상태 여부
    var documentID: String?=null,
    var timestamp: Any? = null

) {
}