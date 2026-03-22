package com.inven.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.variantsRecyclerView)
        scanButton = findViewById(R.id.scanButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadVariants()

        val createOrderButton = findViewById<Button>(R.id.createOrderButton)
        createOrderButton.setOnClickListener {
            startActivity(android.content.Intent(this, CreateOrderActivity::class.java))
        }

        val pendingBillsButton = findViewById<Button>(R.id.pendingBillsButton)
        pendingBillsButton.setOnClickListener {
            startActivity(android.content.Intent(this, PendingBillsActivity::class.java))
        }

        val ordersButton = findViewById<Button>(R.id.ordersButton)
        ordersButton.setOnClickListener {
            startActivity(android.content.Intent(this, OrdersActivity::class.java))
        }

        scanButton.setOnClickListener {
            startActivity(android.content.Intent(this, ScanActivity::class.java))
        }
    }

    private fun loadVariants() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                android.util.Log.d("InvenApp", "Calling API...")
                val response = RetrofitClient.apiService.getVariants()
                android.util.Log.d("InvenApp", "Response code: ${response.code()}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val variants = response.body() ?: emptyList()
                        recyclerView.adapter = VariantAdapter(variants)
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to load variants", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.util.Log.e("InvenApp", "Error: ${e.message}", e)
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}