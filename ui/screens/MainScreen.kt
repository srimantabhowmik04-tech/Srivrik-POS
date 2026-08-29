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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.atoz.pos.data.model.Product
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*
import com.atoz.pos.util.PosUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PosViewModel) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var selectedStep by remember { mutableIntStateOf(1) }

    if (!isLoggedIn) {
        LoginScreen { isLoggedIn = true }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SriVik POS", fontWeight = FontWeight.Bold, color = PureWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RoyalBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = PureWhite) {
                NavigationBarItem(
                    selected = selectedStep == 1,
                    onClick = { selectedStep = 1 },
                    label = { Text("১. বিলিং") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedStep == 2,
                    onClick = { selectedStep = 2 },
                    label = { Text("২. স্টক") },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedStep == 3,
                    onClick = { selectedStep = 3 },
                    label = { Text("৩. বাকি খাতা") },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedStep == 4,
                    onClick = { selectedStep = 4 },
                    label = { Text("৪. রিপোর্ট") },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedStep == 5,
                    onClick = { selectedStep = 5 },
                    label = { Text("৫. প্রোফাইল") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(OffWhite)
        ) {
            when (selectedStep) {
                1 -> BillingView(viewModel)
                2 -> StockView(viewModel)
                3 -> CustomersView(viewModel)
                4 -> ReportsView(viewModel)
                5 -> ProfileSettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun LoginScreen(onSuccess: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalBlue)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PureWhite, modifier = Modifier.size(90.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("SriVik POS", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PureWhite)
        Text("Srimanta & Souvik", fontSize = 14.sp, color = PureWhite.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                pin = it
                error = false
            },
            label = { Text("অপারেটর পিন দিন (ডিফল্ট: 1234)", color = PureWhite) },
            visualTransformation = PasswordVisualTransformation(),
            isError = error,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PureWhite,
                unfocusedBorderColor = PureWhite.copy(alpha = 0.6f),
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            )
        )

        if (error) {
            Text("ভুল পিন কোড! আবার চেষ্টা করুন।", color = CrimsonRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (pin == "1234") onSuccess() else error = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
            modifier = Modifier.fillMaxWidth(0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("লগইন করুন", color = PureWhite, fontSize = 16.sp)
        }
    }
}
