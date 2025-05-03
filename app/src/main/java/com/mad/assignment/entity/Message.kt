package com.mad.assignment.entity

import com.google.firebase.Timestamp

data class Message(
    var messageId: String? = null,
    val sender: String? = null,
    val receiver: String? = null,
    val message: String? = null,
    val messageTime: Any? = null,
    val fileType: String? = null,
    val fileUrl: String? = null,
    val isSeen: Boolean = false
)
{
}