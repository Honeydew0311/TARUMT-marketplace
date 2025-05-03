package com.mad.assignment.entity

//Constructor for User class
data class User(
    val email: String? = null,
    var username: String? = null,
    var profileImgUrl: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var status: String? = null,
    var favList: List<String>? = null,
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "username" to username,
            "profileImgUrl" to profileImgUrl,
            "phone" to phone,
            "address" to address,
            "status" to status,
            "favList" to favList,
        )
    }
}