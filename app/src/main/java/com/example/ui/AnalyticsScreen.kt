package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Category
import com.example.data.CategoryFilter
import com.example.data.Statement
import com.example.data.Transaction
import com.example.ui.theme.ChartColor1
import com.example.ui.theme.ChartColor2
import com.example.ui.theme.ChartColor3
import com.example.ui.theme.ChartColor4
import com.example.ui.theme.ChartColor5
import com.example.ui.theme.ChartColor6
import com.example.ui.theme.ChartColor7
import com.example.ui.theme.ChartColor8
import com.example.ui.theme.ComfortTrendDown
import com.example.ui.theme.ComfortTrendUp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

private val chartColors = listOf(
    ChartColor1, ChartColor2, ChartColor3, ChartColor4,
    ChartColor5, ChartColor6, ChartColor7, ChartColor8
)

@Composable
fun MonthlyExpenditureSummaryCard(
    transactions: List<Transaction>,
    statements: List<Statement>,
    modifier: Modifier = Modifier
) {
    val monthData = remember(transactions, statements) {
        val statementsById = statements.associateBy { it.id }
        val netCosts = mutableMapOf<Long, Double>()
        
        for (tx in transactions) {
            val stmt = statementsById[tx.statementId]
            if (stmt != null) {
                netCosts[tx.statementId] = netCosts.getOrDefault(tx.statementId, 0.0) + tx.amount
            }
        }
        
        val costsByMonth = mutableMapOf<YearMonth, Double>()
        for ((stmtId, totalTxAmount) in netCosts) {
            val stmt = statementsById[stmtId] ?: continue
            val ym = parseMonthYear(stmt.monthYear) ?: continue
            val netCost = totalTxAmount - stmt.paymentsAndCredits
            costsByMonth[ym] = costsByMonth.getOrDefault(ym, 0.0) + netCost
        }
        
        costsByMonth.entries.sortedBy { it.key }.map { it.value }
    }

    val avgCost = if (monthData.isNotEmpty()) monthData.average() else 0.0
    val currentMonthCost = monthData.lastOrNull() ?: 0.0
    val prevMonthCost = if (monthData.size >= 2) monthData[monthData.size - 2] else 0.0

    val trendPercent = if (prevMonthCost > 0) {
        ((currentMonthCost - prevMonthCost) / prevMonthCost) * 100
    } else 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "AVG MONTHLY EXPENDITURE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format(Locale.US, "$%,.2f", avgCost),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (monthData.size >= 2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val color = if (trendPercent > 0) ComfortTrendUp else ComfortTrendDown
                    val iconText = if (trendPercent > 0) "↑" else "↓"
                    Text(
                        text = "$iconText ${String.format(Locale.US, "%.1f", Math.abs(trendPercent))}% from last month",
                        color = color,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (monthData.isNotEmpty()) {
                val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
                val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
                Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    val maxVal = monthData.maxOrNull() ?: 1.0
                    val minVal = monthData.minOrNull() ?: 0.0
                    val range = if (maxVal == minVal) 1.0 else maxVal - minVal
                    
                    val stepX = size.width / (if (monthData.size > 1) (monthData.size - 1) else 1)
                    val pts = monthData.mapIndexed { index, value ->
                        val x = index * stepX
                        val y = size.height - ((value - minVal) / range * size.height).toFloat()
                        Offset(x, y.toFloat())
                    }

                    val path = Path().apply {
                        pts.forEachIndexed { index, offset ->
                            if (index == 0) moveTo(offset.x, offset.y)
                            else lineTo(offset.x, offset.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = onPrimaryContainerColor,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                    
                    pts.forEach { pt ->
                        drawCircle(
                            color = primaryContainerColor,
                            radius = 6f,
                            center = pt
                        )
                        drawCircle(
                            color = onPrimaryContainerColor,
                            radius = 4f,
                            center = pt
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    filters: List<CategoryFilter>,
    statements: List<Statement>,
    preSelectedCategoryId: Int?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember { mutableStateOf(preSelectedCategoryId) }
    var selectedMember by remember { mutableStateOf<String?>(null) }
    var selectedTimeRange by remember { mutableStateOf("All Time") }
    var searchQuery by remember { mutableStateOf("") }

    val distinctMembers = remember(transactions) {
        transactions.map { it.cardMember }.distinct().sorted()
    }

    val filteredTransactions = remember(
        transactions, selectedCategoryId, selectedMember, selectedTimeRange, searchQuery
    ) {
        transactions.filter { tx ->
            val matchCat = selectedCategoryId == null || tx.categoryId == selectedCategoryId
            val matchMember = selectedMember == null || tx.cardMember == selectedMember
            val matchQuery = searchQuery.isBlank() || tx.description.contains(searchQuery, ignoreCase = true)
            
            val matchTime = when (selectedTimeRange) {
                "Last 30 Days" -> ChronoUnit.DAYS.between(tx.date, LocalDate.now()) <= 30
                "Last 3 Months" -> ChronoUnit.MONTHS.between(tx.date, LocalDate.now()) <= 3
                "Last 6 Months" -> ChronoUnit.MONTHS.between(tx.date, LocalDate.now()) <= 6
                "Last Year" -> ChronoUnit.YEARS.between(tx.date, LocalDate.now()) <= 1
                else -> true
            }

            matchCat && matchMember && matchQuery && matchTime
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Analytics", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Spending Breakdown & Trends", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Filter
                var catExpanded by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = selectedCategoryId != null,
                        onClick = { catExpanded = true },
                        label = {
                            val name = if (selectedCategoryId != null) {
                                categories.find { it.id == selectedCategoryId }?.name ?: "Unknown"
                            } else "All Categories"
                            Text(name)
                        }
                    )
                    DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = { selectedCategoryId = null; catExpanded = false }
                        )
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { selectedCategoryId = cat.id; catExpanded = false }
                            )
                        }
                    }
                }

                // Member Filter
                var memberExpanded by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = selectedMember != null,
                        onClick = { memberExpanded = true },
                        label = { Text(selectedMember ?: "All Members") }
                    )
                    DropdownMenu(expanded = memberExpanded, onDismissRequest = { memberExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("All Members") },
                            onClick = { selectedMember = null; memberExpanded = false }
                        )
                        distinctMembers.forEach { mem ->
                            DropdownMenuItem(
                                text = { Text(mem) },
                                onClick = { selectedMember = mem; memberExpanded = false }
                            )
                        }
                    }
                }

                // Time Range Filter
                var timeExpanded by remember { mutableStateOf(false) }
                val timeOptions = listOf("All Time", "Last 30 Days", "Last 3 Months", "Last 6 Months", "Last Year")
                Box {
                    FilterChip(
                        selected = selectedTimeRange != "All Time",
                        onClick = { timeExpanded = true },
                        label = { Text(selectedTimeRange) }
                    )
                    DropdownMenu(expanded = timeExpanded, onDismissRequest = { timeExpanded = false }) {
                        timeOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = { selectedTimeRange = opt; timeExpanded = false }
                            )
                        }
                    }
                }

                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.width(200.dp).height(50.dp),
                    placeholder = { Text("Search merchants...", style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (selectedCategoryId != null || selectedMember != null || selectedTimeRange != "All Time" || searchQuery.isNotBlank()) {
                    TextButton(onClick = {
                        selectedCategoryId = null
                        selectedMember = null
                        selectedTimeRange = "All Time"
                        searchQuery = ""
                    }) {
                        Text("Clear")
                    }
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val totalSpend = filteredTransactions.sumOf { it.amount }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TOTAL SPEND (FILTERED)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.primary))
                        Text(String.format(Locale.US, "$%,.2f", totalSpend), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                        Text("${filteredTransactions.size} transactions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }

                if (selectedCategoryId == null && filteredTransactions.isNotEmpty()) {
                    val catTotals = filteredTransactions.groupBy { tx ->
                        tx.categoryId?.let { id -> categories.find { it.id == id }?.name } ?: "Uncategorized"
                    }.mapValues { it.value.sumOf { tx -> tx.amount } }
                    
                    SpendingPieChart(categoryTotals = catTotals, totalSpend = totalSpend)
                }

                CategoryMonthlyBreakdownCard(
                    transactions = filteredTransactions,
                    statements = statements,
                    categories = categories,
                    selectedCategoryId = selectedCategoryId
                )
                
                val monthlyData = remember(filteredTransactions, statements) {
                    val stmtMap = statements.associateBy { it.id }
                    filteredTransactions.groupBy { tx ->
                        stmtMap[tx.statementId]?.monthYear ?: "Unknown"
                    }.mapValues { it.value.sumOf { tx -> tx.amount } }
                    .toList()
                    .sortedBy { parseMonthYear(it.first) ?: YearMonth.of(1970, 1) }
                }
                
                if (monthlyData.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Monthly Trend", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(16.dp))
                            MonthlyTrendChart(monthlyData = monthlyData)
                        }
                    }
                }

                TopMerchantsCard(transactions = filteredTransactions)
                RecurringChargesCard(transactions = filteredTransactions, statements = statements)
                BiggestExpensesCard(transactions = filteredTransactions)
            }
        }
    }
}

@Composable
fun SpendingPieChart(
    categoryTotals: Map<String, Double>,
    totalSpend: Double,
    modifier: Modifier = Modifier
) {
    val sortedCategories = categoryTotals.entries.sortedByDescending { it.value }.filter { it.value > 0 }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Spending Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(24.dp))
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    sortedCategories.forEachIndexed { index, entry ->
                        val sweepAngle = (entry.value / totalSpend * 360f).toFloat()
                        val color = chartColors[index % chartColors.size]
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 40f, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }
                Text(
                    String.format(Locale.US, "$%,.0f", totalSpend),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sortedCategories.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(12.dp).background(chartColors[index % chartColors.size], CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(entry.key, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(
                            String.format(Locale.US, "$%,.2f", entry.value),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            String.format(Locale.US, "%.0f%%", (entry.value / totalSpend * 100)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.width(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyTrendChart(
    monthlyData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) return
    val maxVal = monthlyData.maxOf { it.second }.toFloat()
    
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val barWidth = 40f
        val spacing = (size.width - (monthlyData.size * barWidth)) / (monthlyData.size + 1)
        
        // Grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = size.height * (i.toFloat() / gridLines)
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        monthlyData.forEachIndexed { index, data ->
            val barHeight = if (maxVal > 0) (data.second.toFloat() / maxVal) * (size.height - 40f) else 0f
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - 20f - barHeight

            drawRoundRect(
                color = ChartColor1,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
            
            // Note: Canvas text drawing is complex in pure Compose, we omit labels to keep it simple,
            // or we'd use nativeCanvas.
        }
    }
}

@Composable
fun CategoryMonthlyBreakdownCard(
    transactions: List<Transaction>,
    statements: List<Statement>,
    categories: List<Category>,
    selectedCategoryId: Int?,
    modifier: Modifier = Modifier
) {
    val data = remember(transactions, statements) {
        val stmtMap = statements.associateBy { it.id }
        transactions.groupBy { tx ->
            stmtMap[tx.statementId]?.monthYear ?: "Unknown"
        }.mapValues { it.value.sumOf { tx -> tx.amount } }
        .toList()
        .sortedByDescending { parseMonthYear(it.first) ?: YearMonth.of(1970, 1) }
    }

    if (data.isEmpty()) return
    val maxAmt = data.maxOf { it.second }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Monthly Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))
            
            data.forEach { (month, amt) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(month, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(80.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(String.format(Locale.US, "$%,.2f", amt), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.width(80.dp))
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.height(8.dp).weight(1f).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                        val fraction = if (maxAmt > 0) (amt / maxAmt).toFloat() else 0f
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).background(ChartColor2, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
fun TopMerchantsCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val topMerchants = remember(transactions) {
        transactions.groupBy { it.description }
            .map { it.key to it.value }
            .sortedByDescending { it.second.sumOf { tx -> tx.amount } }
            .take(5)
    }
    
    if (topMerchants.isEmpty()) return
    val maxAmt = topMerchants.maxOfOrNull { it.second.sumOf { tx -> tx.amount } } ?: 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Top Merchants", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Where your money goes most", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Spacer(Modifier.height(16.dp))
            
            topMerchants.forEachIndexed { index, (merchant, txs) ->
                val total = txs.sumOf { it.amount }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.width(24.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(merchant, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Box(modifier = Modifier.height(4.dp).fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                            val fraction = if (maxAmt > 0) (total / maxAmt).toFloat() else 0f
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).background(ChartColor1, CircleShape))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(String.format(Locale.US, "$%,.2f", total), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("(${txs.size} txns)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
fun RecurringChargesCard(
    transactions: List<Transaction>,
    statements: List<Statement>,
    modifier: Modifier = Modifier
) {
    val recurring = remember(transactions) {
        transactions.groupBy { it.description.trim().lowercase() }
            .mapValues { entry -> entry.value }
            .filter { it.value.map { tx -> tx.statementId }.distinct().size >= 2 }
            .filter { entry -> 
                val amounts = entry.value.map { it.amount }
                val mean = amounts.average()
                val stdDev = sqrt(amounts.map { (it - mean).pow(2) }.average())
                mean > 0 && (stdDev / mean) < 0.2
            }
            .toList()
            .sortedByDescending { it.second.map { tx -> tx.statementId }.distinct().size }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Recurring Charges", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Subscriptions & regular payments detected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
            Spacer(Modifier.height(16.dp))
            
            if (recurring.isEmpty()) {
                Text("No recurring charges detected", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            } else {
                recurring.forEach { (name, txs) ->
                    val avg = txs.map { it.amount }.average()
                    val total = txs.sumOf { it.amount }
                    val months = txs.map { it.statementId }.distinct().size
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(txs.first().description, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("$months months", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(String.format(Locale.US, "$%,.2f / mo", avg), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Total: ${String.format(Locale.US, "$%,.2f", total)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiggestExpensesCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val biggest = remember(transactions) {
        transactions.sortedByDescending { it.amount }.take(5)
    }

    if (biggest.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Biggest Expenses", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Largest individual purchases", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Spacer(Modifier.height(16.dp))
            
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            
            biggest.forEach { tx ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.description, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(tx.date.format(formatter), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Text(String.format(Locale.US, "$%,.2f", tx.amount), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

private fun parseMonthYear(monthYear: String): YearMonth? {
    try {
        val parts = monthYear.split(" ")
        if (parts.size != 2) return null
        val monthStr = parts[0]
        val yearStr = parts[1]
        
        val formatter = if (monthStr.length == 3) {
            DateTimeFormatter.ofPattern("MMM yyyy", Locale.US)
        } else {
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
        }
        return YearMonth.parse(monthYear, formatter)
    } catch (e: Exception) {
        return null
    }
}
