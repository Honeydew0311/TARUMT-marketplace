package com.mad.assignment.dao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mad.assignment.R
import com.mad.assignment.entity.Product
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductBuyAdapter(options: FirestoreRecyclerOptions<Product>, private val listener: OnItemClickListener) :
    FirestoreRecyclerAdapter<Product, ProductBuyAdapter.ViewHolder>(options) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val IVproductImage : ImageView = itemView.findViewById(R.id.IVproductImage)
        val TVproductName : TextView = itemView.findViewById(R.id.TVproductName)
        val TVproductPrice : TextView = itemView.findViewById(R.id.TVproductPrice)
        var productID : String = ""

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position), productID)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_view_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model : Product){
        // convert timestamp format
        val timestamp = model.productTimestamp
        val date = timestamp?.toDate()
        val formattedDateTime = date?.let { formatDateTime(it) }

        try{
            Picasso.get()
                .load(model.productImage)
                .into(holder.IVproductImage)
        }catch (e : Exception){
            println(e)
        }

        holder.productID = snapshots.getSnapshot(position).id
        holder.TVproductName.text = model.productName
        holder.TVproductPrice.text = model.productPrice.toString()

    }

    override fun updateOptions(newOptions: FirestoreRecyclerOptions<Product>) {
        super.updateOptions(newOptions)
        notifyDataSetChanged() // Notify adapter of data change
    }

    interface OnItemClickListener{
        fun onItemClick(product: Product, productID: String)
    }

    private fun formatDateTime(timestamp: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(timestamp)
    }
}