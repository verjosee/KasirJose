package com.example.kasirjose.data.repository

import com.example.kasirjose.data.database.ProductDao
import com.example.kasirjose.data.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<Product>> = productDao.getAllProducts().flowOn(Dispatchers.IO)
    val productCount: Flow<Int> = productDao.getProductCount().flowOn(Dispatchers.IO)
    val totalStock: Flow<Int> = productDao.getTotalStock().flowOn(Dispatchers.IO)

    fun getLowStockProducts(threshold: Int = 5): Flow<List<Product>> =
        productDao.getLowStockProducts(threshold).flowOn(Dispatchers.IO)

    fun getLowestStockProducts(limit: Int = 5): Flow<List<Product>> =
        productDao.getLowestStockProducts(limit).flowOn(Dispatchers.IO)

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product): Long = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    fun searchProduct(query: String): Flow<List<Product>> =
        productDao.searchProduct(query).flowOn(Dispatchers.IO)

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        if (productDao.getCountSync() == 0) {
            val sampleProducts = listOf(
                Product(nama = "Indomie Goreng", harga = 3500.0, stok = 25),
                Product(nama = "Aqua 600ml", harga = 4000.0, stok = 18),
                Product(nama = "Teh Botol Sosro", harga = 5000.0, stok = 12),
                Product(nama = "Roti Sari Roti", harga = 8500.0, stok = 4),
                Product(nama = "Kopi Kapal Api", harga = 6000.0, stok = 3),
                Product(nama = "Susu Ultra 250ml", harga = 7000.0, stok = 15),
                Product(nama = "Chitato Sapi Panggang", harga = 11000.0, stok = 8),
                Product(nama = "Biskuit Oreo", harga = 9500.0, stok = 0)
            )
            productDao.insertAll(sampleProducts)
        }
    }
}
