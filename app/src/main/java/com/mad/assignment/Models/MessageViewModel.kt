package com.mad.assignment.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mad.assignment.entity.Message
import com.mad.assignment.repository.ChatRepository
import com.mad.assignment.repository.MessageRepository

class MessageViewModel : ViewModel() {
    private val repository = MessageRepository()
    private val chatRepository = ChatRepository()

    fun sendMessage(chatId: String, message: Message) {
        chatRepository.updateChat(chatId, message)
        repository.sendMessage(chatId, message)
    }

    suspend fun getAllMessage(chatId: String): LiveData<List<Message>> {
        return repository.getAllMessages(chatId)
    }

}