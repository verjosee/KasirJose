package com.example.kasirjose.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kasirjose.data.entity.SaleTransaction
import kotlinx.coroutines.flow.Flow

data class TransactionWithItemCount(
    val id: Int,
    val tanggal: Long,
    val totalBayar: Double,
    val totalItem: Int
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` ORDER BY tanggal DESC")
    fun getAllTransactions(): Flow<List<SaleTransaction>>

    @Query("""
        SELECT t.id, t.tanggal, t.totalBayar, COALESCE(SUM(td.jumlah), 0) AS totalItem
        FROM `transaction` t
        LEFT JOIN transaction_detail td ON t.id = td.transactionId
        GROUP BY t.id
        ORDER BY t.tanggal DESC
    """)
    fun getAllTransactionsWithItemCount(): Flow<List<TransactionWithItemCount>>

    @Query("""
        SELECT t.id, t.tanggal, t.totalBayar, COALESCE(SUM(td.jumlah), 0) AS totalItem
        FROM `transaction` t
        LEFT JOIN transaction_detail td ON t.id = td.transactionId
        GROUP BY t.id
        ORDER BY t.tanggal DESC
        LIMIT :limit
    """)
    fun getRecentTransactionsWithItemCount(limit: Int = 5): Flow<List<TransactionWithItemCount>>

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    suspend fun getTransactionById(id: Int): SaleTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SaleTransaction): Long

    @Query("SELECT COUNT(*) FROM `transaction`")
    fun getTransactionCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalBayar), 0.0) FROM `transaction`")
    fun getTotalRevenue(): Flow<Double>

    @Query("SELECT COUNT(*) FROM `transaction` WHERE tanggal >= :startOfDay AND tanggal <= :endOfDay")
    fun getTodayTransactionCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalBayar), 0.0) FROM `transaction` WHERE tanggal >= :startOfDay AND tanggal <= :endOfDay")
    fun getTodayRevenue(startOfDay: Long, endOfDay: Long): Flow<Double>
}
