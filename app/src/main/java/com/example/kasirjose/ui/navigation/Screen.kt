package com.example.kasirjose.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Product : Screen("product", "Produk", Icons.Default.Inventory2)
    object Cashier : Screen("cashier", "Kasir", Icons.Default.ShoppingCart)
    object TransactionHistory : Screen("transaction", "Riwayat", Icons.AutoMirrored.Filled.ReceiptLong)
    object Report : Screen("report", "Laporan", Icons.Default.Assessment)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Product,
    Screen.Cashier,
    Screen.TransactionHistory,
    Screen.Report
)
