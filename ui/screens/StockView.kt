package com.atoz.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*

@Composable
fun StockView(viewModel: PosViewModel) {
    val products by viewModel.products.collectAsState()
    val sixMonthsAgo = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = RoyalBlue,
                contentColor = PureWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(products) { product ->
                val isDeadStock = product.lastSoldTimestamp < sixMonthsAgo

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("স্টক: ${product.stockQuantity}", fontWeight = FontWeight.Bold, color = RoyalBlue)
                        }

                        Text("ক্রয়: ৳${product.purchasePrice} | বিক্রয়: ৳${product.sellingPrice} | MRP: ৳${product.mrp}", fontSize = 12.sp, color = SlateGray)

                        if (isDeadStock) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "⚠️ সতর্কতা: বিগত ৬ মাস ধরে এই পণ্যটি একবারও বিক্রি হয়নি!",
                                color = CrimsonRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.addStock(product, 5) }) {
                                Text("+৫")
                            }
                            OutlinedButton(onClick = { viewModel.addStock(product, 10) }) {
                                Text("+১০")
                            }
                            OutlinedButton(onClick = { viewModel.addStock(product, 50) }) {
                                Text("+৫০")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, barcode, pPrice, mrp, sPrice, stock ->
                viewModel.saveProduct(name, barcode, pPrice, mrp, sPrice, stock)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var mrp by remember { mutableStateOf("") }
    var sPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন পণ্য যোগ করুন") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("পণ্যের নাম") })
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("বারকোড") })
                OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("ক্রয় মূল্য") })
                OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP") })
                OutlinedTextField(value = sPrice, onValueChange = { sPrice = it }, label = { Text("বিক্রয় মূল্য") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("স্টক সংখ্যা") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name,
                        barcode,
                        pPrice.toDoubleOrNull() ?: 0.0,
                        mrp.toDoubleOrNull() ?: 0.0,
                        sPrice.toDoubleOrNull() ?: 0.0,
                        stock.toIntOrNull() ?: 0
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("সংরক্ষণ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
