package com.mad.assignment.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mad.assignment.entity.Chat
import com.mad.assignment.entity.Message
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.User
import com.mad.assignment.repository.ChatRepository

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    suspend fun newChat(chat: Chat) {
        repository.newChat(chat)
    }

    suspend fun sendMessage(chatId: String, message: Message) {
        repository.sendMessage(chatId, message)
    }

    suspend fun getChatList(): LiveData<List<Chat>> {
        return repository.getChatList()
    }

    suspend fun checkChatExists(buyer: User, seller: User): Boolean{
        return repository.checkChatExists(buyer, seller)
    }

    fun getChatId(buyerId: String, sellerId: String){
        return getChatId(buyerId, sellerId)
    }

}