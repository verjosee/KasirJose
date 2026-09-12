package com.example.kasirjose.ui.screen.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kasirjose.data.entity.Product
import com.example.kasirjose.ui.component.EmptyStateView
import com.example.kasirjose.ui.theme.StatusDanger
import com.example.kasirjose.ui.theme.StatusDangerBg
import com.example.kasirjose.ui.theme.StatusSuccess
import com.example.kasirjose.ui.theme.StatusSuccessBg
import com.example.kasirjose.ui.theme.StatusWarning
import com.example.kasirjose.ui.theme.StatusWarningBg
import com.example.kasirjose.util.FormatHelper
import com.example.kasirjose.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

enum class StockStatus(val label: String, val textColor: Color, val bgColor: Color) {
    AMAN("Aman", StatusSuccess, StatusSuccessBg),
    MENIPIS("Menipis", StatusWarning, StatusWarningBg),
    HABIS("Habis", StatusDanger, StatusDangerBg)
}

fun getStockStatus(stok: Int): StockStatus {
    return when {
        stok <= 0 -> StockStatus.HABIS
        stok <= 5 -> StockStatus.MENIPIS
        else -> StockStatus.AMAN
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isDialogOpen by viewModel.isDialogOpen.collectAsStateWithLifecycle()
    val editingProduct by viewModel.editingProduct.collectAsStateWithLifecycle()
    val productToDelete by viewModel.productToDelete.collectAsStateWithLifecycle()

    var productForDetail by remember { mutableStateOf<Product?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Daftar Produk",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Cari produk...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Cari")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { viewModel.openAddDialog() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah")
                }
            }

            if (products.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Inventory2,
                    title = if (searchQuery.isNotBlank()) "Produk Tidak Ditemukan" else "Belum Ada Produk",
                    subtitle = if (searchQuery.isNotBlank()) "Coba gunakan kata kunci pencarian yang lain" else "Klik tombol Tambah untuk memasukkan produk baru",
                    actionLabel = if (searchQuery.isNotBlank()) "Reset Pencarian" else "Tambah Produk",
                    onActionClick = {
                        if (searchQuery.isNotBlank()) viewModel.onSearchQueryChanged("") else viewModel.openAddDialog()
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "${products.size} produk tersedia",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onCardClick = { productForDetail = product },
                            onEdit = { viewModel.openEditDialog(product) },
                            onDelete = { viewModel.openDeleteDialog(product) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (isDialogOpen) {
        ProductFormDialog(
            product = editingProduct,
            onDismiss = { viewModel.closeDialog() },
            onSave = { nama, harga, stok, onError ->
                viewModel.saveProduct(
                    nama = nama,
                    hargaText = harga,
                    stokText = stok,
                    onError = onError,
                    onSuccess = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (editingProduct != null) "Produk berhasil diperbarui" else "Produk berhasil ditambahkan"
                            )
                        }
                    }
                )
            }
        )
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { viewModel.closeDeleteDialog() },
            title = { Text("Hapus Produk?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Apakah kamu yakin ingin menghapus \"${product.nama}\"? Data yang dihapus tidak dapat dipulihkan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmDelete(
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Produk \"${product.nama}\" dihapus")
                                }
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.closeDeleteDialog() }) {
                    Text("Batal")
                }
            }
        )
    }

    productForDetail?.let { product ->
        ProductDetailDialog(
            product = product,
            onDismiss = { productForDetail = null },
            onEdit = {
                productForDetail = null
                viewModel.openEditDialog(product)
            }
        )
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onCardClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = getStockStatus(product.stok)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = FormatHelper.formatRupiah(product.harga),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(status.bgColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = status.textColor
                        )
                    }

                    Text(
                        text = "Stok: ${product.stok} unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val status = getStockStatus(product.stok)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Detail Produk",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kode Produk", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("#${product.id}", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nama", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(product.nama, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Harga Jual", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        FormatHelper.formatRupiah(product.harga),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sisa Stok", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("${product.stok} unit", fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(status.bgColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = status.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = status.textColor
                            )
                        }
                    }
                }

                HorizontalDivider()
            }
        },
        confirmButton = {
            Button(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun ProductFormDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (nama: String, harga: String, stok: String, onError: (String) -> Unit) -> Unit
) {
    var nama by remember { mutableStateOf(product?.nama ?: "") }
    var harga by remember {
        mutableStateOf(product?.harga?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var stok by remember { mutableStateOf(product?.stok?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = nama.trim().isNotBlank() &&
            (harga.trim().toDoubleOrNull() ?: -1.0) > 0 &&
            (stok.trim().toIntOrNull() ?: -1) >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (product == null) "Tambah Produk" else "Edit Produk",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        errorMessage = null
                    },
                    label = { Text("Nama Produk *") },
                    placeholder = { Text("Contoh: Es Kopi Susu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = harga,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            harga = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Harga (Rp) *") },
                    placeholder = { Text("Contoh: 15000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stok,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            stok = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Stok *") },
                    placeholder = { Text("Contoh: 20") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(nama, harga, stok) { err ->
                        errorMessage = err
                    }
                },
                enabled = isFormValid
            ) {
                Text(if (product == null) "Tambah" else "Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
