package com.atoz.pos.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atoz.pos.data.local.AppDatabase
import com.atoz.pos.data.model.*
import com.atoz.pos.util.PosUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(val product: Product, var quantity: Int)

class PosViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).posDao()

    val products = dao.getAllProducts().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val invoices = dao.getAllInvoices().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val customers = dao.getAllCustomers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val storeProfile = dao.getStoreProfile().stateIn(viewModelScope, SharingStarted.Lazily, StoreProfile())

    val cart = MutableStateFlow<List<CartItem>>(emptyList())

    fun addToCart(product: Product) {
        val current = cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        cart.value = current
    }

    fun removeFromCart(product: Product) {
        val current = cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            if (current[index].quantity > 1) {
                current[index] = current[index].copy(quantity = current[index].quantity - 1)
            } else {
                current.removeAt(index)
            }
        }
        cart.value = current
    }

    fun clearCart() {
        cart.value = emptyList()
    }

    fun saveProduct(name: String, barcode: String, purchase: Double, mrp: Double, sell: Double, stock: Int) {
        viewModelScope.launch {
            dao.insertProduct(Product(0, name, barcode, purchase, mrp, sell, stock))
        }
    }

    fun addStock(product: Product, extra: Int) {
        viewModelScope.launch {
            dao.updateProduct(product.copy(stockQuantity = product.stockQuantity + extra))
        }
    }

    fun saveCustomer(name: String, phone: String, due: Double = 0.0) {
        viewModelScope.launch {
            dao.insertCustomer(Customer(0, name, phone, due))
        }
    }

    fun updateStoreProfile(profile: StoreProfile) {
        viewModelScope.launch {
            dao.saveProfile(profile)
        }
    }

    fun checkout(phone: String, paymentMode: String, isPaid: Boolean, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val total = cart.value.sumOf { it.product.sellingPrice * it.quantity }
            val terminal = storeProfile.value?.terminalId ?: "Mobile-1"
            val invoiceId = dao.insertInvoice(Invoice(0, phone, total, paymentMode, isPaid, terminal))

            cart.value.forEach { item ->
                dao.updateProduct(
                    item.product.copy(
                        stockQuantity = item.product.stockQuantity - item.quantity,
                        lastSoldTimestamp = System.currentTimeMillis()
                    )
                )
            }

            if (paymentMode == "DUE" && phone.isNotBlank()) {
                val existingCustomer = customers.value.find { it.phone == phone }
                if (existingCustomer != null) {
                    dao.updateCustomer(existingCustomer.copy(totalDue = existingCustomer.totalDue + total))
                } else {
                    dao.insertCustomer(Customer(0, "Customer $phone", phone, total))
                }
            }

            clearCart()
            onComplete(invoiceId)
        }
    }

    fun exportBackup(uri: Uri): Boolean = PosUtils.exportDatabase(getApplication(), uri)
    fun importBackup(uri: Uri): Boolean = PosUtils.importDatabase(getApplication(), uri)
}
