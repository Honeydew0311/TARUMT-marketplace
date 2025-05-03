package com.mad.assignment.repository

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.entity.Chat
import com.mad.assignment.entity.Message
import com.mad.assignment.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ChatRepository {

    private val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

    suspend fun newChat(chat: Chat) {
//        val map = HashMap<String, Message>()
//        map["1234"] = Message("1234", null, null, "Hello", Timestamp.now(), null, null)
        val chatId = chatsRef.push().key ?: return
        chat.chatId = chatId
        val key = chatsRef.push().key ?: return
        chatsRef.child(key).setValue(chat).await()
    }

    suspend fun checkChatExists(buyer: User, seller: User): Boolean {
        try {
            val database: FirebaseDatabase = Firebase.database
            val chatsRef: DatabaseReference = database.getReference("chats")

            // Query chats node to find chats involving both buyer and seller
            val querySnapshot = chatsRef
                .orderByChild("buyer/email").equalTo(buyer.email)
                .get()
                .await()

            // Check if there is any chat with the specified seller
            querySnapshot.children.forEach { chatSnapshot ->
                val sellerEmail = chatSnapshot.child("seller/email").getValue(String::class.java)
                if (sellerEmail == seller.email) {
                    return true // Chat exists with the specified seller
                }
            }

            return false // No chat exists with the specified seller
        } catch (e: Exception) {
            // Handle exceptions
            Log.e("Firebase", "Error checking chat existence: ${e.message}")
            throw e
        }
    }

    suspend fun sendMessage(chatId: String, message: Message) {
        //generate a new key for the message
        val key = chatsRef.child(chatId).child("messages").push().key ?: return
        message.messageId = key
        chatsRef.child(chatId).child("messages").push().setValue(message).await()
    }

    suspend fun getChatList(): LiveData<List<Chat>>{
        return withContext(Dispatchers.IO){
            val chatsLiveData = MutableLiveData<List<Chat>>()
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = mutableListOf<Chat>()
                    for (childSnapshot in snapshot.children) {
                        val chat = childSnapshot.getValue(Chat::class.java)
                        chat?.let { chats.add(it) }
                    }
                    chatsLiveData.value = chats
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            }
            chatsRef.addValueEventListener(valueEventListener)
            // Remove the listener when no longer needed
            chatsRef.removeEventListener(valueEventListener)
            return@withContext chatsLiveData
        }
    }

     fun updateChat(chatId: String, msg: Message) {
        val chatRef = chatsRef.child(chatId)
        chatRef.child("lastChatTime").setValue(Timestamp.now())
         chatRef.child("lastChatMsg").setValue(msg.message)
    }

    fun getChatId(buyerId: String, sellerId: String) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("chats")

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { chatSnapshot ->
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat?.buyer?.get("email") == buyerId && chat.seller?.get("email") == sellerId) {
                        val chatId = chatSnapshot.key
                        // Do something with the chat ID (e.g., navigate to the chat fragment)
                        return
                    }
                }
                // Chat not found between the buyer and seller
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.e("getChatId", "Error getting chat ID", databaseError.toException())
            }
        })
    }
}