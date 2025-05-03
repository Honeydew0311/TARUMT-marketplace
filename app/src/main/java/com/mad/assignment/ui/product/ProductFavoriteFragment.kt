package com.mad.assignment.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.dao.FavouriteAdapter
import com.mad.assignment.dao.ProductAdapter
import com.mad.assignment.databinding.FragmentProductFavoriteBinding
import com.mad.assignment.entity.Product
import com.mad.assignment.ui.sell.SellCategoryActivity
import com.mad.assignment.ui.user.UserHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Collections

class ProductFavoriteFragment : Fragment(), FavouriteAdapter.OnItemClickListener {
    private var _binding: FragmentProductFavoriteBinding? = null
    private val binding get() = _binding!!
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val query: Query = FirebaseFirestore.getInstance().collection("products")
    private lateinit var adapter: FavouriteAdapter
    private val productViewModel : ProductViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root

        // declare firebaseRecyclerOptions
        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = FavouriteAdapter(options, this)
        binding.rvFav.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFav.adapter = adapter
        binding.rvFav.isClickable = true

        // Observe changes in the LiveData<List<Product>> from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                productViewModel.getAllFavouriteProducts().observe(viewLifecycleOwner, Observer { favList ->
                    Log.d("ProductFavoriteFragment", "FavList: $favList")
                    // Update FirestoreRecyclerOptions with the new list of products
                    if (favList.isNotEmpty()) {
                        // Update FirestoreRecyclerOptions only if favList is not empty
                        binding.rvFav.visibility = View.VISIBLE
                        val newOptions = FirestoreRecyclerOptions.Builder<Product>()
                            .setQuery(query.whereIn("productID", favList.map { it.productID }), Product::class.java)
                            .build()

                        adapter.updateOptions(newOptions)
                    } else {
                        // Handle empty list case (e.g., display empty state, notify user)
                        binding.rvFav.visibility = View.GONE
                        Toast.makeText(requireContext(), "No favourite products", Toast.LENGTH_SHORT).show()
                        Log.d("ProductFavoriteFragment", "FavList is empty")

//                        clearAdapter(adapter)
                    }
                })
        }
        return view
    }

    override fun onItemClick(product: Product, productID: String) {
        // Handle item click here
        val intent = Intent(requireContext(), UserHomeActivity::class.java)
        intent.putExtra("productID", productID)
        startActivity(intent)
    }

    override fun onRemoveItemClick(product: Product, productId: String) {
        // Call the method to remove the product from the favorite list via repository
        userViewModel.removeFromFavourite(productId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    fun clearAdapter(adapter: FavouriteAdapter) {
        val emptyProductIds = listOf<String>() // Empty list of product IDs
        val emptyOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query.whereIn("productID", emptyProductIds), Product::class.java)
            .build()
        adapter.updateOptions(emptyOptions)
    }

}