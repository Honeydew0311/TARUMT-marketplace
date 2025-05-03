package com.mad.assignment.dao

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.entity.Product
import com.mad.assignment.repository.ProductRepository
import com.mad.assignment.repository.UserRepository
import com.squareup.picasso.Picasso

class FavouriteAdapter(options: FirestoreRecyclerOptions<Product>, private val listener: FavouriteAdapter.OnItemClickListener) :
    FirestoreRecyclerAdapter<Product, FavouriteAdapter.ViewHolder>(options) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img : ImageView = itemView.findViewById(R.id.ivFavProductImg)
        val name: TextView = itemView.findViewById(R.id.tvFavProductName)
        val price: TextView = itemView.findViewById(R.id.tvFavProductPrice)
        val favourite: ImageView = itemView.findViewById(R.id.ivFavFavourite)
        var productID : String = ""

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position), productID)
                }
            }

            // Assuming remove button is the notFavourite ImageView
            favourite.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Handle remove item click
                    listener.onRemoveItemClick(getItem(position), productID)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model : Product){
        holder.productID = snapshots.getSnapshot(position).id

        try{
            Picasso.get()
                .load(model.productImage)
                .into(holder.img)
        } catch (e : Exception){
            Log.d("FavouriteAdapter", e.toString())
        }

        holder.name.text = model.productName

        holder.price.text = model.productPrice.toString()
    }

    override fun updateOptions(newOptions: FirestoreRecyclerOptions<Product>) {
        super.updateOptions(newOptions)
        notifyDataSetChanged() // Notify adapter of data change
    }

    interface OnItemClickListener{
        fun onItemClick(product: Product, productID: String)
        fun onRemoveItemClick(product: Product, productId: String)
    }
}