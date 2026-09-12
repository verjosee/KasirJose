package com.example.kasirjose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirjose.data.entity.Product
import com.example.kasirjose.data.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val products: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allProducts
            } else {
                repository.searchProduct(query.trim())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen.asStateFlow()

    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog() {
        _editingProduct.value = null
        _isDialogOpen.value = true
    }

    fun openEditDialog(product: Product) {
        _editingProduct.value = product
        _isDialogOpen.value = true
    }

    fun closeDialog() {
        _isDialogOpen.value = false
        _editingProduct.value = null
    }

    fun openDeleteDialog(product: Product) {
        _productToDelete.value = product
    }

    fun closeDeleteDialog() {
        _productToDelete.value = null
    }

    fun saveProduct(
        nama: String,
        hargaText: String,
        stokText: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val trimmedName = nama.trim()
        if (trimmedName.isBlank()) {
            onError("Nama produk tidak boleh kosong")
            return
        }

        val harga = hargaText.trim().toDoubleOrNull()
        if (harga == null || harga <= 0) {
            onError("Harga harus berupa angka lebih besar dari 0")
            return
        }

        val stok = stokText.trim().toIntOrNull()
        if (stok == null || stok < 0) {
            onError("Stok harus berupa angka dan tidak boleh negatif")
            return
        }

        viewModelScope.launch {
            val currentEditing = _editingProduct.value
            if (currentEditing != null) {
                val updatedProduct = currentEditing.copy(
                    nama = trimmedName,
                    harga = harga,
                    stok = stok
                )
                repository.updateProduct(updatedProduct)
            } else {
                val newProduct = Product(
                    nama = trimmedName,
                    harga = harga,
                    stok = stok
                )
                repository.insertProduct(newProduct)
            }
            closeDialog()
            onSuccess()
        }
    }

    fun confirmDelete(onSuccess: () -> Unit) {
        val product = _productToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteProduct(product)
            closeDeleteDialog()
            onSuccess()
        }
    }
}
