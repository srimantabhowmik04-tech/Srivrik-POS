package com.atoz.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*

@Composable
fun CustomersView(viewModel: PosViewModel) {
    val customers by viewModel.customers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = RoyalBlue, contentColor = PureWhite) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(customers) { customer ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("ফোন: ${customer.phone}", fontSize = 13.sp, color = SlateGray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("মোট বাকি", fontSize = 11.sp, color = SlateGray)
                            Text("৳${customer.totalDue}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CrimsonRed)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var due by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("নতুন গ্রাহক যোগ করুন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("নাম") })
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("ফোন নম্বর") })
                    OutlinedTextField(value = due, onValueChange = { due = it }, label = { Text("পূর্বের বাকি (যদি থাকে)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCustomer(name, phone, due.toDoubleOrNull() ?: 0.0)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("সংরক্ষণ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
