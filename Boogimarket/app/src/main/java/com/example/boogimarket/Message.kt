package com.example.boogimarket

data class Message(
    var message: String?,
    var sendId: String?
){//기본 생성자
    constructor():this("","")
}
