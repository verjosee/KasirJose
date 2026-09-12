package com.example.kasirjose.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction",
    indices = [
        Index(value = ["tanggal"])
    ]
)
data class SaleTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: Long,
    val totalBayar: Double
)
