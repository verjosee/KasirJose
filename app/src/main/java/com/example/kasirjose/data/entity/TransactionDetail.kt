package com.example.kasirjose.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_detail",
    foreignKeys = [
        ForeignKey(
            entity = SaleTransaction::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("transactionId"),
        Index("productId")
    ]
)
data class TransactionDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val transactionId: Int,
    val productId: Int,
    val jumlah: Int,
    val subtotal: Double
)

data class TransactionDetailItem(
    val id: Int,
    val transactionId: Int,
    val productId: Int,
    val namaProduk: String,
    val hargaSatuan: Double,
    val jumlah: Int,
    val subtotal: Double
)
