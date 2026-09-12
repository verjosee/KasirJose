package com.example.kasirjose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirjose.data.entity.Product
import com.example.kasirjose.data.repository.CheckoutCartItem
import com.example.kasirjose.data.repository.ProductRepository
import com.example.kasirjose.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.harga * quantity
}

sealed interface CashierUiEvent {
    data class ShowSnackbar(val message: String) : CashierUiEvent
    data class CheckoutSuccess(val transactionId: Int, val totalBayar: Double) : CashierUiEvent
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CashierViewModel(
    productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val availableProducts: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                productRepository.allProducts
            } else {
                productRepository.searchProduct(query.trim())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val totalBayar: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _eventFlow = MutableSharedFlow<CashierUiEvent>(extraBufferCapacity = 1)
    val eventFlow: SharedFlow<CashierUiEvent> = _eventFlow.asSharedFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addToCart(product: Product) {
        if (product.stok <= 0) {
            viewModelScope.launch {
                _eventFlow.emit(CashierUiEvent.ShowSnackbar("Stok ${product.nama} habis (0)"))
            }
            return
        }

        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }

        if (index != -1) {
            val existing = currentList[index]
            if (existing.quantity < product.stok) {
                currentList[index] = existing.copy(quantity = existing.quantity + 1)
                _cartItems.value = currentList
            } else {
                viewModelScope.launch {
                    _eventFlow.emit(CashierUiEvent.ShowSnackbar("Jumlah melebihi stok tersedia (${product.stok})"))
                }
            }
        } else {
            currentList.add(CartItem(product = product, quantity = 1))
            _cartItems.value = currentList
        }
    }

    fun increaseQuantity(item: CartItem) {
        if (item.quantity < item.product.stok) {
            val currentList = _cartItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.product.id == item.product.id }
            if (index != -1) {
                currentList[index] = item.copy(quantity = item.quantity + 1)
                _cartItems.value = currentList
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(CashierUiEvent.ShowSnackbar("Maksimal stok tercapai (${item.product.stok})"))
            }
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == item.product.id }
        if (index != -1) {
            if (item.quantity > 1) {
                currentList[index] = item.copy(quantity = item.quantity - 1)
                _cartItems.value = currentList
            } else {
                currentList.removeAt(index)
                _cartItems.value = currentList
            }
        }
    }

    fun removeFromCart(item: CartItem) {
        val currentList = _cartItems.value.toMutableList()
        currentList.removeAll { it.product.id == item.product.id }
        _cartItems.value = currentList
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkout() {
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) {
            viewModelScope.launch {
                _eventFlow.emit(CashierUiEvent.ShowSnackbar("Keranjang belanja masih kosong!"))
            }
            return
        }

        val checkoutItems = currentCart.map {
            CheckoutCartItem(
                productId = it.product.id,
                nama = it.product.nama,
                harga = it.product.harga,
                jumlah = it.quantity,
                subtotal = it.subtotal
            )
        }
        val total = currentCart.sumOf { it.subtotal }

        viewModelScope.launch {
            val result = transactionRepository.checkout(checkoutItems, total)
            result.onSuccess { transactionId ->
                clearCart()
                _eventFlow.emit(CashierUiEvent.CheckoutSuccess(transactionId, total))
                _eventFlow.emit(CashierUiEvent.ShowSnackbar("Transaksi berhasil! Total: Rp ${total.toLong()}"))
            }.onFailure { error ->
                _eventFlow.emit(CashierUiEvent.ShowSnackbar(error.message ?: "Terjadi kesalahan saat transaksi"))
            }
        }
    }
}
