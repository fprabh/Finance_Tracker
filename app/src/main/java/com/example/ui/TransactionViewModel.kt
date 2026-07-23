package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.CategoryFilter
import com.example.data.Statement
import com.example.data.Transaction
import com.example.data.TransactionRepository
import com.example.util.CsvParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TransactionRepository
    
    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(
            db.transactionDao(),
            db.categoryDao(),
            db.categoryFilterDao(),
            db.statementDao()
        )
        checkAndSeedDefaultCategories()
        
        // Automatically sync from NAS when the app opens
        syncNow()
    }

    fun syncNow() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncFromBackend()
        }
    }

    fun forceSeedDefaultCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val defaultMapping = mapOf(
                "Delivery Food" to "UBER EATS, DOORDASH",
                "Coffee" to "STARBUCKS, TIM HORTONS, FORECAST, TREES ORGANIC, SECOND CUP, COFFEE DATE",
                "Amazon" to "AMAZON, AMZN",
                "Payment" to "PAYMENT RECEIVED",
                "Movies" to "CINEPLEX, LANDMARK, FAMOUS PLAYER",
                "Video Games" to "SEAGM",
                "Convenience" to "7-ELEVEN, 7 ELEVEN",
                "Parking" to "IMPARK, PAYBYPHONE",
                "Pharmacy" to "REXALL",
                "Fees" to "MEMBERSHIP FEE, INSTALLMENT FEE",
                "Dining" to "MCDONALD'S, PIZZA HUT, CHIPOTLE, SUBWAY, BASKIN ROBBINS, PIZZA GARDEN, TST-RUEX, PARATHA 2 PASTA, THAI EXPRESS, THE WAFFLE CO, TB SAYAN, D Spot Dessert Cafe, FRIENDS INDIAN, GATEWAY PIZZA, HAPPY SINGH, THE BREW ESTATE, SP MANN, TST-CAPO, DOUGHNUT LOVE, YUMMY SLICE, NAMDHARI, Chaiiwala, MAHARAJA BAKERY, TST-BASANT SWEETS",
                "Gas" to "SHELL, CANCO, CHV, CHEVRON, ESSO",
                "Grocery" to "SAVE ON FOODS, SAFEWAY, AGGARWAL, WAL-MART",
                "Subscription/Bills" to "GOOGLE, SPOTIFY, TELUS, ROGERS, VIRGIN, LINKEDIN, APPLE.COM/BILL, UBER ONE MEMBERSHIP",
                "Ride Share" to "UBER TRIP, LYFT",
                "Shopping" to "UNIQLO, SEPHORA, LONDON DRUGS, WINNERSHOMESENSE",
                "Home" to "CANADIAN TIRE, THE HOME DEPOT",
                "Other" to "PET VALU",
                "Credit" to "CREDIT FOR FRAUDULENT",
                "Gym" to "ABC",
                "Transit" to "COMPASS",
                "Insurance" to "BCAA - INSURANCE"
            )
            
            // Fetch existing categories to avoid exact duplicates
            val currentCategories = repository.allCategories.first()
            val existingNames = currentCategories.map { it.name.lowercase() }
            
            defaultMapping.forEach { (categoryName, keywordsStr) ->
                if (!existingNames.contains(categoryName.lowercase())) {
                    val catId = repository.insertCategory(Category(name = categoryName))
                    val keywords = keywordsStr.split(",").map { it.trim() }
                    keywords.forEach { keyword ->
                        if (keyword.isNotEmpty()) {
                            repository.insertFilter(CategoryFilter(categoryId = catId.toInt(), keyword = keyword))
                        }
                    }
                }
            }
        }
    }

    private fun checkAndSeedDefaultCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCategories = repository.allCategories.first()
            if (currentCategories.isEmpty()) {
                forceSeedDefaultCategories()
            }
        }
    }

    // Statements Flow
    val statements: StateFlow<List<Statement>> = repository.allStatements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getTransactionsByStatementId(statementId: Long): Flow<List<Transaction>> {
        return repository.getTransactionsByStatementId(statementId)
    }

    fun getUncategorizedCountForStatement(statementId: Long): Flow<Int> {
        return repository.getUncategorizedCountForStatement(statementId)
    }

    // Transactions Flow
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Categories Flow
    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (name.trim().isNotEmpty()) {
                repository.insertCategory(Category(name = name.trim()))
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    // Filters Flow
    val filters: StateFlow<List<CategoryFilter>> = repository.allFilters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Distinct descriptions of transactions where categoryId IS NULL
    val uncategorizedDescriptions: StateFlow<List<String>> = repository.uncategorizedDescriptions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _importStatus = MutableStateFlow<ImportStatus>(ImportStatus.Idle)
    val importStatus: StateFlow<ImportStatus> = _importStatus.asStateFlow()

    private val _isRetroactiveCategorizing = MutableStateFlow(false)
    val isRetroactiveCategorizing: StateFlow<Boolean> = _isRetroactiveCategorizing.asStateFlow()

    // Import with auto-categorization
    fun importCsvText(
        csvText: String, 
        cardType: String,
        monthYear: String,
        previousBalance: Double,
        paymentsAndCredits: Double,
        interestPaid: Double,
        statementBalance: Double
    ) {
        viewModelScope.launch {
            _importStatus.value = ImportStatus.Loading
            try {
                // Save Statement First
                val statement = Statement(
                    cardType = cardType,
                    monthYear = monthYear,
                    previousBalance = previousBalance,
                    paymentsAndCredits = paymentsAndCredits,
                    interestPaid = interestPaid,
                    statementBalance = statementBalance
                )
                
                val statementId = withContext(Dispatchers.IO) {
                    repository.insertStatement(statement)
                }

                val parsedList = withContext(Dispatchers.IO) {
                    CsvParser.parseCsv(csvText, statementId)
                }

                if (parsedList.isEmpty()) {
                    _importStatus.value = ImportStatus.Error("No valid rows could be parsed. Make sure to paste CSV data.")
                    return@launch
                }

                val finalizedList = withContext(Dispatchers.IO) {
                    val activeFilters = repository.getAllFiltersSuspend()
                    parsedList.map { transaction ->
                        // Match description with keywords
                        val matchedCategoryIds = mutableSetOf<Int>()
                        for (filter in activeFilters) {
                            if (transaction.description.contains(filter.keyword, ignoreCase = true)) {
                                matchedCategoryIds.add(filter.categoryId)
                            }
                        }

                        val (catId, hasConflict) = when {
                            matchedCategoryIds.size == 1 -> {
                                Pair(matchedCategoryIds.first(), false)
                            }
                            matchedCategoryIds.size >= 2 -> {
                                Pair(null, true)
                            }
                            else -> {
                                Pair(null, false)
                            }
                        }

                        transaction.copy(
                            categoryId = catId,
                            hasConflict = hasConflict
                        )
                    }
                }

                withContext(Dispatchers.IO) {
                    repository.insertTransactions(finalizedList)
                }
                _importStatus.value = ImportStatus.Success(finalizedList.size)
            } catch (e: Exception) {
                _importStatus.value = ImportStatus.Error(e.localizedMessage ?: "Failed to parse CSV text.")
            }
        }
    }

    // Edit Transaction
    fun updateTransactionCategory(transaction: Transaction, categoryId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = transaction.copy(
                categoryId = categoryId,
                hasConflict = false // Manual assignment resolves conflict
            )
            repository.updateTransaction(updated)
        }
    }

    // Add filter keyword for category
    fun addCategoryFilter(categoryId: Int, keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val trimmedKeyword = keyword.trim()
            if (trimmedKeyword.isNotEmpty()) {
                repository.insertFilter(
                    CategoryFilter(categoryId = categoryId, keyword = trimmedKeyword)
                )
                
                _isRetroactiveCategorizing.value = true
                try {
                    val uncategorized = repository.getUncategorizedTransactions()
                    val matchedTransactions = uncategorized.filter { transaction ->
                        transaction.description.contains(trimmedKeyword, ignoreCase = true)
                    }.map { transaction ->
                        transaction.copy(
                            categoryId = categoryId,
                            hasConflict = false
                        )
                    }
                    if (matchedTransactions.isNotEmpty()) {
                        repository.insertTransactions(matchedTransactions)
                    }
                } finally {
                    _isRetroactiveCategorizing.value = false
                }
            }
        }
    }

    // Delete filter
    fun deleteCategoryFilter(filter: CategoryFilter) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFilter(filter)
        }
    }

    fun resetImportStatus() {
        _importStatus.value = ImportStatus.Idle
    }

    // CSV Category Import
    fun importCategoriesCsv(csvText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lines = csvText.lines()
            for (line in lines) {
                if (line.trim().isEmpty()) continue
                val parts = line.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                if (parts.isEmpty()) continue
                
                val categoryName = parts[0]
                val category = repository.getCategoryByName(categoryName)
                val categoryId: Int
                if (category == null) {
                    val newId = repository.insertCategory(Category(name = categoryName))
                    categoryId = newId.toInt()
                } else {
                    categoryId = category.id
                }
                
                for (i in 1 until parts.size) {
                    val keyword = parts[i]
                    val existingFilter = repository.getFilterByKeyword(categoryId, keyword)
                    if (existingFilter == null) {
                        repository.insertFilter(CategoryFilter(categoryId = categoryId, keyword = keyword))
                    }
                }
            }
        }
    }

    // Manual Refresh / Recategorize All
    fun reCategorizeAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRetroactiveCategorizing.value = true
            try {
                val allTransactions = repository.getAllTransactionsSuspend()
                val allFilters = repository.getAllFiltersSuspend()
                
                val updatedTransactions = mutableListOf<Transaction>()
                for (transaction in allTransactions) {
                    // Match description with keywords
                    val matchedCategoryIds = mutableSetOf<Int>()
                    for (filter in allFilters) {
                        if (transaction.description.contains(filter.keyword, ignoreCase = true)) {
                            matchedCategoryIds.add(filter.categoryId)
                        }
                    }

                    val (catId, hasConflict) = when {
                        matchedCategoryIds.size == 1 -> Pair(matchedCategoryIds.first(), false)
                        matchedCategoryIds.size >= 2 -> Pair(null, true)
                        else -> Pair(transaction.categoryId, transaction.hasConflict)
                    }

                    if (catId != null && (catId != transaction.categoryId || hasConflict != transaction.hasConflict)) {
                        updatedTransactions.add(transaction.copy(categoryId = catId, hasConflict = hasConflict))
                    } else if (hasConflict && !transaction.hasConflict) {
                        updatedTransactions.add(transaction.copy(categoryId = null, hasConflict = true))
                    }
                }
                
                if (updatedTransactions.isNotEmpty()) {
                    repository.insertTransactions(updatedTransactions)
                }
            } finally {
                _isRetroactiveCategorizing.value = false
            }
        }
    }

    fun backupDatabase(context: android.content.Context, outputUri: android.net.Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbFile = context.getDatabasePath("finance_database")
                context.contentResolver.openOutputStream(outputUri)?.use { output ->
                    java.io.FileInputStream(dbFile).use { input ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun restoreDatabase(context: android.content.Context, inputUri: android.net.Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dbFile = context.getDatabasePath("finance_database")
                context.contentResolver.openInputStream(inputUri)?.use { input ->
                    java.io.FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }
                // After restoring, we should ideally restart the app or recreate the database instance
                // But as a quick fix we can just exit to force the user to reopen
                System.exit(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

sealed interface ImportStatus {
    object Idle : ImportStatus
    object Loading : ImportStatus
    data class Success(val count: Int) : ImportStatus
    data class Error(val message: String) : ImportStatus
}
