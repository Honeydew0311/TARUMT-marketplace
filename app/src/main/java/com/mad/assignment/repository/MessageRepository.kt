package com.mad.assignment.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mad.assignment.entity.Chat
import com.mad.assignment.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageRepository {

    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.getReference("chats")

    fun sendMessage(chatId: String, message: Message) {
        val messageId = chatsRef.child(chatId).child("messages").push().key
        messageId?.let {
            chatsRef.child(chatId).child("messages").child(it).setValue(message)
                .addOnSuccessListener {
                    // Data was successfully written
                    // Handle success if needed
                }
                .addOnFailureListener { e ->
                    // Handle error writing data
                }
        }
    }

    // get messageId from the database
    suspend fun getMessageId(chatId: String): String {
        return chatsRef.child(chatId).child("messages").push().key ?: ""
    }

    suspend fun getAllMessages(chatId: String): LiveData<List<Message>> {
        return suspendCoroutine { continuation ->
            val messagesLiveData = MutableLiveData<List<Message>>()
            val messagesRef = chatsRef.child(chatId).child("messages")
            messagesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        message?.let { messages.add(it) }
                    }
                    messagesLiveData.postValue(messages)
                    Log.d("MessageRepository", "Fetched messages: $messages")
//                    continuation.resume(messagesLiveData)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e("MessageRepository", "Error fetching messages: $error")
                    messagesLiveData.postValue(emptyList())
//                    continuation.resume(messagesLiveData)
                }
            })
        }
    }
}