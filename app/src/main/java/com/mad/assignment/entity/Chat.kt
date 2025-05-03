package com.mad.assignment.entity

data class Chat(
    var chatId: String? = null,
    var messages: Map<String, Message>? = null,
    var buyer: Map<String, Any?>? = null,
    var seller: Map<String, Any?>? = null,
    var lastChatMsg : String? = null,
    var lastChatTime: Any? = null
) {
}
