package com.mad.assignment.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProductRepository {

    private val productCollection = FirebaseFirestore.getInstance().collection("products")

    suspend fun addProduct(product: Product) {
        val productID = productCollection.document().id
        val data = hashMapOf(
            "productID" to productID,
            "productImage" to product.productImage,
            "productName" to product.productName,
            "productQty" to product.productQty,
            "productPrice" to product.productPrice,
            "productType" to product.productType,
            "productBrand" to product.productBrand,
            "productCondition" to product.productCondition,
            "description" to product.description,
            "productCategory" to product.productCategory,
            "productModel" to product.productModel,
            "productWeight" to product.productWeight,
            "productSize" to product.productSize,
            "productWidth" to product.productWidth,
            "productHeight" to product.productHeight,
            "productDepth" to product.productDepth,
            "productStatus" to product.productStatus,
            "productTimestamp" to FieldValue.serverTimestamp(),
            "productQtyLeft" to product.productQtyLeft,
            "productSeller" to product.productSeller
        )

        productCollection.document(productID).set(data)
    }

    suspend fun getProduct(productID: String): Product? {
        return try {
            val document = productCollection.document(productID).get().await()
            val product = document.toObject(Product::class.java)
            product?.apply {
                // Set the unique key of the product
                this.productID = document.id
            }
            product
        } catch (e: Exception) {
            Log.e("GETPRODUCTERROR", "Error fetching product: $e")
            null
        }
    }

    suspend fun getAllPendingProducts(): LiveData<List<Product>>{
        return suspendCancellableCoroutine { continuation ->
            val productsLiveData = MutableLiveData<List<Product>>()

            val listener = productCollection
                .whereEqualTo("productStatus", "Pending")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                        return@addSnapshotListener
                    }

                    val products = mutableListOf<Product>()
                    for (doc in querySnapshot!!.documents) {
                        val product = doc.toObject(Product::class.java)
                        product?.let {
                            it.productID = doc.id
                            products.add(it)
                        }
                    }
                    productsLiveData.value = products

                    // Only resume the coroutine if it hasn't been resumed yet
                    if (!continuation.isCompleted) {
                        continuation.resume(productsLiveData)
                    }
                }

            // Cancel the listener when coroutine is cancelled
            continuation.invokeOnCancellation {
                listener.remove()
            }
        }
    }

    suspend fun getAllPostedProducts(): LiveData<List<Product>>{
        return suspendCancellableCoroutine { continuation ->
            val productsLiveData = MutableLiveData<List<Product>>()

            val listener = productCollection
                .whereEqualTo("productStatus", "Posted")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                        return@addSnapshotListener
                    }

                    val products = mutableListOf<Product>()
                    for (doc in querySnapshot!!.documents) {
                        val product = doc.toObject(Product::class.java)
                        product?.let {
                            it.productID = doc.id
                            products.add(it)
                        }
                    }
                    productsLiveData.value = products

                    // Only resume the coroutine if it hasn't been resumed yet
                    if (!continuation.isCompleted) {
                        continuation.resume(productsLiveData)
                    }
                }

            // Cancel the listener when coroutine is cancelled
            continuation.invokeOnCancellation {
                listener.remove()
            }
        }
    }

    suspend fun getAllProducts(): LiveData<List<Product>> {
        return suspendCancellableCoroutine { continuation ->
            val productsLiveData = MutableLiveData<List<Product>>()

            val listener = productCollection
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                        return@addSnapshotListener
                    }

                    val products = mutableListOf<Product>()
                    for (doc in querySnapshot!!.documents) {
                        val product = doc.toObject(Product::class.java)
                        product?.let {
                            it.productID = doc.id
                            products.add(it)
                        }
                    }
                    productsLiveData.value = products

                    // Only resume the coroutine if it hasn't been resumed yet
                    if (!continuation.isCompleted) {
                        continuation.resume(productsLiveData)
                    }
                }

            // Cancel the listener when coroutine is cancelled
            continuation.invokeOnCancellation {
                listener.remove()
            }
        }
    }

    suspend fun deleteProduct(productID: String) {
        productCollection.document(productID).delete().await()
    }

    suspend fun updateProduct(product: Product) {
        productCollection.document(product.productID!!).set(product).await()
    }

//    suspend fun getAllFavouriteProducts(): LiveData<List<Product>> {
//        val email = FirebaseAuth.getInstance().currentUser!!.email
//        val userRef = FirebaseFirestore.getInstance().collection("users").document(email!!)
//
//        val mutableLiveData = MutableLiveData<List<Product>>()
//
//    try {
//
//        val snapshot = userRef.get().await()
//        val user = snapshot.toObject(User::class.java)
//        val favList = user?.favList ?: emptyList()
//
//        if (favList.isNotEmpty()) {
//            val productsQuery = FirebaseFirestore.getInstance().collection("products").whereIn("productID", favList)
//            val productSnapshot = productsQuery.get().await()
//            val products = productSnapshot.toObjects(Product::class.java)
//            mutableLiveData.setValue(products)
//        } else {
//            mutableLiveData.setValue(emptyList())
//        }
//
//    } catch (exception: Exception) {
//        Log.w("Favourites", "Error retrieving favorite products", exception)
//    }
//
//        return mutableLiveData
//    }

    suspend fun getAllFavouriteProducts(): LiveData<List<Product>> {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val mutableLiveData = MutableLiveData<List<Product>>()

        if (email != null) {
            try {
                val userSnapshot = FirebaseFirestore.getInstance().collection("users")
                    .document(email).get().await()
                val favList = userSnapshot.toObject(User::class.java)?.favList ?: emptyList()

                val productsQuery = FirebaseFirestore.getInstance().collection("products")
                    .whereIn("productID", favList)
                val productSnapshot = productsQuery.get().await()
                val products = productSnapshot.toObjects(Product::class.java)

                mutableLiveData.postValue(products)
            } catch (exception: Exception) {
                // Handle exceptions
                mutableLiveData.postValue(emptyList()) // Return empty list on error
            }
        } else {
            // Return empty list if user is not authenticated
            mutableLiveData.postValue(emptyList())
        }

        return mutableLiveData
    }

}