
package com.example.ui

import com.example.R
import java.time.LocalDate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.example.data.Category
import com.example.data.CategoryFilter
import com.example.data.Transaction
import com.example.data.Statement
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Analytics
import java.time.Instant
import java.time.ZoneId

enum class ScreenDestination(val route: String, val title: String, val icon: ImageVector) {
    Dashboard("dashboard", title = "Dashboard", Icons.Rounded.Dashboard),
    Transactions("transactions", title = "Transactions", Icons.Rounded.ReceiptLong),
    Settings("settings", title = "Settings", Icons.Rounded.Settings)
}

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(ScreenDestination.Dashboard) }
    
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val importStatus by viewModel.importStatus.collectAsState()
    val suggestions by viewModel.uncategorizedDescriptions.collectAsState()
    val isRetroactiveCategorizing by viewModel.isRetroactiveCategorizing.collectAsState()
    val statements by viewModel.statements.collectAsState()

    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var showAnalyticsScreen by remember { mutableStateOf(false) }
    var analyticsPreSelectedCategoryId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                ScreenDestination.values().forEach { destination ->
                    val selected = currentScreen == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_${destination.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            HeaderBar(currentScreen = currentScreen)

            // Status Banners
            StatusBanner(
                status = importStatus,
                onDismiss = { viewModel.resetImportStatus() }
            )

            // Screen Switcher
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentScreen) {
                    ScreenDestination.Dashboard -> {
                        if (showAnalyticsScreen) {
                            AnalyticsScreen(
                                transactions = transactions,
                                categories = categories,
                                filters = filters,
                                statements = statements,
                                preSelectedCategoryId = analyticsPreSelectedCategoryId,
                                onBack = {
                                    showAnalyticsScreen = false
                                    analyticsPreSelectedCategoryId = null
                                }
                            )
                        } else {
                            DashboardScreen(
                                transactions = transactions,
                                categories = categories,
                                filters = filters,
                                statements = statements,
                                onCategoryClick = { categoryId ->
                                    analyticsPreSelectedCategoryId = categoryId
                                    showAnalyticsScreen = true
                                },
                                onViewAnalyticsClick = {
                                    analyticsPreSelectedCategoryId = null
                                    showAnalyticsScreen = true
                                }
                            )
                        }
                    }
                    ScreenDestination.Transactions -> {
                        TransactionsTabScreen(
                            viewModel = viewModel,
                            categories = categories,
                            onTransactionClick = { transactionToEdit = it }
                        )
                    }
                    ScreenDestination.Settings -> {
                        SettingsTabScreen(
                            viewModel = viewModel,
                            filters = filters,
                            suggestions = suggestions,
                            onAddFilter = { catId, keyword ->
                                viewModel.addCategoryFilter(catId, keyword)
                            },
                            onDeleteFilter = { filter ->
                                viewModel.deleteCategoryFilter(filter)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Edit Dialog for single transaction
    transactionToEdit?.let { transaction ->
        TransactionEditDialog(
            transaction = transaction,
            categories = categories,
            filters = filters,
            viewModel = viewModel,
            onDismiss = { transactionToEdit = null },
            onSaveCategory = { categoryId ->
                viewModel.updateTransactionCategory(transaction, categoryId)
            }
        )
    }

    // Refreshing popup signifying updating transactions
    if (isRetroactiveCategorizing) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Updating Transactions...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Retroactively applying the new categorization rule to existing transactions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderBar(currentScreen: ScreenDestination) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = currentScreen.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (currentScreen) {
                        ScreenDestination.Dashboard -> "Financial Insights & Distribution"
                        ScreenDestination.Transactions -> "Pasted Statement Import & History"
                        ScreenDestination.Settings -> "Settings & Data Management"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = when (currentScreen) {
                    ScreenDestination.Dashboard -> Icons.Rounded.Dashboard
                    ScreenDestination.Transactions -> Icons.Rounded.ReceiptLong
                    ScreenDestination.Settings -> Icons.Rounded.Settings
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun DashboardScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    filters: List<CategoryFilter>,
    statements: List<Statement>,
    onCategoryClick: (Int?) -> Unit,
    onViewAnalyticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSpend = remember(transactions) {
        transactions.sumOf { it.amount }
    }
    
    val uncategorizedCount = remember(transactions) {
        transactions.count { it.categoryId == null }
    }

    val conflictCount = remember(transactions) {
        transactions.count { it.hasConflict }
    }

    val categoryTotals = remember(transactions, categories) {
        categories.associateWith { category ->
            transactions.filter { it.categoryId == category.id }.sumOf { it.amount }
        }
    }

    val uncategorizedTotal = remember(transactions) {
        transactions.filter { it.categoryId == null }.sumOf { it.amount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Monthly Expenditure Summary
        if (transactions.isNotEmpty() && statements.isNotEmpty()) {
            MonthlyExpenditureSummaryCard(
                transactions = transactions,
                statements = statements
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Main Total Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "TOTAL EXPENDITURE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.US, "$%,.2f", totalSpend),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (conflictCount > 0 || uncategorizedCount > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (conflictCount > 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = "Conflict Warning",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$conflictCount Conflicts",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }

                        if (uncategorizedCount > 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lightbulb,
                                        contentDescription = "Uncategorized Info",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$uncategorizedCount Uncategorized",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Spending by Category Distribution
        Text(
            text = "SPENDING BY CATEGORY",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        if (transactions.isEmpty()) {
            EmptyDashboardView()
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Loop over categories
                    categories.forEach { category ->
                        val amount = categoryTotals[category] ?: 0.0
                        val percentage = if (totalSpend > 0) (amount / totalSpend).toFloat() else 0f
                        val categoryFilters = filters.filter { it.categoryId == category.id }

                        CategoryDistributionRow(
                            name = category.name,
                            amount = amount,
                            percentage = percentage,
                            filterCount = categoryFilters.size,
                            onClick = { onCategoryClick(category.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Add Uncategorized row
                    if (uncategorizedTotal > 0) {
                        val percentage = if (totalSpend > 0) (uncategorizedTotal / totalSpend).toFloat() else 0f
                        CategoryDistributionRow(
                            name = "Uncategorized",
                            amount = uncategorizedTotal,
                            percentage = percentage,
                            filterCount = 0,
                            isUncategorized = true,
                            onClick = { onCategoryClick(null) }
                        )
                    }
                }
            }

            // View Analytics button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onViewAnalyticsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Full Analytics",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun CategoryDistributionRow(
    name: String,
    amount: Double,
    percentage: Float,
    filterCount: Int,
    isUncategorized: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUncategorized) Icons.Rounded.Lightbulb else Icons.Rounded.Label,
                    contentDescription = null,
                    tint = if (isUncategorized) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isUncategorized) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "($filterCount filters)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                text = String.format(Locale.US, "$%,.2f", amount),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isUncategorized) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = percentage.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUncategorized) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                    )
            )
        }
    }
}

@Composable
fun ImportStatementDialog(
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    var csvInputText by remember { mutableStateOf("") }
    var accountTypeDropdownExpanded by remember { mutableStateOf(false) }
    
    val accountOptions = listOf(
        "AMEX Platinum",
        "AMEX Cobalt",
        "Rogers Mastercard",
        "Scotiabank Credit",
        "Scotiabank Debit"
    )
    var selectedAccountType by remember { mutableStateOf(accountOptions[0]) }

    val currentLocalDate = remember { java.time.LocalDate.now() }
    val months = remember { listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December") }
    val years = remember { (currentLocalDate.year - 2 .. currentLocalDate.year + 2).map { it.toString() } }
    var selectedMonth by remember { mutableStateOf(months[currentLocalDate.monthValue - 1]) }
    var selectedYear by remember { mutableStateOf(currentLocalDate.year.toString()) }
    var prevBalanceInput by remember { mutableStateOf("") }
    var paymentsInput by remember { mutableStateOf("") }
    var interestInput by remember { mutableStateOf("") }
    var statementBalanceInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 500.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Import Statement",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "STATEMENT DETAILS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Account Type Dropdown
                Text(
                    text = "Account Type",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { accountTypeDropdownExpanded = true }
                            .testTag("import_account_type_dropdown"),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedAccountType,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Dropdown icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = accountTypeDropdownExpanded,
                        onDismissRequest = { accountTypeDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.surface)
                    ) {
                        accountOptions.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    selectedAccountType = account
                                    accountTypeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Month Dropdown
                    var monthExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedMonth,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Month") },
                            trailingIcon = {
                                Icon(Icons.Rounded.ArrowDropDown, "dropdown", Modifier.clickable { monthExpanded = true })
                            },
                            modifier = Modifier.fillMaxWidth().clickable { monthExpanded = true },
                            shape = RoundedCornerShape(8.dp)
                        )
                        DropdownMenu(
                            expanded = monthExpanded, 
                            onDismissRequest = { monthExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface).heightIn(max = 300.dp)
                        ) {
                            months.forEach { m ->
                                DropdownMenuItem(text = { Text(m) }, onClick = { selectedMonth = m; monthExpanded = false })
                            }
                        }
                    }

                    // Year Dropdown
                    var yearExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedYear,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Year") },
                            trailingIcon = {
                                Icon(Icons.Rounded.ArrowDropDown, "dropdown", Modifier.clickable { yearExpanded = true })
                            },
                            modifier = Modifier.fillMaxWidth().clickable { yearExpanded = true },
                            shape = RoundedCornerShape(8.dp)
                        )
                        DropdownMenu(
                            expanded = yearExpanded, 
                            onDismissRequest = { yearExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface).heightIn(max = 300.dp)
                        ) {
                            years.forEach { y ->
                                DropdownMenuItem(text = { Text(y) }, onClick = { selectedYear = y; yearExpanded = false })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prevBalanceInput,
                        onValueChange = { prevBalanceInput = it },
                        label = { Text("Prev Balance") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = paymentsInput,
                        onValueChange = { paymentsInput = it },
                        label = { Text("Payments") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = interestInput,
                        onValueChange = { interestInput = it },
                        label = { Text("Interest") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = statementBalanceInput,
                        onValueChange = { statementBalanceInput = it },
                        label = { Text("Stmt Balance") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TRANSACTIONS CSV",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // CSV text input
                OutlinedTextField(
                    value = csvInputText,
                    onValueChange = { csvInputText = it },
                    label = { Text("CSV Text") },
                    placeholder = { Text("Date,Description,Card Member,Amount\n14-May-24,Amazon,John,24.99") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("import_csv_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val isFormValid = csvInputText.isNotBlank() &&
                        prevBalanceInput.toDoubleOrNull() != null && paymentsInput.toDoubleOrNull() != null &&
                        interestInput.toDoubleOrNull() != null && statementBalanceInput.toDoubleOrNull() != null

                Button(
                    onClick = {
                        if (isFormValid) {
                            viewModel.importCsvText(
                                csvText = csvInputText,
                                cardType = selectedAccountType,
                                monthYear = "$selectedMonth $selectedYear",
                                previousBalance = prevBalanceInput.toDoubleOrNull() ?: 0.0,
                                paymentsAndCredits = paymentsInput.toDoubleOrNull() ?: 0.0,
                                interestPaid = interestInput.toDoubleOrNull() ?: 0.0,
                                statementBalance = statementBalanceInput.toDoubleOrNull() ?: 0.0
                            )
                            onDismiss() // Close the dialog after successful import
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("import_paste_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = isFormValid
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PostAdd,
                        contentDescription = "Add"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IMPORT STATEMENT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionsTabScreen(
    viewModel: TransactionViewModel,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val statements by viewModel.statements.collectAsState()
    var selectedStatement by remember { mutableStateOf<Statement?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (selectedStatement == null) {
            StatementsListScreen(
                statements = statements,
                viewModel = viewModel,
                onStatementClick = { selectedStatement = it }
            )
        } else {
            StatementDetailsScreen(
                statement = selectedStatement!!,
                viewModel = viewModel,
                categories = categories,
                onBack = { selectedStatement = null },
                onTransactionClick = onTransactionClick
            )
        }

        if (selectedStatement == null) {
            FloatingActionButton(
                onClick = { showImportDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("import_fab"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Rounded.PostAdd,
                    contentDescription = "Import Statement"
                )
            }
        }

        if (showImportDialog) {
            ImportStatementDialog(
                viewModel = viewModel,
                onDismiss = { showImportDialog = false }
            )
        }
    }
}

@Composable
fun StatementsListScreen(
    statements: List<Statement>,
    viewModel: TransactionViewModel,
    onStatementClick: (Statement) -> Unit
) {
    val cardTypes = listOf(
        "AMEX Platinum",
        "AMEX Cobalt",
        "Rogers Mastercard",
        "Scotiabank Credit",
        "Scotiabank Debit"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = "STATEMENTS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        cardTypes.forEach { cardType ->
            val cardStatements = statements.filter { it.cardType == cardType }.sortedByDescending { it.id }
            item {
                CardTypeExpandableSection(
                    cardType = cardType,
                    statements = cardStatements,
                    viewModel = viewModel,
                    onStatementClick = onStatementClick
                )
            }
        }
    }
}

@Composable
fun CardTypeExpandableSection(
    cardType: String,
    statements: List<Statement>,
    viewModel: TransactionViewModel,
    onStatementClick: (Statement) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val brandColor = when(cardType.lowercase()) {
        "scotiabank credit", "scotiabank debit" -> Color(0xFFD9534F) // Soft Coral Red
        "rogers mastercard" -> Color(0xFF8B0000) // Deep Crimson/Burgundy
        "amex platinum", "amex cobalt" -> Color(0xFF4A6572) // Muted Slate Blue
        else -> MaterialTheme.colorScheme.primary
    }

    val brandIcon = when(cardType.lowercase()) {
        "scotiabank credit", "scotiabank debit" -> R.drawable.scotiabank
        "rogers mastercard" -> R.drawable.rogers
        "amex platinum", "amex cobalt" -> R.drawable.amex
        else -> R.drawable.ic_launcher_foreground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (brandIcon != R.drawable.ic_launcher_foreground) {
                        Image(
                            painter = painterResource(id = brandIcon),
                            contentDescription = "Bank Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = cardType,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface // Use standard text color for less contrast
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${statements.size} statements",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ArrowDropDown else Icons.Rounded.List,
                        contentDescription = "Expand",
                        tint = brandColor
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (statements.isEmpty()) {
                        Text(
                            text = "No statements found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        statements.forEach { statement ->
                            StatementCard(
                                statement = statement,
                                viewModel = viewModel,
                                onClick = { onStatementClick(statement) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatementCard(
    statement: Statement,
    viewModel: TransactionViewModel,
    onClick: () -> Unit
) {
    val countFlow = remember(statement.id) { viewModel.getUncategorizedCountForStatement(statement.id) }
    val uncategorizedCount by countFlow.collectAsState(initial = 0)

    val brandColor = when(statement.cardType.lowercase()) {
        "scotiabank credit", "scotiabank debit", "scotiabank" -> Color(0xFFD9534F) // Soft Coral Red
        "rogers mastercard", "rogers" -> Color(0xFF8B0000) // Deep Crimson/Burgundy
        "amex platinum", "amex cobalt", "amex" -> Color(0xFF4A6572) // Muted Slate Blue
        else -> MaterialTheme.colorScheme.primary
    }

    val brandIcon = when(statement.cardType.lowercase()) {
        "scotiabank credit", "scotiabank debit", "scotiabank" -> R.drawable.scotiabank
        "rogers mastercard", "rogers" -> R.drawable.rogers
        "amex platinum", "amex cobalt", "amex" -> R.drawable.amex
        else -> R.drawable.ic_launcher_foreground // default
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (brandIcon != R.drawable.ic_launcher_foreground) {
                    Image(
                        painter = painterResource(id = brandIcon),
                        contentDescription = "Bank Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 12.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.CreditCard,
                        contentDescription = "Card",
                        tint = brandColor,
                        modifier = Modifier.size(32.dp).padding(end = 12.dp)
                    )
                }
                
                Column {
                    Text(
                        text = statement.monthYear,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                if (uncategorizedCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = "Uncategorized Info",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$uncategorizedCount Uncategorized",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    }
                }
            } // Close the Row containing icon and texts
            Text(
                text = String.format(java.util.Locale.US, "$%,.2f", statement.statementBalance),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun StatementDetailsScreen(
    statement: Statement,
    viewModel: TransactionViewModel,
    categories: List<Category>,
    onBack: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    val txFlow = remember(statement.id) { viewModel.getTransactionsByStatementId(statement.id) }
    val statementTransactions by txFlow.collectAsState(initial = emptyList())
    var showEditDialog by remember { mutableStateOf(false) }

    val brandColor = when(statement.cardType.lowercase()) {
        "scotiabank" -> Color(0xFFEC111A)
        "rogers" -> Color(0xFFDA291C)
        "amex" -> Color(0xFF002663)
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val onBrandColor = Color.White

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${statement.cardType} - ${statement.monthYear}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit Statement",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showEditDialog) {
            StatementEditDialog(
                statement = statement,
                viewModel = viewModel,
                onDismiss = { showEditDialog = false }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = brandColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "STATEMENT SUMMARY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = onBrandColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow("Previous Balance", statement.previousBalance, onBrandColor)
                        SummaryRow("Payments and Credits", statement.paymentsAndCredits, onBrandColor)
                        SummaryRow("Interest Paid", statement.interestPaid, onBrandColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Statement Balance",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = onBrandColor
                            )
                            Text(
                                text = String.format(java.util.Locale.US, "$%,.2f", statement.statementBalance),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = onBrandColor
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "TRANSACTIONS",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            if (statementTransactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else {
                items(
                    items = statementTransactions,
                    key = { it.id }
                ) { transaction ->
                    val matchedCategory = categories.find { it.id == transaction.categoryId }
                    TransactionDisplayRow(
                        transaction = transaction,
                        category = matchedCategory,
                        onClick = { onTransactionClick(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
        Text(
            text = String.format(java.util.Locale.US, "$%,.2f", amount),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = color
        )
    }
}
@Composable
fun TransactionDisplayRow(
    transaction: Transaction,
    category: Category?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US) }
    val formattedDate = remember(transaction.date) { transaction.date.format(dateFormatter) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Description
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Date line
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))

                // Category or conflict status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        transaction.hasConflict -> {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = "Conflict",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Conflict",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        category != null -> {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        else -> {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Uncategorized",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Amount
            Text(
                text = String.format(Locale.US, "$%,.2f", transaction.amount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (transaction.amount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsTabScreen(
    viewModel: TransactionViewModel,
    filters: List<CategoryFilter>,
    suggestions: List<String>,
    onAddFilter: (Int, String) -> Unit,
    onDeleteFilter: (CategoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val allCategoriesList by viewModel.allCategories.collectAsState(initial = emptyList())
    var filterText by remember { mutableStateOf("") }
    var selectedCategoryIdForFilter by remember { mutableStateOf<Int?>(null) }
    var suggestionsExpanded by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var filterToDelete by remember { mutableStateOf<CategoryFilter?>(null) }
    val filterFocusRequester = remember { FocusRequester() }

    // Setup initial selected category if not set
    if (selectedCategoryIdForFilter == null && allCategoriesList.isNotEmpty()) {
        selectedCategoryIdForFilter = allCategoriesList[0].id
    }

    val filteredSuggestions = remember(filterText, suggestions) {
        if (filterText.isBlank()) emptyList()
        else suggestions.filter { it.contains(filterText, ignoreCase = true) }.take(5)
    }

    var showCsvImportDialog by remember { mutableStateOf(false) }
    var showDataManagementScreen by remember { mutableStateOf(false) }
    var showLoadDefaultsConfirm by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/x-sqlite3")) { uri ->
        if (uri != null) viewModel.backupDatabase(context, uri)
    }
    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.restoreDatabase(context, uri)
    }

    if (showDataManagementScreen) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                IconButton(onClick = { showDataManagementScreen = false }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("DATA MANAGEMENT", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.syncNow() },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync with NAS")
                    }

                    Button(
                        onClick = { showCsvImportDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Rounded.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import Categories (CSV)")
                    }

                    Button(
                        onClick = { viewModel.reCategorizeAllTransactions() },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Re-Categorize All Transactions")
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { backupLauncher.launch("finance_database.db") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Backup")
                        }
                        Button(
                            onClick = { restoreLauncher.launch(arrayOf("*/*")) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Icon(imageVector = Icons.Rounded.Restore, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showLoadDefaultsConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Rounded.Lightbulb, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Load Default Categories")
                    }
                }
            }
        }

        if (showLoadDefaultsConfirm) {
            AlertDialog(
                onDismissRequest = { showLoadDefaultsConfirm = false },
                title = { Text("Load Default Categories") },
                text = { Text("Are you sure you want to load default categories? This might reset or duplicate categories if you have already customized them.") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.forceSeedDefaultCategories()
                        showLoadDefaultsConfirm = false
                    }) {
                        Text("Yes, Load")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoadDefaultsConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        return // Return here so we only show the data management screen
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Button(
            onClick = { showDataManagementScreen = true },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Advanced Data Management")
        }

        // Create Filter Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CREATE CATEGORY FILTER",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Select Category Dropdown
                Text(
                    text = "Assign Keyword To Category",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                var dropdownExpanded by remember { mutableStateOf(false) }
                val currentCategory = allCategoriesList.find { it.id == selectedCategoryIdForFilter }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentCategory?.name ?: "Select Category",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Dropdown icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.surface).heightIn(max = 350.dp)
                    ) {
                        if (allCategoriesList.isNotEmpty()) {
                            allCategoriesList.forEach { category ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = category.name,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ) 
                                    },
                                    onClick = {
                                        selectedCategoryIdForFilter = category.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        } else {
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = "No Categories Available",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) 
                                },
                                onClick = {
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keyword/Merchant suggestion Box
                Text(
                    text = "Keyword / Merchant Name Filter",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = filterText,
                        onValueChange = {
                            filterText = it
                            suggestionsExpanded = true
                        },
                        placeholder = { Text("e.g. Starbucks, Amazon, Groceries") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(filterFocusRequester),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Autocomplete Suggestions Dropdown
                    if (suggestionsExpanded && filteredSuggestions.isNotEmpty()) {
                        DropdownMenu(
                            expanded = suggestionsExpanded,
                            onDismissRequest = { suggestionsExpanded = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier.fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.surface).heightIn(max = 300.dp)
                        ) {
                            filteredSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        filterText = suggestion
                                        suggestionsExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val targetCatId = selectedCategoryIdForFilter
                        if (targetCatId != null && filterText.isNotBlank()) {
                            onAddFilter(targetCatId, filterText)
                            filterText = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("add_filter_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = filterText.isNotBlank() && selectedCategoryIdForFilter != null
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalOffer,
                        contentDescription = "Tag Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD KEYWORD FILTER")
                }
            }
        }

        // List of all existing Categories with Edit and Delete capability
        Text(
            text = "MANAGE CATEGORIES",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Add Quick Category input
                var newCategoryName by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("New Category Name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                viewModel.addCategory(newCategoryName)
                                newCategoryName = ""
                            }
                        },
                        enabled = newCategoryName.isNotBlank(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add")
                    }
                }

            }
        }

        allCategoriesList.forEach { category ->
            val categoryFilters = filters.filter { it.categoryId == category.id }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { categoryToEdit = category }.padding(vertical = 4.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${categoryFilters.size} filters",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = { categoryToDelete = category },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = "Delete Category",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (categoryFilters.isEmpty()) {
                        Text(
                            text = "No filter keywords added yet. Add one above to auto-assign transactions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categoryFilters.forEach { filter ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 8.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
                                    ) {
                                        Text(
                                            text = filter.keyword,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { filterToDelete = filter },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.DeleteOutline,
                                                contentDescription = "Delete filter",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (categoryToEdit != null) {
        val category = categoryToEdit!!
        var editNameText by remember(category.id) { mutableStateOf(category.name) }
        Dialog(
            onDismissRequest = { categoryToEdit = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Rename Category",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editNameText,
                        onValueChange = { editNameText = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { categoryToEdit = null }) {
                            Text("CANCEL")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (editNameText.isNotBlank()) {
                                    viewModel.updateCategory(category.copy(name = editNameText.trim()))
                                    categoryToEdit = null
                                }
                            },
                            enabled = editNameText.isNotBlank()
                        ) {
                            Text("SAVE")
                        }
                    }
                }
            }
        }
    }

    // CSV Import Dialog
    if (showCsvImportDialog) {
        var csvPasteText by remember { mutableStateOf("") }
        Dialog(
            onDismissRequest = { showCsvImportDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 500.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Import Categories",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Paste CSV text below. Format: Category, keyword1, keyword2...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = csvPasteText,
                        onValueChange = { csvPasteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp, max = 300.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        placeholder = { Text("Groceries, Walmart, Costco\nEntertainment, Netflix, AMC") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCsvImportDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (csvPasteText.isNotBlank()) {
                                    viewModel.importCategoriesCsv(csvPasteText)
                                    showCsvImportDialog = false
                                }
                            },
                            enabled = csvPasteText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Process CSV")
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (categoryToDelete != null) {
        val category = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text(
                    text = "Delete Category?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${category.name}'? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(category)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Delete Confirmation Dialog for Keywords
    if (filterToDelete != null) {
        val filter = filterToDelete!!
        val categoryName = allCategoriesList.find { it.id == filter.categoryId }?.name ?: "this category"
        AlertDialog(
            onDismissRequest = { filterToDelete = null },
            title = {
                Text(
                    text = "Delete Keyword?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${filter.keyword}' from $categoryName?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFilter(filter)
                        filterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { filterToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditDialog(
    transaction: Transaction,
    categories: List<Category>,
    filters: List<CategoryFilter>,
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit,
    onSaveCategory: (Int?) -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.US) }
    val formattedDate = remember(transaction.date) { transaction.date.format(dateFormatter) }

    var editDescription by remember(transaction) { mutableStateOf(transaction.description) }
    var editAmount by remember(transaction) { mutableStateOf(transaction.amount.toString()) }
    var editCardMember by remember(transaction) { mutableStateOf(transaction.cardMember) }
    
    val months = remember { listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec") }
    val years = remember { (2000..2050).map { it.toString() } }
    val days = remember { (1..31).map { it.toString().padStart(2, '0') } }
    
    var selDay by remember(transaction) { mutableStateOf(transaction.date.dayOfMonth.toString().padStart(2, '0')) }
    var selMonth by remember(transaction) { mutableStateOf(transaction.date.month.name.lowercase().capitalize().take(3)) }
    var selYear by remember(transaction) { mutableStateOf(transaction.date.year.toString()) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    var selectedCategoryId by remember(transaction.id) { mutableStateOf(transaction.categoryId) }
    var pendingCategoryIdToSave by remember { mutableStateOf<Int?>(null) }
    var showRuleDialog by remember { mutableStateOf(false) }
    var stepOneOfRuleDialog by remember { mutableStateOf(true) }
    var ruleKeywordText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 500.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Transaction",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Core details card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TRANSACTION DETAILS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editDescription, onValueChange = { editDescription = it },
                            label = { Text("Merchant / Description") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            singleLine = true, shape = RoundedCornerShape(8.dp)
                        )
                        
                        var showDatePicker by remember { mutableStateOf(false) }
                        val initialMillis = remember(transaction) {
                            transaction.date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                        }

                        OutlinedTextField(
                            value = "$selDay $selMonth $selYear",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Date") },
                            trailingIcon = {
                                Icon(Icons.Rounded.ArrowDropDown, "Select date",
                                    Modifier.clickable { showDatePicker = true })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { showDatePicker = true },
                            shape = RoundedCornerShape(8.dp)
                        )

                        if (showDatePicker) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = initialMillis
                            )
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val picked = Instant.ofEpochMilli(millis)
                                                .atZone(ZoneId.of("UTC"))
                                                .toLocalDate()
                                            selDay = picked.dayOfMonth.toString().padStart(2, '0')
                                            selMonth = picked.month.name.lowercase()
                                                .replaceFirstChar { c -> c.uppercase() }.take(3)
                                            selYear = picked.year.toString()
                                        }
                                        showDatePicker = false
                                    }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                colors = DatePickerDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                DatePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    colors = DatePickerDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                                        headlineContentColor = MaterialTheme.colorScheme.onSurface,
                                        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        yearContentColor = MaterialTheme.colorScheme.onSurface,
                                        currentYearContentColor = MaterialTheme.colorScheme.primary,
                                        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                                        dayContentColor = MaterialTheme.colorScheme.onSurface,
                                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                        todayContentColor = MaterialTheme.colorScheme.primary,
                                        todayDateBorderColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = editCardMember, onValueChange = { editCardMember = it },
                            label = { Text("Card Member") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            singleLine = true, shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = editAmount, onValueChange = { editAmount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            singleLine = true, shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Conflict Alert if any
                if (transaction.hasConflict) {
                    val conflictingMatches = remember(transaction.description, filters, categories) {
                        filters.filter { filter ->
                            transaction.description.contains(filter.keyword, ignoreCase = true)
                        }.mapNotNull { filter ->
                            val cat = categories.find { it.id == filter.categoryId }
                            if (cat != null) Pair(cat.name, filter.keyword) else null
                        }.distinctBy { it.first to it.second }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = "Conflict Icon",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Multiple keyword filters matched:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            conflictingMatches.forEach { (categoryName, keyword) ->
                                Row(
                                    modifier = Modifier.padding(start = 30.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "\u2022",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = categoryName,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = " \u2014 keyword \"$keyword\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manually assign a category below to resolve.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 30.dp)
                            )
                        }
                    }
                }

                // Category Selection list
                Text(
                    text = "SELECT CATEGORY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                var expanded by remember { mutableStateOf(false) }
                val currentCategory = categories.find { it.id == selectedCategoryId }
                val categoryName = currentCategory?.name ?: "Uncategorized"

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .testTag("dialog_category_button"),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (selectedCategoryId == null) Icons.Rounded.Lightbulb else Icons.Rounded.Label,
                                    contentDescription = null,
                                    tint = if (selectedCategoryId == null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Expand categories dropdown",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f).background(MaterialTheme.colorScheme.surface).heightIn(max = 350.dp)
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = "Uncategorized",
                                    color = MaterialTheme.colorScheme.onSurface
                                ) 
                            },
                            onClick = {
                                selectedCategoryId = null
                                onSaveCategory(null)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Lightbulb,
                                    contentDescription = "Uncategorized Icon",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            },
                            modifier = Modifier.testTag("dropdown_item_uncategorized")
                        )

                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = category.name,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ) 
                                },
                                onClick = {
                                    pendingCategoryIdToSave = category.id
                                    ruleKeywordText = transaction.description
                                    stepOneOfRuleDialog = true
                                    showRuleDialog = true
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Label,
                                        contentDescription = "${category.name} Icon",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.testTag("dropdown_item_${category.name.lowercase().replace(" ", "_")}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val newAmount = editAmount.toDoubleOrNull() ?: transaction.amount
                        val monthIndex = months.indexOfFirst { it.equals(selMonth, ignoreCase = true) } + 1
                        val newDate = try {
                            LocalDate.of(selYear.toInt(), monthIndex, selDay.toInt())
                        } catch (e: Exception) { transaction.date }
                        
                        val updatedTx = transaction.copy(
                            description = editDescription,
                            amount = newAmount,
                            cardMember = editCardMember,
                            date = newDate
                        )
                        viewModel.updateTransactionFull(updatedTx)
                        onDismiss()
                    }) {
                        Text("SAVE")
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Auto-Categorization rule generation popup dialog
    if (showRuleDialog && pendingCategoryIdToSave != null) {
        val targetCategory = categories.find { it.id == pendingCategoryIdToSave }
        Dialog(
            onDismissRequest = { showRuleDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 420.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    if (stepOneOfRuleDialog) {
                        Text(
                            text = "Create Category Rule?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Would you like to create an auto-categorization rule for this merchant?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    // NO option: Close popup and save category to transaction as normal
                                    selectedCategoryId = pendingCategoryIdToSave
                                    onSaveCategory(pendingCategoryIdToSave)
                                    showRuleDialog = false
                                }
                            ) {
                                Text("NO")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    // YES option: Transition to text input state
                                    stepOneOfRuleDialog = false
                                }
                            ) {
                                Text("YES")
                            }
                        }
                    } else {
                        Text(
                            text = "Configure Rule Keyword",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This will automatically assign future transactions matching this keyword to \"${targetCategory?.name}\".",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = ruleKeywordText,
                            onValueChange = { ruleKeywordText = it },
                            label = { Text("Merchant / Keyword") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showRuleDialog = false }
                            ) {
                                Text("CANCEL")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (ruleKeywordText.isNotBlank()) {
                                        viewModel.addCategoryFilter(pendingCategoryIdToSave!!, ruleKeywordText)
                                        selectedCategoryId = pendingCategoryIdToSave
                                        onSaveCategory(pendingCategoryIdToSave)
                                        showRuleDialog = false
                                    }
                                },
                                enabled = ruleKeywordText.isNotBlank()
                            ) {
                                Text("SAVE RULE")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor
        )
    }
}

@Composable
fun EmptyDashboardView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Lightbulb,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No Summary Data",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = "Import transactions under the Transactions tab to see visual spending patterns.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyTransactionsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ReceiptLong,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Transactions Pasted",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Paste text and choose an account above, then click Import Pasted CSV.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatusBanner(
    status: ImportStatus,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = status != ImportStatus.Idle,
        enter = fadeIn(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        when (status) {
            ImportStatus.Loading -> {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Parsing and auto-categorizing statement...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            is ImportStatus.Success -> {
                Surface(
                    color = Color(0xFFE8F5E9), // Clean success green
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Success check",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Successfully imported ${status.count} transactions!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF1B5E20)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            is ImportStatus.Error -> {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = "Error icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = status.message,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            ImportStatus.Idle -> { /* Idle */ }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    com.example.ui.theme.MyApplicationTheme {
        androidx.compose.material3.Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            DashboardScreen(
                transactions = listOf(
                    com.example.data.Transaction(id = 1, date = java.time.LocalDate.now(), description = "Grocery Store", amount = 120.50, cardMember = "John", statementId = 1L, hasConflict = false),
                    com.example.data.Transaction(id = 2, date = java.time.LocalDate.now(), description = "Netflix", amount = 15.99, cardMember = "John", statementId = 1L, hasConflict = false)
                ),
                categories = listOf(
                    com.example.data.Category(id = 1, name = "Groceries"),
                    com.example.data.Category(id = 2, name = "Entertainment")
                ),
                filters = emptyList(),
                statements = emptyList(),
                onCategoryClick = {},
                onViewAnalyticsClick = {}
            )
        }
    }
}

@Composable
fun StatementEditDialog(
    statement: Statement,
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    var editPrevBalance by remember(statement) { mutableStateOf(statement.previousBalance.toString()) }
    var editPayments by remember(statement) { mutableStateOf(statement.paymentsAndCredits.toString()) }
    var editInterest by remember(statement) { mutableStateOf(statement.interestPaid.toString()) }
    var editStmtBalance by remember(statement) { mutableStateOf(statement.statementBalance.toString()) }

    // Visual scroll date picker (two dropdowns: Month and Year)
    val months = remember { listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec") }
    val years = remember { (2000..2050).map { it.toString() } }
    
    val initialParts = statement.monthYear.split(" ")
    var selMonth by remember(statement) { mutableStateOf(if (initialParts.size >= 2) initialParts[0] else "Jan") }
    var selYear by remember(statement) { mutableStateOf(if (initialParts.size >= 2) initialParts.last() else "2024") }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).widthIn(max = 500.dp).wrapContentHeight(),
            shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Edit Statement", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Statement",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var monthExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selMonth, onValueChange = {}, readOnly = true, label = { Text("Month") },
                            trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, "dropdown", Modifier.clickable { monthExpanded = true }) },
                            modifier = Modifier.fillMaxWidth().clickable { monthExpanded = true }, shape = RoundedCornerShape(8.dp)
                        )
                        DropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }, modifier = Modifier.heightIn(max = 200.dp)) {
                            months.forEach { m -> DropdownMenuItem(text = { Text(m) }, onClick = { selMonth = m; monthExpanded = false }) }
                        }
                    }
                    var yearExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selYear, onValueChange = {}, readOnly = true, label = { Text("Year") },
                            trailingIcon = { Icon(Icons.Rounded.ArrowDropDown, "dropdown", Modifier.clickable { yearExpanded = true }) },
                            modifier = Modifier.fillMaxWidth().clickable { yearExpanded = true }, shape = RoundedCornerShape(8.dp)
                        )
                        DropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }, modifier = Modifier.heightIn(max = 200.dp)) {
                            years.forEach { y -> DropdownMenuItem(text = { Text(y) }, onClick = { selYear = y; yearExpanded = false }) }
                        }
                    }
                }

                OutlinedTextField(value = editPrevBalance, onValueChange = { editPrevBalance = it }, label = { Text("Previous Balance") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = editPayments, onValueChange = { editPayments = it }, label = { Text("Payments & Credits") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = editInterest, onValueChange = { editInterest = it }, label = { Text("Interest Paid") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true, shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = editStmtBalance, onValueChange = { editStmtBalance = it }, label = { Text("Statement Balance") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true, shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCEL") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val updated = statement.copy(
                            monthYear = "$selMonth $selYear",
                            previousBalance = editPrevBalance.toDoubleOrNull() ?: statement.previousBalance,
                            paymentsAndCredits = editPayments.toDoubleOrNull() ?: statement.paymentsAndCredits,
                            interestPaid = editInterest.toDoubleOrNull() ?: statement.interestPaid,
                            statementBalance = editStmtBalance.toDoubleOrNull() ?: statement.statementBalance
                        )
                        viewModel.updateStatement(updated)
                        onDismiss()
                    }) { Text("SAVE") }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Statement") },
            text = { Text("Are you sure you want to delete this statement AND all its associated transactions? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStatement(statement)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
