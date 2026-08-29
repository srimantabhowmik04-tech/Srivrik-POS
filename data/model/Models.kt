package com.atoz.pos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val barcode: String,
    val purchasePrice: Double,
    val mrp: Double,
    val sellingPrice: Double,
    val stockQuantity: Int,
    val lastSoldTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val totalDue: Double = 0.0
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerPhone: String,
    val totalAmount: Double,
    val paymentMode: String,
    val isPaid: Boolean,
    val terminalId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "store_profile")
data class StoreProfile(
    @PrimaryKey val id: Int = 1,
    val storeName: String = "SriVik Store",
    val ownerName: String = "Srimanta & Souvik",
    val phone: String = "",
    val address: String = "",
    val upiId: String = "",
    val upiPayeeName: String = "SriVik Store",
    val logoUri: String? = null,
    val autoPrintOnPayment: Boolean = true,
    val terminalId: String = "Counter-1"
)
