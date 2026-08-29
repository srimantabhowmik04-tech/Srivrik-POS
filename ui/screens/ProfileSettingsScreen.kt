package com.atoz.pos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atoz.pos.data.model.StoreProfile
import com.atoz.pos.ui.PosViewModel
import com.atoz.pos.ui.theme.*

@Composable
fun ProfileSettingsScreen(viewModel: PosViewModel) {
    val currentProfile by viewModel.storeProfile.collectAsState()

    var storeName by remember(currentProfile) { mutableStateOf(currentProfile?.storeName ?: "SriVik Store") }
    var ownerName by remember(currentProfile) { mutableStateOf(currentProfile?.ownerName ?: "Srimanta & Souvik") }
    var phone by remember(currentProfile) { mutableStateOf(currentProfile?.phone ?: "") }
    var address by remember(currentProfile) { mutableStateOf(currentProfile?.address ?: "") }
    var upiId by remember(currentProfile) { mutableStateOf(currentProfile?.upiId ?: "") }
    var terminalId by remember(currentProfile) { mutableStateOf(currentProfile?.terminalId ?: "Mobile-1") }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri: Uri? ->
        uri?.let { viewModel.exportBackup(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { viewModel.importBackup(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("দোকানের প্রোফাইল সেটিংস", fontWeight = FontWeight.Bold, color = RoyalBlue, fontSize = 16.sp)

                OutlinedTextField(value = storeName, onValueChange = { storeName = it }, label = { Text("দোকানের নাম") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("মালিকের নাম") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("মোবাইল নম্বর") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("ঠিকানা") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = upiId, onValueChange = { upiId = it }, label = { Text("UPI ID (যেমন: store@upi)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = terminalId, onValueChange = { terminalId = it }, label = { Text("টার্মিনাল আইডি (যেমন: Mobile-1)") }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        viewModel.updateStoreProfile(
                            StoreProfile(
                                id = 1,
                                storeName = storeName,
                                ownerName = ownerName,
                                phone = phone,
                                address = address,
                                upiId = upiId,
                                upiPayeeName = storeName,
                                terminalId = terminalId
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("প্রোফাইল সেভ করুন")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ডাটা ব্যাকআপ ও ট্রান্সফার (মেমোরি / পেনড্রাইভ)", fontWeight = FontWeight.Bold, color = RoyalBlue, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { exportLauncher.launch("srivik_pos_backup.db") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                    ) {
                        Text("ব্যাকআপ নিন")
                    }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ডাটা রিস্টোর")
                    }
                }
            }
        }
    }
}
