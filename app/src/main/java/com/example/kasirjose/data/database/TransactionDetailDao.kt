package com.example.kasirjose.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kasirjose.data.entity.TransactionDetail
import com.example.kasirjose.data.entity.TransactionDetailItem
import kotlinx.coroutines.flow.Flow

data class BestSellingProductItem(
    val productId: Int,
    val namaProduk: String,
    val totalTerjual: Int,
    val totalPendapatan: Double
)

@Dao
interface TransactionDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionDetail(detail: TransactionDetail): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(details: List<TransactionDetail>): List<Long>

    @Query("""
        SELECT td.id, td.transactionId, td.productId, p.nama AS namaProduk, p.harga AS hargaSatuan, td.jumlah, td.subtotal
        FROM transaction_detail td
        JOIN product p ON td.productId = p.id
        WHERE td.transactionId = :transactionId
    """)
    fun getDetailsByTransactionId(transactionId: Int): Flow<List<TransactionDetailItem>>

    @Query("""
        SELECT td.id, td.transactionId, td.productId, p.nama AS namaProduk, p.harga AS hargaSatuan, td.jumlah, td.subtotal
        FROM transaction_detail td
        JOIN product p ON td.productId = p.id
        WHERE td.transactionId = :transactionId
    """)
    suspend fun getDetailsByTransactionIdSync(transactionId: Int): List<TransactionDetailItem>

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM transaction_detail")
    fun getTotalItemsSold(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(td.jumlah), 0)
        FROM transaction_detail td
        JOIN `transaction` t ON td.transactionId = t.id
        WHERE t.tanggal >= :startOfDay AND t.tanggal <= :endOfDay
    """)
    fun getTodayItemsSold(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("""
        SELECT td.productId, p.nama AS namaProduk, COALESCE(SUM(td.jumlah), 0) AS totalTerjual, COALESCE(SUM(td.subtotal), 0.0) AS totalPendapatan
        FROM transaction_detail td
        JOIN product p ON td.productId = p.id
        GROUP BY td.productId
        ORDER BY totalTerjual DESC
        LIMIT :limit
    """)
    fun getBestSellingProducts(limit: Int = 5): Flow<List<BestSellingProductItem>>
}
