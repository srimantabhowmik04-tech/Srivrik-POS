package com.atoz.pos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.atoz.pos.data.model.Product
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*
import com.atoz.pos.util.PosUtils

@Composable
fun BillingView(viewModel: PosViewModel) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val profile by viewModel.storeProfile.collectAsState()
    val context = LocalContext.current

    var customerPhone by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showQrDialog by remember { mutableStateOf(false) }

    val totalAmount = cart.sumOf { it.product.sellingPrice * it.quantity }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.barcode.contains(searchQuery)
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("পণ্য খুঁজুন বা বারকোড টাইপ করুন") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // পণ্যের তালিকা (কার্টে যোগ করার জন্য)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredProducts) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("দাম: ৳${product.sellingPrice} | স্টক: ${product.stockQuantity}", color = SlateGray, fontSize = 13.sp)
                        }
                        Button(
                            onClick = { viewModel.addToCart(product) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("যোগ +", color = PureWhite)
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // কার্ট সারাংশ
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("মোট আইটেম: ${cart.sumOf { it.quantity }}", fontWeight = FontWeight.Medium)
                    Text("মোট টাকা: ৳$totalAmount", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RoyalBlue)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("গ্রাহকের মোবাইল নম্বর") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.checkout(customerPhone, "CASH", true) { id ->
                                PosUtils.sendReceiptWhatsApp(context, customerPhone, "SriVik Store Invoice #$id: Total Paid ৳$totalAmount. Thank you!")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        enabled = cart.isNotEmpty()
                    ) {
                        Text("নগদ (Cash)")
                    }

                    Button(
                        onClick = { showQrDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        enabled = cart.isNotEmpty()
                    ) {
                        Text("UPI QR")
                    }

                    Button(
                        onClick = {
                            viewModel.checkout(customerPhone, "DUE", false) {}
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        enabled = cart.isNotEmpty() && customerPhone.isNotBlank()
                    ) {
                        Text("বাকি (Due)")
                    }
                }
            }
        }
    }

    if (showQrDialog) {
        val upiUri = PosUtils.generateUpiString(
            upiId = profile?.upiId ?: "example@upi",
            payeeName = profile?.storeName ?: "SriVik Store",
            amount = totalAmount,
            trRef = "INV${System.currentTimeMillis()}"
        )
        val qrBitmap = remember(upiUri) { PosUtils.generateQrBitmap(upiUri) }

        Dialog(onDismissRequest = { showQrDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("স্ক্যান করে পেমেন্ট করুন", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RoyalBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("পরিমাণ: ৳$totalAmount", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = CrimsonRed)
                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Payment QR",
                        modifier = Modifier.size(220.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.checkout(customerPhone, "UPI", true) { id ->
                                showQrDialog = false
                                PosUtils.sendReceiptWhatsApp(context, customerPhone, "SriVik Store: Online Payment Received ৳$totalAmount for Bill #$id.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("পেমেন্ট সম্পন্ন হয়েছে (Confirm & Print)")
                    }
                }
            }
        }
    }
}
