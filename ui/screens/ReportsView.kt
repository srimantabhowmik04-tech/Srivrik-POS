package com.atoz.pos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*

@Composable
fun ReportsView(viewModel: PosViewModel) {
    val invoices by viewModel.invoices.collectAsState()

    val totalSales = invoices.sumOf { it.totalAmount }
    val cashSales = invoices.filter { it.paymentMode == "CASH" }.sumOf { it.totalAmount }
    val upiSales = invoices.filter { it.paymentMode == "UPI" }.sumOf { it.totalAmount }
    val dueSales = invoices.filter { it.paymentMode == "DUE" }.sumOf { it.totalAmount }

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = RoyalBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("সর্বমোট বিক্রি", color = PureWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("৳$totalSales", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Text("মোট ইনভয়েস: ${invoices.size} টি", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("নগদ ক্যাশ", fontSize = 12.sp, color = SlateGray)
                    Text("৳$cashSales", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 16.sp)
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("অনলাইন UPI", fontSize = 12.sp, color = SlateGray)
                    Text("৳$upiSales", fontWeight = FontWeight.Bold, color = RoyalBlue, fontSize = 16.sp)
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("বাকি হিসাব", fontSize = 12.sp, color = SlateGray)
                    Text("৳$dueSales", fontWeight = FontWeight.Bold, color = CrimsonRed, fontSize = 16.sp)
                }
            }
        }
    }
}
