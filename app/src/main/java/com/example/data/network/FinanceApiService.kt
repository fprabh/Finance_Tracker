package com.example.data.network

import com.example.data.Category
import com.example.data.Transaction
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FinanceApiService {
    
    @GET("categories/")
    suspend fun getCategories(): List<Category>

    @POST("categories/")
    suspend fun createCategory(@Body category: Category): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    @GET("transactions/")
    suspend fun getTransactions(): List<Transaction>

    @POST("transactions/")
    suspend fun createTransaction(@Body transaction: Transaction): Transaction
}
