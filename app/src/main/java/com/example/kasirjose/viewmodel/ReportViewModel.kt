package com.example.kasirjose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirjose.data.database.BestSellingProductItem
import com.example.kasirjose.data.entity.Product
import com.example.kasirjose.data.repository.ProductRepository
import com.example.kasirjose.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ReportViewModel(
    productRepository: ProductRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {

    val todayRevenue: StateFlow<Double> = transactionRepository.getTodayRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTransactionCount: StateFlow<Int> = transactionRepository.getTodayTransactionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayItemsSold: StateFlow<Int> = transactionRepository.getTodayItemsSold()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue: StateFlow<Double> = transactionRepository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalTransactions: StateFlow<Int> = transactionRepository.transactionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalItemsSold: StateFlow<Int> = transactionRepository.getTotalItemsSold()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestSellingProducts: StateFlow<List<BestSellingProductItem>> =
        transactionRepository.getBestSellingProducts(10)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowestStockProducts: StateFlow<List<Product>> =
        productRepository.getLowestStockProducts(10)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
