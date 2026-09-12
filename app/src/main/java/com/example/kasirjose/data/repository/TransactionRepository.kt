package com.example.kasirjose.data.repository

import androidx.room.withTransaction
import com.example.kasirjose.data.database.AppDatabase
import com.example.kasirjose.data.database.BestSellingProductItem
import com.example.kasirjose.data.database.TransactionWithItemCount
import com.example.kasirjose.data.entity.SaleTransaction
import com.example.kasirjose.data.entity.TransactionDetail
import com.example.kasirjose.data.entity.TransactionDetailItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar

data class CheckoutCartItem(
    val productId: Int,
    val nama: String,
    val harga: Double,
    val jumlah: Int,
    val subtotal: Double
)

class TransactionRepository(
    private val database: AppDatabase
) {
    private val transactionDao = database.transactionDao()
    private val transactionDetailDao = database.transactionDetailDao()
    private val productDao = database.productDao()

    val allTransactions: Flow<List<SaleTransaction>> =
        transactionDao.getAllTransactions().flowOn(Dispatchers.IO)

    val allTransactionsWithItemCount: Flow<List<TransactionWithItemCount>> =
        transactionDao.getAllTransactionsWithItemCount().flowOn(Dispatchers.IO)

    val transactionCount: Flow<Int> =
        transactionDao.getTransactionCount().flowOn(Dispatchers.IO)

    val totalRevenue: Flow<Double> =
        transactionDao.getTotalRevenue().flowOn(Dispatchers.IO)

    fun getRecentTransactionsWithItemCount(limit: Int = 5): Flow<List<TransactionWithItemCount>> =
        transactionDao.getRecentTransactionsWithItemCount(limit).flowOn(Dispatchers.IO)

    fun getTodayTransactionCount(): Flow<Int> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayTransactionCount(start, end).flowOn(Dispatchers.IO)
    }

    fun getTodayRevenue(): Flow<Double> {
        val (start, end) = getTodayRange()
        return transactionDao.getTodayRevenue(start, end).flowOn(Dispatchers.IO)
    }

    fun getTodayItemsSold(): Flow<Int> {
        val (start, end) = getTodayRange()
        return transactionDetailDao.getTodayItemsSold(start, end).flowOn(Dispatchers.IO)
    }

    fun getTotalItemsSold(): Flow<Int> =
        transactionDetailDao.getTotalItemsSold().flowOn(Dispatchers.IO)

    fun getBestSellingProducts(limit: Int = 5): Flow<List<BestSellingProductItem>> =
        transactionDetailDao.getBestSellingProducts(limit).flowOn(Dispatchers.IO)

    fun getDetailsByTransactionId(transactionId: Int): Flow<List<TransactionDetailItem>> =
        transactionDetailDao.getDetailsByTransactionId(transactionId).flowOn(Dispatchers.IO)

    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    suspend fun checkout(items: List<CheckoutCartItem>, totalBayar: Double): Result<Int> =
        withContext(Dispatchers.IO) {
            if (items.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Keranjang belanja kosong"))
            }

            try {
                val transactionId = database.withTransaction {
                    for (item in items) {
                        val currentProduct = productDao.getProductById(item.productId)
                            ?: throw IllegalStateException("Produk ${item.nama} tidak ditemukan")

                        if (currentProduct.stok < item.jumlah) {
                            throw IllegalStateException("Stok ${item.nama} tidak mencukupi (sisa ${currentProduct.stok})")
                        }

                        val updatedRows = productDao.reduceStock(item.productId, item.jumlah)
                        if (updatedRows == 0) {
                            throw IllegalStateException("Gagal mengurangi stok ${item.nama}")
                        }
                    }

                    val newTransaction = SaleTransaction(
                        tanggal = System.currentTimeMillis(),
                        totalBayar = totalBayar
                    )
                    val insertedId = transactionDao.insertTransaction(newTransaction).toInt()

                    val detailEntities = items.map { item ->
                        TransactionDetail(
                            transactionId = insertedId,
                            productId = item.productId,
                            jumlah = item.jumlah,
                            subtotal = item.subtotal
                        )
                    }
                    transactionDetailDao.insertAll(detailEntities)

                    insertedId
                }
                Result.success(transactionId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
