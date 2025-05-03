package com.mad.assignment.ui.product

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.R
import com.mad.assignment.dao.ProductAdapter
import com.mad.assignment.dao.ProductBuyAdapter
//import com.mad.assignment.dao.ProductAdapter
import com.mad.assignment.databinding.FragmentUserHomeBinding
import com.mad.assignment.entity.Product
import com.mad.assignment.ui.sell.SellCategoryActivity
import com.mad.assignment.ui.user.UserHomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BuyMenuFragment : Fragment(R.layout.fragment_user_home), ProductBuyAdapter.OnItemClickListener {

    private var _binding: FragmentUserHomeBinding? = null
    private val query: Query = FirebaseFirestore.getInstance().collection("products")
    private val productViewModel : ProductViewModel by viewModels()
    private lateinit var adapter: ProductBuyAdapter
    private lateinit var allCategory : List<Int>
    private var userHomeActivity : UserHomeActivity? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        userHomeActivity = activity as UserHomeActivity

        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()

        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.setHasFixedSize(true)
        adapter = ProductBuyAdapter(options, this)
        binding.recycleView.adapter = adapter

        binding.ivBuyFilter.setOnClickListener{
            if (binding.clFilterCategoryBuyMenu.isVisible)
            {
                binding.clFilterCategoryBuyMenu.visibility = View.GONE
            } else
            {
                binding.clFilterCategoryBuyMenu.visibility = View.VISIBLE
            }
        }

        allCategory = listOf()

        binding.btnResetBuyMenu.setOnClickListener{
            binding.cgBuyMenuCategory.clearCheck()
            allCategory = mutableListOf()
        }

        binding.svBuySearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Update FirestoreRecyclerOptions with the new query
                val newQuery = if (newText.isNullOrEmpty()) {
                    if (allCategory.isNotEmpty()){
                        query.orderBy("productName")
                            .whereIn("productCategory", allCategory)
                            .whereEqualTo("productStatus", "Posted")
                            .whereGreaterThan("productQtyLeft", 0)
                            .orderBy("productTimestamp", Query.Direction.ASCENDING)
                    } else {
                        query.orderBy("productName")
                            .whereEqualTo("productStatus", "Posted")
                            .whereGreaterThan("productQtyLeft", 0)
                            .orderBy("productTimestamp", Query.Direction.ASCENDING)
                    }
                } else {
                    if (allCategory.isNotEmpty()){
                        // Create a new query based on the search query
                        query.orderBy("productName")
                            .whereIn("productCategory", allCategory)
                            .whereEqualTo("productStatus", "Posted")
                            .whereGreaterThan("productQtyLeft", 0)
                            .startAt(newText)
                            .endAt(newText + "~")
                    }
                    else
                    {
                        // Create a new query based on the search query
                        query.orderBy("productName")
                            .whereEqualTo("productStatus", "Posted")
                            .whereGreaterThan("productQtyLeft", 0)
                            .startAt(newText)
                            .endAt(newText + "~")
                    }
                }
                updateFirestoreRecyclerOptions(newQuery)
                return true
            }
        })

        binding.btnApplyBuyMenu.setOnClickListener{
            allCategory = listOf()
            binding.cgBuyMenuCategory.checkedChipIds.forEach{cateogry ->
                if (binding.cgBuyMenuCategory.getChildAt(0).id == cateogry)
                {
                    allCategory += listOf(0)
                } else if (binding.cgBuyMenuCategory.getChildAt(1).id == cateogry)
                {
                    allCategory += listOf(1)
                } else if (binding.cgBuyMenuCategory.getChildAt(2).id == cateogry)
                {
                    allCategory += listOf(2)
                } else if (binding.cgBuyMenuCategory.getChildAt(3).id == cateogry)
                {
                    allCategory += listOf(3)
                }
            }
            val newOptions = FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereIn("productCategory", allCategory).whereEqualTo("productStatus", "Posted").whereGreaterThan("productQtyLeft", 0).orderBy("productTimestamp", Query.Direction.ASCENDING), Product::class.java)
                .build()
            adapter.updateOptions(newOptions)
        }


        // Observe changes in the LiveData<List<Product>> from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            productViewModel.getAllProducts().observe(viewLifecycleOwner, Observer { productList ->
                // Update FirestoreRecyclerOptions with the new list of products

                var newOptions : FirestoreRecyclerOptions<Product>

                if (userHomeActivity?.searchValue != null)
                {
                    newOptions = FirestoreRecyclerOptions.Builder<Product>()
                        .setQuery(query
                            .startAt(userHomeActivity?.searchValue)
                            .endAt(userHomeActivity?.searchValue + "~").whereEqualTo("productStatus", "Posted").whereGreaterThan("productQtyLeft", 0).orderBy("productTimestamp", Query.Direction.ASCENDING), Product::class.java)
                        .build()
                    userHomeActivity?.searchValue = null
                }else if (!allCategory.isEmpty()){
                    newOptions = FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query.whereIn("productCategory", allCategory).whereEqualTo("productStatus", "Posted").whereGreaterThan("productQtyLeft", 0).orderBy("productTimestamp", Query.Direction.ASCENDING), Product::class.java)
                    .build()
                }
                else
                {
                    newOptions = FirestoreRecyclerOptions.Builder<Product>()
                        .setQuery(query.whereEqualTo("productStatus", "Posted").whereGreaterThan("productQtyLeft", 0).orderBy("productTimestamp", Query.Direction.ASCENDING), Product::class.java)
                        .build()
                }
                // Update the adapter with the new options
                adapter.updateOptions(newOptions)
            })
        }


        return view
    }

    override fun onItemClick(product: Product, productID: String) {
        userHomeActivity?.productId = productID
        userHomeActivity?.productCount = 1
        binding.cgBuyMenuCategory.clearCheck()
        binding.svBuySearch.setQuery("", false)

        userHomeActivity?.hideNav()
        userHomeActivity?.addFragmentWithStack(ProductSpecificFragment(), requireActivity())
//        findNavController(requireView()).navigate(R.id.product)
        // Handle item click here
//        val intent = Intent(requireContext(), SellCategoryActivity::class.java)
//        intent.putExtra("productStatus", product.productStatus)
//        intent.putExtra("productID", productID)
//        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateFirestoreRecyclerOptions(newQuery: Query) {
        val newOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(newQuery, Product::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter.updateOptions(newOptions)
    }
}