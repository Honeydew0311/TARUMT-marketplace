package com.mad.assignment.dao

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mad.assignment.R
import com.mad.assignment.entity.Transaction
import com.mad.assignment.entity.Product
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryBuyAdapter(options: FirestoreRecyclerOptions<Transaction>, private val listener: OnItemClickListener) :
    FirestoreRecyclerAdapter<Transaction, HistoryBuyAdapter.ViewHolder>(options) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.ivHistoryProductImg)
        val name: TextView = itemView.findViewById(R.id.tvHistoryProductName)
        val qty: TextView = itemView.findViewById(R.id.tvHistoryProductQty)
        val price: TextView = itemView.findViewById(R.id.tvHistoryProductPrice)
        val date: TextView = itemView.findViewById(R.id.tvHistoryProductDate)
        val secondDate: TextView = itemView.findViewById(R.id.tvHistoryProductDateSecondary)
        val btnAction: Button = itemView.findViewById(R.id.btnActionHistory)
        var transactionID : String = ""

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }

            btnAction.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onButtonClick(getItem(position))
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model : Transaction){
        // convert timestamp format
        holder.transactionID = model.transactionID!!
        Log.d("transid", holder.transactionID)
        val product : Product = model.product!!
        val timestamp = model.transactionTime
        val date = timestamp?.toDate()
        val formattedDateTime = date?.let { formatDateTime(it) }
        if (model.deliveryStatus.toString() == "notShippedOut") {
            holder.secondDate.visibility = View.GONE
            holder.btnAction.visibility = View.GONE
        } else if (model.deliveryStatus.toString() == "shippedOut") {
            holder.secondDate.visibility = View.GONE
            holder.btnAction.visibility = View.VISIBLE
            holder.btnAction.text = "Received"
        } else if (model.deliveryStatus.toString() == "received") {
            holder.secondDate.visibility = View.VISIBLE
            holder.btnAction.visibility = View.GONE
            val secondDate = (model.receivedTime!!.toDate()).let {formatDateTime(it)}
            holder.secondDate.text = "Date Received: $secondDate"
        }
        try{
            Picasso.get()
                .load(product.productImage)
                .into(holder.img)
        }catch (e : Exception){
            println(e)
        }
        holder.name.text = product.productName
        holder.qty.text = "Quantity: ${model.productCount}"
        holder.price.text = "Price: RM ${product.productPrice.toString()}"
        holder.date.text = "Date Bought: $formattedDateTime"
    }

    override fun updateOptions(newOptions: FirestoreRecyclerOptions<Transaction>) {
        super.updateOptions(newOptions)
        notifyDataSetChanged() // Notify adapter of data change
    }

    interface OnItemClickListener{
        fun onItemClick(transaction: Transaction)
        fun onButtonClick(transaction: Transaction)
    }

    private fun formatDateTime(timestamp: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }

}
