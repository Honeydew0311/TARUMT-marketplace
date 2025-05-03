package com.mad.assignment.ui.user

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
import com.mad.assignment.dao.HistorySellAdapter
import com.mad.assignment.databinding.FragmentUserHistorySellBinding
import com.mad.assignment.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UserHistorySellFragment : Fragment(R.layout.fragment_user_history_sell), HistorySellAdapter.OnItemClickListener {
    private var _binding: FragmentUserHistorySellBinding? = null
    private val binding get() = _binding!!
    private var coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val query: Query = FirebaseFirestore.getInstance().collection("Transaction").orderBy("transactionTime", Query.Direction.DESCENDING)
    private lateinit var adapter: HistorySellAdapter
    private val userViewModel: UserViewModel by viewModels()
    private val transactionViewModel : TransactionViewModel by viewModels()
    private lateinit var currentUser : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserHistorySellBinding.inflate(inflater, container, false)
        val view = binding.root

        val options = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(query, Transaction::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = HistorySellAdapter(options, this)
        binding.rvHistorySell.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistorySell.adapter = adapter
        binding.rvHistorySell.isClickable = true

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

        /*binding.svHistorySell.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    query.startAt(newText)
                        .endAt(newText + "~")
                        .orderBy("transactionTime", Query.Direction.DESCENDING)
                        .whereEqualTo("userEmail", currentUser)
                }
                updateFirestoreRecyclerOptions(newQuery)
                return true
            }
        })
*/
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScopeIO.cancel()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_user, fragment, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    private fun updateFirestoreRecyclerOptions(newQuery: Query) {
        val newOptions = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(newQuery, Transaction::class.java)
            .build()
        adapter.updateOptions(newOptions)
    }

    override fun onItemClick(transaction: Transaction) {
        Toast.makeText(context, transaction.product!!.productID, Toast.LENGTH_SHORT).show()
    }

    override fun onButtonClick(transaction: Transaction) {
        transaction.deliveryStatus = "received"
        transaction.shippingTime = Timestamp.now()
        viewLifecycleOwner.lifecycleScope.launch {
            transactionViewModel.updateTransaction(transaction)
        }

    }
}