package com.example.kasirjose.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val harga: Double,
    val stok: Int
)
