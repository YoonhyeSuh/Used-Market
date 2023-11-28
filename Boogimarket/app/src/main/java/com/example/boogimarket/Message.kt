package com.example.boogimarket


data class Message(
    var message: String?,
    var sendId: String?,
    var receiveId: String?,
    var timestamp: Any? = null

){//기본 생성자
    constructor():this("","","")
}
