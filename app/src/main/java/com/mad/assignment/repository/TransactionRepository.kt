package com.mad.assignment.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mad.assignment.entity.Transaction
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TransactionRepository {
    private val transactionCollection = FirebaseFirestore.getInstance().collection("Transaction")

    suspend fun addTransaction(transaction: Transaction) {
        val transactionID = transactionCollection.document().id
        Log.d("transactionID", transactionID)
        val data = hashMapOf(
            "transactionID" to transactionID,
            "deliveryAddress" to transaction.deliveryAddress,
            "deliveryStatus" to transaction.deliveryStatus,
            "merchandiseSubtotal" to transaction.merchandiseSubtotal,
            "paymentMethod" to transaction.paymentMethod,
            "productCount" to transaction.productCount,
            "product" to transaction.product,
            "shippingOption" to transaction.shippingOption,
            "shippingSST" to transaction.shippingSST,
            "shippingSubTotal" to transaction.shippingSubtotal,
            "totalAmount" to transaction.totalAmount,
            "transactionTime" to FieldValue.serverTimestamp(),
            "shippingTime" to null,
            "receivedTime" to null,
            "userEmail" to transaction.userEmail
        )
        Log.d("asdfasdf", data["transactionID"].toString())
        transactionCollection.document(transactionID).set(data)
    }

    suspend fun getTransaction(transactionID: String): Transaction? {
        return try {
            val document = transactionCollection.document(transactionID).get().await()
            val transaction = document.toObject(Transaction::class.java)
            transaction?.apply {
                // Set the unique key of the product
                this.transactionID = document.id
            }
            transaction
        } catch (e: Exception) {
            Log.e("GETTRANSACTIONERROR", "Error fetching product: $e")
            null
        }
    }

//    suspend fun getAllPendingTransaction(): LiveData<List<Transaction>> {
//        return suspendCancellableCoroutine { continuation ->
//            val transactionsLiveData = MutableLiveData<List<Transaction>>()
//
//            val listener = transactionCollection
//                .whereEqualTo("transactionStatus", "Pending")
//                .addSnapshotListener { querySnapshot, error ->
//                    if (error != null) {
//                        continuation.resumeWithException(error)
//                        return@addSnapshotListener
//                    }
//
//                    val transactions = mutableListOf<Transaction>()
//                    for (doc in querySnapshot!!.documents) {
//                        val transaction = doc.toObject(Transaction::class.java)
//                        transaction?.let {
//                            it.transactionID = doc.id
//                            transactions.add(it)
//                        }
//                    }
//                    transactionsLiveData.value = transactions
//
//                    // Only resume the coroutine if it hasn't been resumed yet
//                    if (!continuation.isCompleted) {
//                        continuation.resume(transactionsLiveData)
//                    }
//                }
//
//            // Cancel the listener when coroutine is cancelled
//            continuation.invokeOnCancellation {
//                listener.remove()
//            }
//        }
//    }

    suspend fun getAllTransactions(): LiveData<List<Transaction>> {
        return suspendCancellableCoroutine { continuation ->
            val transactionsLiveData = MutableLiveData<List<Transaction>>()

            val listener = transactionCollection
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                        return@addSnapshotListener
                    }

                    val transactions = mutableListOf<Transaction>()
                    for (doc in querySnapshot!!.documents) {
                        val transaction = doc.toObject(Transaction::class.java)
                        transaction?.let {
                            it.transactionID = doc.id
                            transactions.add(it)
                        }
                    }
                    transactionsLiveData.value = transactions

                    // Only resume the coroutine if it hasn't been resumed yet
                    if (!continuation.isCompleted) {
                        continuation.resume(transactionsLiveData)
                    }
                }

            // Cancel the listener when coroutine is cancelled
            continuation.invokeOnCancellation {
                listener.remove()
            }
        }
    }

    suspend fun deleteTransaction(transactionID: String) {
        transactionCollection.document(transactionID).delete().await()
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionCollection.document(transaction.transactionID!!).set(transaction).await()
    }
}
