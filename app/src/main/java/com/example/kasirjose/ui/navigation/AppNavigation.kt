package com.example.kasirjose.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kasirjose.ui.screen.cashier.CashierScreen
import com.example.kasirjose.ui.screen.dashboard.DashboardScreen
import com.example.kasirjose.ui.screen.product.ProductScreen
import com.example.kasirjose.ui.screen.report.ReportScreen
import com.example.kasirjose.ui.screen.transaction.TransactionHistoryScreen
import com.example.kasirjose.viewmodel.CashierViewModel
import com.example.kasirjose.viewmodel.DashboardViewModel
import com.example.kasirjose.viewmodel.ProductViewModel
import com.example.kasirjose.viewmodel.ReportViewModel
import com.example.kasirjose.viewmodel.TransactionViewModel
import com.example.kasirjose.viewmodel.ViewModelFactory

@Composable
fun AppNavigation(
    viewModelFactory: ViewModelFactory,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val dashboardViewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
    val productViewModel: ProductViewModel = viewModel(factory = viewModelFactory)
    val cashierViewModel: CashierViewModel = viewModel(factory = viewModelFactory)
    val transactionViewModel: TransactionViewModel = viewModel(factory = viewModelFactory)
    val reportViewModel: ReportViewModel = viewModel(factory = viewModelFactory)

    val onNavigateToTab: (String) -> Unit = remember(navController, currentRoute) {
        { route ->
            if (currentRoute != route) {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = { onNavigateToTab(screen.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToCashier = { onNavigateToTab(Screen.Cashier.route) },
                    onNavigateToProduct = { onNavigateToTab(Screen.Product.route) },
                    onNavigateToTransaction = { onNavigateToTab(Screen.TransactionHistory.route) },
                    onNavigateToReport = { onNavigateToTab(Screen.Report.route) }
                )
            }
            composable(Screen.Product.route) {
                ProductScreen(viewModel = productViewModel)
            }
            composable(Screen.Cashier.route) {
                CashierScreen(viewModel = cashierViewModel)
            }
            composable(Screen.TransactionHistory.route) {
                TransactionHistoryScreen(viewModel = transactionViewModel)
            }
            composable(Screen.Report.route) {
                ReportScreen(viewModel = reportViewModel)
            }
        }
    }
}
