package com.example.data

import kotlinx.coroutines.flow.Flow
import com.example.data.network.NetworkClient
import android.util.Log

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val categoryFilterDao: CategoryFilterDao,
    private val statementDao: StatementDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    
    val allFilters: Flow<List<CategoryFilter>> = categoryFilterDao.getAllFilters()
    
    val allStatements: Flow<List<Statement>> = statementDao.getAllStatements()

    val uncategorizedDescriptions: Flow<List<String>> = transactionDao.getUncategorizedDescriptions()

    suspend fun insertStatement(statement: Statement): Long {
        return statementDao.insertStatement(statement)
    }

    fun getTransactionsByStatementId(statementId: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByStatementId(statementId)
    }

    fun getUncategorizedCountForStatement(statementId: Long): Flow<Int> {
        return transactionDao.getUncategorizedCountForStatement(statementId)
    }

    suspend fun getUncategorizedTransactions(): List<Transaction> {
        return transactionDao.getUncategorizedTransactions()
    }

    suspend fun insertTransactions(transactions: List<Transaction>) {
        transactionDao.insertTransactions(transactions)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun insertCategory(category: Category): Long {
        val id = categoryDao.insertCategory(category)
        if (NetworkClient.IS_SYNC_ENABLED) {
            try {
                NetworkClient.api.createCategory(category.copy(id = id.toInt()))
            } catch (e: Exception) {
                Log.e("Sync", "Failed to push category: ${e.message}")
            }
        }
        return id
    }

    suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategoryWithNullifyingTransactions(category)
    }

    suspend fun insertFilter(filter: CategoryFilter): Long {
        return categoryFilterDao.insertFilter(filter)
    }

    suspend fun deleteFilter(filter: CategoryFilter) {
        categoryFilterDao.deleteFilter(filter)
    }

    suspend fun getAllFiltersSuspend(): List<CategoryFilter> {
        return categoryFilterDao.getAllFiltersSuspend()
    }

    suspend fun getFilterByKeyword(categoryId: Int, keyword: String): CategoryFilter? {
        return categoryFilterDao.getFilterByKeyword(categoryId, keyword)
    }

    suspend fun getAllTransactionsSuspend(): List<Transaction> {
        return transactionDao.getAllTransactionsSuspend()
    }

    fun getFiltersForCategory(categoryId: Int): Flow<List<CategoryFilter>> {
        return categoryFilterDao.getFiltersForCategory(categoryId)
    }

    suspend fun syncFromBackend() {
        if (!NetworkClient.IS_SYNC_ENABLED) return
        
        try {
            val categories = NetworkClient.api.getCategories()
            categories.forEach { categoryDao.insertCategory(it) }
            
            val transactions = NetworkClient.api.getTransactions()
            transactionDao.insertTransactions(transactions)
            Log.d("Sync", "Successfully synced from backend")
        } catch (e: Exception) {
            Log.e("Sync", "Failed to sync with backend: ${e.message}")
        }
    }
}
