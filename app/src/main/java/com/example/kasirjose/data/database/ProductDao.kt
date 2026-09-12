package com.example.kasirjose.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kasirjose.data.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM product ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>): List<Long>

    @Update
    suspend fun updateProduct(product: Product): Int

    @Delete
    suspend fun deleteProduct(product: Product): Int

    @Query("SELECT * FROM product WHERE nama LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchProduct(query: String): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM product")
    fun getProductCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(stok), 0) FROM product")
    fun getTotalStock(): Flow<Int>

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getCountSync(): Int

    @Query("UPDATE product SET stok = stok - :jumlah WHERE id = :id AND stok >= :jumlah")
    suspend fun reduceStock(id: Int, jumlah: Int): Int

    @Query("SELECT * FROM product WHERE stok <= :threshold ORDER BY stok ASC")
    fun getLowStockProducts(threshold: Int = 5): Flow<List<Product>>

    @Query("SELECT * FROM product ORDER BY stok ASC LIMIT :limit")
    fun getLowestStockProducts(limit: Int = 5): Flow<List<Product>>
}
