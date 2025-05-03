package com.mad.assignment.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.Transaction
import com.mad.assignment.entity.User
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class UserRepository {

    private val userCollection = FirebaseFirestore.getInstance().collection("users")
    private val productCollection = FirebaseFirestore.getInstance().collection("products")
    private val email = FirebaseAuth.getInstance().currentUser?.email

    fun addUser(email : String, name : String, profilePic : String) {
        // validate whether user already exists
        userCollection.document(email).get().addOnSuccessListener { it ->
            if (it.exists()) {
                Log.d(ContentValues.TAG, "User already exists")
            } else {
                // Add user to database
                val user = hashMapOf(
                    "email" to email,
                    "username" to name,
                    "profileImgUrl" to profilePic,
                    "phone" to "",
                    "address" to "",
                    "status" to "",
                    "favList" to listOf(""),
                )

                userCollection.document(email).set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }
            }
        }
    }

    suspend fun getCurrentUser(): User? {
        return suspendCancellableCoroutine { continuation ->
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // User is signed in, get their email
                val email = currentUser.email ?: ""
                // Retrieve user data from Firestore
                userCollection.document(email).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val user = documentSnapshot.toObject(User::class.java)
                        continuation.resume(user)
                    }
                    .addOnFailureListener { e ->
                        Log.e("GETUSERERROR", "Error fetching user: $e")
                        continuation.resume(null)
                    }
            }
        }
    }

    // get user by email
    suspend fun getUser(email: String): User? {
        return try {
            // Retrieve user document from Firestore
            val documentSnapshot = userCollection.document(email).get().await()

            // Convert document snapshot to User object
            val user = documentSnapshot.toObject(User::class.java)
            // Return the User object
            user
        } catch (e: Exception) {
            // Log any errors that occur during the retrieval process
            Log.e("GETUSERERROR", "Error fetching user: $e")
            null
        }
    }

    // Adding a favorite product
    fun addToFavourite(productId: String) {
        userCollection.document(email!!).update("favList", FieldValue.arrayUnion(productId))
            .addOnSuccessListener { Log.d("Favourites", "Product added to favorites") }
            .addOnFailureListener { Log.w("Favourites", "Error adding favorite", it) }
    }

    fun removeFromFavourites(productId: String) {
        userCollection.document(email!!).update("favList", FieldValue.arrayRemove(productId))
            .addOnSuccessListener { Log.d("Favourites", "Product removed from favorites") }
            .addOnFailureListener { Log.w("Favourites", "Error removing favorite", it) }
    }

    fun getFavouriteProducts(): LiveData<List<Product>> {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = userCollection.document(userId)

        val mutableLiveData = MutableLiveData<List<Product>>()

        userRef.get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                val favList = user?.favList ?: emptyList()

                val productsQuery = productCollection.whereIn("productID", favList)
                productsQuery.get().addOnSuccessListener { productSnapshot ->
                    val products = productSnapshot.toObjects(Product::class.java)
                    mutableLiveData.value = products
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Favourites", "Error retrieving favorite products", exception)
            }

        return mutableLiveData
    }

    suspend fun updateUser(user: User) {
        userCollection.document(user.email!!).set(user).await()
    }

}