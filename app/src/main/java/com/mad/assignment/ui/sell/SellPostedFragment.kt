package com.mad.assignment.ui.sell

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.dao.ProductAdapter
import com.mad.assignment.databinding.FragmentSellPostedBinding
import com.mad.assignment.entity.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SellPostedFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private var _binding: FragmentSellPostedBinding? = null
    private val binding get() = _binding!!
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val email = FirebaseAuth.getInstance().currentUser!!.email
    private val query: Query = FirebaseFirestore.getInstance().collection("products")
        .whereEqualTo("productStatus", "Posted")
        .whereEqualTo("productSeller", email)
        .orderBy("productTimestamp", Query.Direction.ASCENDING)
    private lateinit var adapter: ProductAdapter
    private val productViewModel : ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSellPostedBinding.inflate(inflater, container, false)
        val view = binding.root

        // declare firebaseRecyclerOptions
        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ProductAdapter(options, this)
        binding.rvSellPosted.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSellPosted.adapter = adapter
        binding.rvSellPosted.isClickable = true


        // Observe changes in the LiveData<List<Product>> from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            productViewModel.getAllPostedProducts().observe(viewLifecycleOwner, Observer { productList ->
                // Update FirestoreRecyclerOptions with the new list of products
                val newOptions = FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product::class.java)
                    .build()

                // Update the adapter with the new options
                adapter.updateOptions(newOptions)
            })
        }

        binding.svSellPosted.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Update FirestoreRecyclerOptions with the new query
                val newQuery = if (newText.isNullOrEmpty()) {
                    query // Use original query if search query is empty
                } else {
                    // Create a new query based on the search query
                    FirebaseFirestore.getInstance().collection("products")
                        .whereEqualTo("productStatus", "Posted")
                        .whereEqualTo("productSeller", email)
                        .orderBy("productName")
                        .startAt(newText)
                        .endAt(newText + "~")
                }
                updateFirestoreRecyclerOptions(newQuery)
                return true
            }
        })

        return view
    }

    override fun onItemClick(product: Product, productID: String) {
        // Handle item click here
        val intent = Intent(requireContext(), SellCategoryActivity::class.java)
        intent.putExtra("productStatus", product.productStatus)
        intent.putExtra("productID", productID)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    private fun updateFirestoreRecyclerOptions(newQuery: Query) {
        val newOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(newQuery, Product::class.java)
            .build()
        adapter.updateOptions(newOptions)
    }
}