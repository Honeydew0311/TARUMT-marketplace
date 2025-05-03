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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mad.assignment.R
import com.mad.assignment.entity.Message
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(options: FirebaseRecyclerOptions<Message>, private val imgUrl: String) : FirebaseRecyclerAdapter<Message, MessageAdapter.ViewHolder>(options) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message : TextView? = itemView.findViewById(R.id.tvTxtMsg)
        val profilePic : ImageView? = itemView.findViewById(R.id.civChatroomImg)
        val time : TextView = itemView.findViewById(R.id.tvTxtDateTime)
        val img : ImageView? = itemView.findViewById(R.id.ivChatroomImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        when (viewType) {
            SENDER_VIEW_TYPE -> view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chatroom_sent_message, parent, false)
            RECEIVER_VIEW_TYPE -> view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chatroom_received_message, parent, false)
            // Add more cases for additional view types
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {

        if (holder.profilePic != null && imgUrl.isNotEmpty()) {
            try {
                Picasso.get()
                    .load(imgUrl)
                    .into(holder.profilePic)
            } catch (e: Exception) {
                Log.d("MessageAdapter", "onBindViewHolder: " + e.message)
            }
        }

        // Handle displaying images if the message has an imageUrl
        if (!model.fileUrl.isNullOrEmpty()) {
            holder.img?.visibility = View.VISIBLE
            Picasso.get()
                .load(model.fileUrl)
                .resize(500,500)
                .centerCrop()
                .into(holder.img)
            holder.message?.visibility = View.GONE
        } else {
            holder.img?.visibility = View.GONE
            holder.message?.text = model.message

        }

        val timestamp =
            if (model.messageTime == null) {
                Timestamp.now()
            } else {
                val map = model.messageTime as HashMap<String, Long>
                Timestamp(map["seconds"]!!, map["nanoseconds"]!!.toInt())
            }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(timestamp.toDate())
        holder.time.text = formattedTime
    }

    companion object {
        private const val SENDER_VIEW_TYPE = 1
        private const val RECEIVER_VIEW_TYPE = 2
    }

    override fun updateOptions(newOptions: FirebaseRecyclerOptions<Message>) {
        super.updateOptions(newOptions)
        notifyDataSetChanged() // Notify adapter of data change
    }

    override fun getItemViewType(position: Int): Int {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        return if (getItem(position).sender == userEmail) {
            1
        } else {
            2
        }
    }
}