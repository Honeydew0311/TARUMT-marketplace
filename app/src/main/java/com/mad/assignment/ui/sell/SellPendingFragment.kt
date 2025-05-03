package com.mad.assignment.ui.sell

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.R
import com.mad.assignment.dao.ProductAdapter
import com.mad.assignment.databinding.FragmentSellPendingBinding
import com.mad.assignment.entity.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class SellPendingFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private var _binding: FragmentSellPendingBinding? = null
    private val binding get() = _binding!!
    private var isExpanded = false
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val email = FirebaseAuth.getInstance().currentUser!!.email
    private val query: Query = FirebaseFirestore.getInstance().collection("products")
        .whereEqualTo("productStatus", "Pending")
        .whereEqualTo("productSeller", email)
        .orderBy("productTimestamp", Query.Direction.ASCENDING)
    private lateinit var adapter: ProductAdapter
    private val productViewModel : ProductViewModel by viewModels()


    private val fromBottomFabAnim : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_fab)
    }
    private val rotateClockWiseFabAnim : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clockwise)
    }
    private val rotateAntiClockWiseFabAnim : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anticlockwise)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSellPendingBinding.inflate(inflater, container, false)
        val view = binding.root

        // declare firebaseRecyclerOptions
        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ProductAdapter(options, this)
        binding.rvSellPending.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSellPending.adapter = adapter
        binding.rvSellPending.isClickable = true


        // Observe changes in the LiveData<List<Product>> from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            productViewModel.getAllPendingProducts().observe(viewLifecycleOwner, Observer { productList ->
                // Update FirestoreRecyclerOptions with the new list of products
                val newOptions = FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product::class.java)
                    .build()

                // Update the adapter with the new options
                adapter.updateOptions(newOptions)
            })
        }

        binding.svSellPending.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                        .whereEqualTo("productStatus", "Pending")
                        .whereEqualTo("productSeller", email)
                        .orderBy("productName")
                        .startAt(newText)
                        .endAt(newText + "~")
                }
                updateFirestoreRecyclerOptions(newQuery)
                return true
            }
        })



        binding.vSellPending.setOnClickListener {
            // Handle sell pending click
            shrinkFab()
        }

        binding.fabAdd.setOnClickListener {
            // Handle FAB click
            if(isExpanded){
                shrinkFab()
            } else {
                expandFab()
            }
        }

        binding.fabElectronics.setOnClickListener {
            // Handle electronics FAB click
            shrinkFab()
            val intent = Intent(requireContext(), SellCategoryActivity::class.java)
            intent.putExtra("category", 0)
            startActivity(intent)
        }
        binding.fabHousing.setOnClickListener {
            // Handle electronics FAB click
            shrinkFab()
            val intent = Intent(requireContext(), SellCategoryActivity::class.java)
            intent.putExtra("category", 1)
            startActivity(intent)
        }
        binding.fabEducation.setOnClickListener {
            // Handle electronics FAB click
            shrinkFab()
            val intent = Intent(requireContext(), SellCategoryActivity::class.java)
            intent.putExtra("category", 2)
            startActivity(intent)
        }
        binding.fabSports.setOnClickListener {
            // Handle electronics FAB click
            shrinkFab()
            val intent = Intent(requireContext(), SellCategoryActivity::class.java)
            intent.putExtra("category", 3)
            startActivity(intent)
        }

        return view
    }

    override fun onItemClick(product: Product, productID: String) {
        // Handle item click here
        val intent = Intent(requireContext(), SellCategoryActivity::class.java)
        intent.putExtra("productStatus", product.productStatus)
        intent.putExtra("productID",productID)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    private fun shrinkFab() {
        // Shrink the FAB
        binding.vSellPending.visibility = View.GONE
        binding.fabAdd.startAnimation(rotateAntiClockWiseFabAnim)
        binding.fabElectronics.startAnimation(toBottomFabAnim)
        binding.fabEducation.startAnimation(toBottomFabAnim)
        binding.fabHousing.startAnimation(toBottomFabAnim)
        binding.fabSports.startAnimation(toBottomFabAnim)

        binding.tvElectronics.visibility = View.GONE
        binding.tvEducation.visibility = View.GONE
        binding.tvHousing.visibility = View.GONE
        binding.tvSports.visibility = View.GONE

        isExpanded = !isExpanded
    }

    private fun expandFab() {
        // Expand the FAB
        binding.vSellPending.visibility = View.VISIBLE
        binding.fabAdd.startAnimation(rotateClockWiseFabAnim)
        binding.fabElectronics.startAnimation(fromBottomFabAnim)
        binding.fabEducation.startAnimation(fromBottomFabAnim)
        binding.fabHousing.startAnimation(fromBottomFabAnim)
        binding.fabSports.startAnimation(fromBottomFabAnim)

        binding.tvElectronics.visibility = View.VISIBLE
        binding.tvEducation.visibility = View.VISIBLE
        binding.tvHousing.visibility = View.VISIBLE
        binding.tvSports.visibility = View.VISIBLE

        isExpanded = !isExpanded
    }

    private fun updateFirestoreRecyclerOptions(newQuery: Query) {
        val newOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(newQuery, Product::class.java)
            .build()
        adapter.updateOptions(newOptions)
    }
}