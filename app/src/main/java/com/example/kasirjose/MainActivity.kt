package com.example.kasirjose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.kasirjose.data.database.AppDatabase
import com.example.kasirjose.data.repository.ProductRepository
import com.example.kasirjose.data.repository.TransactionRepository
import com.example.kasirjose.ui.navigation.AppNavigation
import com.example.kasirjose.ui.theme.KasirJoseTheme
import com.example.kasirjose.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val productRepository = ProductRepository(database.productDao())
        val transactionRepository = TransactionRepository(database)

        lifecycleScope.launch(Dispatchers.IO) {
            productRepository.checkAndSeedInitialData()
        }

        val viewModelFactory = ViewModelFactory(productRepository, transactionRepository)

        setContent {
            KasirJoseTheme {
                AppNavigation(viewModelFactory = viewModelFactory)
            }
        }
    }
}