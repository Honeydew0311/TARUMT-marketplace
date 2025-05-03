package com.mad.assignment.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mad.assignment.entity.Transaction
import com.mad.assignment.repository.ProductRepository
import com.mad.assignment.repository.TransactionRepository

class TransactionViewModel : ViewModel(){

    private val repository = TransactionRepository()

    suspend fun getAllTransactions(): LiveData<List<Transaction>> {
        return repository.getAllTransactions()
    }

    suspend fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
    }

    suspend fun getTransaction(transactionID: String): Transaction? {
        return repository.getTransaction(transactionID)
    }

    suspend fun deleteTransaction(transactionID: String) {
        repository.deleteTransaction(transactionID)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }

}