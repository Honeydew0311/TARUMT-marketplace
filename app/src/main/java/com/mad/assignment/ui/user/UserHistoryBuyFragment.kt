package com.mad.assignment.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.Models.TransactionViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.dao.HistoryBuyAdapter
import com.mad.assignment.databinding.FragmentUserHistoryBuyBinding
import com.mad.assignment.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UserHistoryBuyFragment : Fragment(R.layout.fragment_user_history_buy), HistoryBuyAdapter.OnItemClickListener {
    private var _binding: FragmentUserHistoryBuyBinding? = null
    private val binding get() = _binding!!
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val query: Query = FirebaseFirestore.getInstance().collection("Transaction").orderBy("transactionTime", Query.Direction.DESCENDING)
    private lateinit var adapter: HistoryBuyAdapter
    private val userViewModel: UserViewModel by viewModels()
    private val transactionViewModel : TransactionViewModel by viewModels()
    private lateinit var currentUser : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserHistoryBuyBinding.inflate(inflater, container, false)
        val view = binding.root

        val options = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(query, Transaction::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = HistoryBuyAdapter(options, this)
        binding.rvHistoryBuy.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryBuy.adapter = adapter
        binding.rvHistoryBuy.isClickable = true

        viewLifecycleOwner.lifecycleScope.launch {
            currentUser = userViewModel.getCurrentUser()?.email!!

            transactionViewModel.getAllTransactions().observe(viewLifecycleOwner, Observer { _ ->
                val newOptions = FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query
                    .orderBy("transactionTime", Query.Direction.DESCENDING)
                    .whereEqualTo("userEmail", currentUser), Transaction::class.java)
                .build()

                adapter.updateOptions(newOptions)
            })
        }

        /*binding.svHistoryBuy.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    FirebaseFirestore.getInstance().collection("Transaction")
                        .orderBy("transactionTime", Query.Direction.DESCENDING)
                        .whereEqualTo("userEmail", currentUser)
                        .whereGreaterThanOrEqualTo("product.productName",newText.trim().uppercase())
                        .whereLessThanOrEqualTo("product.productName",newText.trim().lowercase() + "~")
                }
                updateFirestoreRecyclerOptions(newQuery)
                return true
            }
        })*/

        updateFirestoreRecyclerOptions(query)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    private fun updateFirestoreRecyclerOptions(newQuery: Query) {
        val newOptions = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(newQuery, Transaction::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter.updateOptions(newOptions)
    }

    override fun onItemClick(transaction: Transaction) {
        val intent = Intent(requireContext(), UserHomeActivity::class.java)
        intent.putExtra("productID", transaction.product!!.productID)
        startActivity(intent)
    }

    override fun onButtonClick(transaction: Transaction) {
        transaction.deliveryStatus = "received"
        transaction.receivedTime = Timestamp.now()
        viewLifecycleOwner.lifecycleScope.launch {
            transactionViewModel.updateTransaction(transaction)
        }

    }
}