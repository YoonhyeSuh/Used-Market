package com.example.boogimarket

//사용자 정보를 담을 클래스
data class User (
    var name: String,
    var birth: String,
    var email: String,
    var userId: String, //인증 데이터에 있는 uid
    var image: String
){
    constructor(): this("","","","","")
}