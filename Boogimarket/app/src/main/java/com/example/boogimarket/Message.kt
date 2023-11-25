package com.example.boogimarket

import com.google.firebase.firestore.FieldValue

data class Message(
    var message: String?,
    var sendId: String?,
    var timestamp: Any? = null

){//기본 생성자
    constructor():this("","")
}
