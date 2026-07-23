package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "Category")
@JsonClass(generateAdapter = true)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
