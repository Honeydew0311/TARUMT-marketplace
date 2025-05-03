package com.mad.assignment.dao

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.mad.assignment.R
import com.mad.assignment.entity.Chat
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(options: FirebaseRecyclerOptions<Chat>, private val listener: OnItemClickListener) :
    FirebaseRecyclerAdapter<Chat, ChatAdapter.ViewHolder>(options) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.civChat)
        val name: TextView = itemView.findViewById(R.id.tvChatName)
        var lastMsg : TextView = itemView.findViewById(R.id.tvChatText)
        var chatID : String = ""

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position), chatID)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model : Chat){
        holder.chatID = snapshots.getSnapshot(position).key.toString()

        if(model.seller?.get("email") == FirebaseAuth.getInstance().currentUser?.email){
            holder.name.text = model.buyer?.get("username").toString()
            try{
                Picasso.get()
                    .load(model.buyer?.get("profileImgUrl").toString())
                    .into(holder.img)
            }catch (e : Exception){
                Log.d("ChatAdapter", e.toString())
            }
        }else{
            holder.name.text = model.seller?.get("username").toString()
            try{
                Picasso.get()
                    .load(model.seller?.get("profileImgUrl").toString())
                    .into(holder.img)
            }catch (e : Exception){
                Log.d("ChatAdapter", e.toString())
            }
        }
        holder.lastMsg.text = model.lastChatMsg
    }

    override fun updateOptions(newOptions: FirebaseRecyclerOptions<Chat>) {
        super.updateOptions(newOptions)
        notifyDataSetChanged() // Notify adapter of data change
    }

    interface OnItemClickListener{
        fun onItemClick(chat: Chat, chatID: String)
    }
}
