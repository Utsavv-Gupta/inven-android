package com.inven.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var variantSpinner: Spinner
    private lateinit var quantityInput: EditText
    private lateinit var addItemButton: Button
    private lateinit var submitOrderButton: Button
    private lateinit var orderItemsRecyclerView: android.widget.ListView

    private var variants = listOf<Variant>()
    private val orderItems = mutableListOf<OrderItemCreate>()
    private val orderItemLabels = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order)

        variantSpinner = findViewById(R.id.variantSpinner)
        quantityInput = findViewById(R.id.quantityInput)
        addItemButton = findViewById(R.id.addItemButton)
        submitOrderButton = findViewById(R.id.submitOrderButton)
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView)

        loadVariants()

        addItemButton.setOnClickListener {
            val selectedIndex = variantSpinner.selectedItemPosition
            val quantity = quantityInput.text.toString().toIntOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Enter a valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val variant = variants[selectedIndex]
            orderItems.add(OrderItemCreate(variant.id, quantity))
            orderItemLabels.add("${variant.brand.name} ${variant.size_label} x$quantity")
            orderItemsRecyclerView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, orderItemLabels)
            quantityInput.text.clear()
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show()
        }

        submitOrderButton.setOnClickListener {
            if (orderItems.isEmpty()) {
                Toast.makeText(this, "Add at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitOrder()
        }
    }

    private fun loadVariants() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getVariants()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        variants = response.body() ?: emptyList()
                        val labels = variants.map { "${it.brand.name} - ${it.size_label}" }
                        variantSpinner.adapter = ArrayAdapter(this@CreateOrderActivity, android.R.layout.simple_spinner_dropdown_item, labels)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateOrderActivity, "Error loading variants", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitOrder() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val order = OrderCreate(orderItems)
                val response = RetrofitClient.apiService.createOrder(order)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreateOrderActivity, "Order created successfully", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateOrderActivity, "Failed to create order", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateOrderActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}