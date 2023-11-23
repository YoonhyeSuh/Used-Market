package com.example.boogimarket

// imgUrl : 상품 이미지, product : 상품, location : 거래 장소, price : 상품 가격, sold : 상품 판매 상태 여부(isSold = true 시 sold out)

data class ProductInformation (
    var email : String?=null,
    var userId : String?=null,
    var imgUri :String?=null,
    var title : String?=null,
    var price : String?=null,
    var location : String? =null,
    var explain : String?=null,
    var sold : Boolean = false
) {
}