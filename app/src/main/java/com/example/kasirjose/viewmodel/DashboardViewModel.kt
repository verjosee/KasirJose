package com.example.kasirjose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirjose.data.database.TransactionWithItemCount
import com.example.kasirjose.data.entity.Product
import com.example.kasirjose.data.repository.ProductRepository
import com.example.kasirjose.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    productRepository: ProductRepository,
    transactionRepository: TransactionRepository
) : ViewModel() {

    val totalProduct: StateFlow<Int> = productRepository.productCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStock: StateFlow<Int> = productRepository.totalStock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTransactionCount: StateFlow<Int> = transactionRepository.getTodayTransactionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayRevenue: StateFlow<Double> = transactionRepository.getTodayRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalTransaction: StateFlow<Int> = transactionRepository.transactionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue: StateFlow<Double> = transactionRepository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val lowStockProducts: StateFlow<List<Product>> = productRepository.getLowStockProducts(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionWithItemCount>> =
        transactionRepository.getRecentTransactionsWithItemCount(5)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
