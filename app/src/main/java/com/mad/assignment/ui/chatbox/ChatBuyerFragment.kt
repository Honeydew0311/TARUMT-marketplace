package com.mad.assignment.ui.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.mad.assignment.Models.ChatViewModel
import com.mad.assignment.dao.ChatAdapter
import com.mad.assignment.databinding.FragmentChatBuyerBinding
import com.mad.assignment.entity.Chat
import com.mad.assignment.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ChatBuyerFragment : Fragment(), ChatAdapter.OnItemClickListener {

    private var _binding: FragmentChatBuyerBinding? = null
    private val binding get() = _binding!!
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private var query: Query = FirebaseDatabase.getInstance().getReference("chats")
    private lateinit var adapter: ChatAdapter
    private val chatViewModel : ChatViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentChatBuyerBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up the RecyclerView
//        val query: Query = FirebaseDatabase.getInstance().getReference().child("chats")
        val options = FirebaseRecyclerOptions.Builder<Chat>()
            .setQuery(query.orderByChild("buyer/email").equalTo(FirebaseAuth.getInstance().currentUser!!.email.toString()), Chat::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ChatAdapter(options, this)
        binding.rvChatBuy.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatBuy.adapter = adapter
        binding.rvChatBuy.isClickable = true

        // obersve changes in the LiveData<List<Chat>> from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try{
                chatViewModel.getChatList().observe(viewLifecycleOwner, Observer { chatList ->
                    Log.d("ChatBuyerFragment", "Chat list updated: $chatList")
                    // Update FirestoreRecyclerOptions with the new list of products
                    val newOptions = FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(query.orderByChild("buyer/email").equalTo(FirebaseAuth.getInstance().currentUser!!.email.toString()), Chat::class.java)
                        .setLifecycleOwner(this@ChatBuyerFragment)
                        .build()

                    // Update the adapter with the new options
                    adapter.updateOptions(newOptions)
                })
            }catch (e: NoSuchElementException) {
                Log.e("ChatBuyerFragment", "NoSuchElementException: ${e.message}")
                e.printStackTrace()
            }
        }

        binding.svChatBuy.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update FirebaseRecyclerOptions with the new query
                val newQuery = if (newText.isNullOrEmpty()) {
                    query // Use original query if search query is empty
                } else {
                    // Create a new query based on the search query
                    query.orderByChild("seller/username")
                        .startAt(newText.trim().uppercase())
                        .endAt(newText.trim().uppercase() + "~")
                }
                updateFirebaseRecyclerOptions(newQuery)
                return true
            }
        })

        return view
    }

    override fun onItemClick(chat: Chat, chatID: String) {
        // Handle item click here
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("chatId",chatID)
        intent.putExtra("chatName", chat.seller!!["username"].toString())
        intent.putExtra("sellerImg", chat.seller!!["profileImgUrl"].toString())
        intent.putExtra("seller", chat.seller!!["email"].toString())
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    private fun updateFirebaseRecyclerOptions(newQuery: Query) {
        val newOptions = FirebaseRecyclerOptions.Builder<Chat>()
            .setQuery(newQuery, Chat::class.java)
            .build()
        adapter.updateOptions(newOptions)
    }


    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }
}