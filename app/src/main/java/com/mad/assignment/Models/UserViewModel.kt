package com.mad.assignment.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.Transaction
import com.mad.assignment.entity.User
import com.mad.assignment.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    suspend fun getCurrentUser(): User? {
        return repository.getCurrentUser()
    }

    suspend fun getUser(email: String): User? {
        return repository.getUser(email)
    }

    suspend fun getFavouriteProducts(): LiveData<List<Product>> {
       return repository.getFavouriteProducts()
    }

    fun addToFavourite(productId: String){
        repository.addToFavourite(productId)
    }

    fun removeFromFavourite(productId: String){
        repository.removeFromFavourites(productId)
    }

    suspend fun updateUser(user: User) {
        repository.updateUser(user)
    }

     fun addUser(email : String, name : String, profilePic : String) {
        repository.addUser(email, name, profilePic)
    }
}