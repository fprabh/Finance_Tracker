package com.example.data

import kotlinx.coroutines.flow.Flow

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
        return categoryDao.insertCategory(category)
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
}
