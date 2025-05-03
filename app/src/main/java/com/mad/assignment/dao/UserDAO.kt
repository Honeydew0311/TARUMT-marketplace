package com.mad.assignment.dao

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserDAO() {

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun addUser(email : String, name : String, profilePic : String) {
        // validate whether user already exists
        userCollection.document(email).get().addOnSuccessListener { it ->
            if (it.exists()) {
                Log.d(TAG, "User already exists")
            } else {
                // Add user to database
                val user = hashMapOf(
                    "email" to email,
                    "username" to name,
                    "profileImgUrl" to profilePic,
                    "phone" to "",
                    "address" to "",
                    "status" to ""
                )

                userCollection.document(email).set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }
}