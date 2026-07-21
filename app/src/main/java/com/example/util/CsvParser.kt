package com.example.util

import com.example.data.Transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object CsvParser {
    fun parseCsv(csvText: String, statementId: Long): List<Transaction> {
        val lines = csvText.lineSequence().toList()
        if (lines.isEmpty()) return emptyList()
        
        val transactions = mutableListOf<Transaction>()
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH)
        
        // Skip the first header row if not empty
        val startRow = if (lines.size > 1) 1 else 0
        
        for (i in startRow until lines.size) {
            val line = lines[i]
            if (line.trim().isEmpty()) continue
            
            val columns = parseCsvLine(line)
            if (columns.size < 4) continue
            
            try {
                // Column 0: Date
                val dateStr = columns[0].trim().removeSurrounding("\"")
                val date = LocalDate.parse(dateStr, formatter)
                
                // Column 1: Description
                val description = columns[1].trim().removeSurrounding("\"")
                
                // Column 2: Card Member
                val cardMember = columns[2].trim().removeSurrounding("\"")
                
                // Column 3: Amount
                val amountStr = columns[3].trim()
                    .removeSurrounding("\"")
                    .replace("$", "")
                    .replace(",", "")
                val amount = amountStr.toDouble()
                
                transactions.add(
                    Transaction(
                        date = date,
                        description = description,
                        cardMember = cardMember,
                        amount = amount,
                        statementId = statementId,
                        categoryId = null,
                        hasConflict = false
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return transactions
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.setLength(0)
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }
}
