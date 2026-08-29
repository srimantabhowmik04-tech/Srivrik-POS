package com.atoz.pos.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object PosUtils {
    fun generateUpiString(upiId: String, payeeName: String, amount: Double, trRef: String): String {
        return "upi://pay?pa=$upiId&pn=${Uri.encode(payeeName)}&am=$amount&cu=INR&tr=$trRef"
    }

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bits[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        return bitmap
    }

    fun sendReceiptWhatsApp(context: Context, phone: String, message: String) {
        val cleanPhone = phone.replace("+", "").replace(" ", "")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        runCatching { context.startActivity(intent) }
    }

    fun exportDatabase(context: Context, destUri: Uri): Boolean {
        return runCatching {
            val dbFile = context.getDatabasePath("srivik_pos_database")
            context.contentResolver.openOutputStream(destUri)?.use { output ->
                FileInputStream(dbFile).use { input -> input.copyTo(output) }
            }
            true
        }.getOrDefault(false)
    }

    fun importDatabase(context: Context, srcUri: Uri): Boolean {
        return runCatching {
            val dbFile = context.getDatabasePath("srivik_pos_database")
            context.contentResolver.openInputStream(srcUri)?.use { input ->
                FileOutputStream(dbFile).use { output -> input.copyTo(output) }
            }
            true
        }.getOrDefault(false)
    }
}
