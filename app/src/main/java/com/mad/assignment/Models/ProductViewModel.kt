package com.mad.assignment.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.User
import com.mad.assignment.repository.ProductRepository
import com.mad.assignment.repository.UserRepository

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

     suspend fun getAllProducts(): LiveData<List<Product>> {
        return repository.getAllProducts()
    }

    suspend fun addProduct(product: Product) {
        repository.addProduct(product)
    }

    suspend fun getProduct(productID: String): Product? {
        return repository.getProduct(productID)
    }

    suspend fun deleteProduct(productID: String) {
        repository.deleteProduct(productID)
    }

    suspend fun updateProduct(product: Product) {
        repository.updateProduct(product)
    }

    suspend fun getAllPendingProducts(): LiveData<List<Product>>{
        return repository.getAllPendingProducts()
    }

    suspend fun getAllPostedProducts(): LiveData<List<Product>>{
        return repository.getAllPostedProducts()
    }

    suspend fun getAllFavouriteProducts(): LiveData<List<Product>>{
        return repository.getAllFavouriteProducts()
    }



}