package com.example.kasirjose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirjose.data.database.TransactionWithItemCount
import com.example.kasirjose.data.entity.SaleTransaction
import com.example.kasirjose.data.entity.TransactionDetailItem
import com.example.kasirjose.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<TransactionWithItemCount>> =
        transactionRepository.allTransactionsWithItemCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTransaction = MutableStateFlow<TransactionWithItemCount?>(null)
    val selectedTransaction: StateFlow<TransactionWithItemCount?> = _selectedTransaction.asStateFlow()

    val selectedTransactionDetails: StateFlow<List<TransactionDetailItem>> = _selectedTransaction
        .flatMapLatest { transaction ->
            if (transaction != null) {
                transactionRepository.getDetailsByTransactionId(transaction.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTransaction(transaction: TransactionWithItemCount) {
        _selectedTransaction.value = transaction
    }

    fun dismissDetailsDialog() {
        _selectedTransaction.value = null
    }
}
