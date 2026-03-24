package com.inven.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var scannedResult: TextView
    private lateinit var addToBillButton: Button
    private lateinit var confirmBillButton: Button

    private val billItems = mutableListOf<PendingBillItemCreate>()
    private var currentVariant: Variant? = null
    private val CAMERA_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        scannedResult = findViewById(R.id.scannedResult)
        addToBillButton = findViewById(R.id.addToBillButton)
        confirmBillButton = findViewById(R.id.confirmBillButton)

        barcodeView = DecoratedBarcodeView(this)
        findViewById<android.widget.FrameLayout>(R.id.scannerContainer).addView(barcodeView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startScanner()
        }

        addToBillButton.setOnClickListener {
            currentVariant?.let { variant ->
                billItems.add(PendingBillItemCreate(variant.id, 1))
                Toast.makeText(this, "${variant.brand.name} ${variant.size_label} added", Toast.LENGTH_SHORT).show()
                scannedResult.text = "Scanning..."
                addToBillButton.visibility = View.GONE
                confirmBillButton.visibility = if (billItems.isNotEmpty()) View.VISIBLE else View.GONE
                barcodeView.resume()
            }
        }

        confirmBillButton.setOnClickListener {
            createPendingBill()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner()
            } else {
                Toast.makeText(this, "Camera permission required for scanning", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun startScanner() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                barcodeView.pause()
                lookupVariant(result.text)
            }
        })
    }

    private fun lookupVariant(qrCode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getVariantByQr(qrCode)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        currentVariant = response.body()
                        currentVariant?.let { variant ->
                            scannedResult.text = "${variant.brand.name} - ${variant.size_label}\n₹${variant.selling_price} | Stock: ${variant.stock_count}"
                            addToBillButton.visibility = View.VISIBLE
                        }
                    } else {
                        scannedResult.text = "Product not found"
                        barcodeView.resume()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    scannedResult.text = "Error: ${e.message}"
                    barcodeView.resume()
                }
            }
        }
    }

    private fun createPendingBill() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bill = PendingBillCreate(billItems)
                val response = RetrofitClient.apiService.createPendingBill(bill)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ScanActivity, "Bill sent for approval", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ScanActivity, "Failed to create bill", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ScanActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}