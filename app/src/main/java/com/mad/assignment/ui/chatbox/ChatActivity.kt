package com.mad.assignment.ui.chatbox

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.mad.assignment.Models.MessageViewModel
import com.mad.assignment.dao.MessageAdapter
import com.mad.assignment.databinding.ActivityChatBinding
import com.mad.assignment.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var adapter: MessageAdapter
    private val messageViewModel : MessageViewModel by viewModels()
    private var imageUrl: String? = null
    private var seller: String? = null
    private var buyer: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private var mimeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        getSupportActionBar()?.hide()
        setContentView(binding.root)

        binding.tvChatroomName.text = intent.getStringExtra("chatName")
        buyer = FirebaseAuth.getInstance().currentUser?.email ?: ""
        seller = intent.getStringExtra("seller")
        val imgUrl = intent.getStringExtra("sellerImg")
        val chatId = intent.getStringExtra("chatId")?:""
        val query = FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages")

        // Set up the RecyclerView
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = MessageAdapter(options, imgUrl!!)
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)
        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.isClickable = true

        // Scroll to the bottom when data changes
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvChatroom.scrollToPosition(adapter.itemCount - 1)
            }
        })

        // observe changes in the LiveData<List<Chat>> from the ViewModel
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                messageViewModel.getAllMessage(chatId)
                    .observe(this@ChatActivity, Observer { messages ->
                        // Update FirebaseRecyclerOptions with the new list of products
                        val newOptions = FirebaseRecyclerOptions.Builder<Message>()
                            .setQuery(query.limitToLast(10), Message::class.java)
                            .setLifecycleOwner(this@ChatActivity)
                            .build()

                        // Update the adapter with the new options
                        adapter.updateOptions(newOptions)
                    })
            }catch (e: NoSuchElementException) {
                Log.e("ChatActivity", "NoSuchElementException: ${e.message}")
                e.printStackTrace()
            }
        }

        binding.ibBackBtn.setOnClickListener {
            finish()
        }

        binding.ibUploadImg.setOnClickListener{
            try {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }catch (e:Exception){
                Toast.makeText(this, "Fail to select file", Toast.LENGTH_SHORT).show()

            }
        }

        binding.ibSend.setOnClickListener {
            val message = binding.ettChatroomMsg.text.toString().trim()
            if (message.isNotEmpty() || imageUrl != null) {
                val messageObj = Message(
                    null,
                    buyer,
                    seller,
                    message,
                    Timestamp.now(),
                    null,
                    null,
                    false
                )
                messageViewModel.sendMessage(chatId, messageObj)
                binding.ettChatroomMsg.text.clear()
            } else {
                Toast.makeText(this, "Cannot send an empty message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data

            // get the file type
            mimeType = selectedImageUri?.let { contentResolver.getType(it) }

            // Now you can upload the selected image to Firebase Storage
            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(selectedImageUri)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageName = UUID.randomUUID().toString() + ".jpg"
        val chatId = intent.getStringExtra("chatId")?:""
        val imageRef = storageRef.child("chat_images/$chatId/$imageName")

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            Log.d(ContentValues.TAG, "Image uploaded successfully")
            // Get the download URL of the uploaded image
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    Log.d(ContentValues.TAG, "Image uploaded successfully: $imageUrl")

                    val message = binding.ettChatroomMsg.text.toString().trim()
                    val newMsg = "${seller} has sent a ${mimeType}"
                    val messageObj = Message(
                        null,
                        buyer,
                        seller,
                        message,
                        Timestamp.now(),
                        mimeType,
                        imageUrl,
                        false
                    )
                    messageViewModel.sendMessage(chatId, messageObj)
                    binding.ettChatroomMsg.text.clear()
                    imageUrl = null
                    mimeType = null
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Failed to get download URL: ${exception.message}")
                }
        }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                Toast.makeText(this, "Failed to upload image to Firebase Storage： ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Failed to upload image to Firebase Storage: ${exception.message}")
            }
    }
}
