package com.inven.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        recyclerView = findViewById(R.id.ordersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadOrders()
    }

    private fun loadOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getOrders()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val orders = response.body() ?: emptyList()
                        val pendingOrders = orders.filter { it.status == "pending" }
                        recyclerView.adapter = OrdersAdapter(pendingOrders) { orderId ->
                            activateOrder(orderId)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrdersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun activateOrder(orderId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.activateOrder(orderId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OrdersActivity, "Order activated! Stock updated.", Toast.LENGTH_LONG).show()
                        loadOrders()
                    } else {
                        Toast.makeText(this@OrdersActivity, "Failed to activate", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrdersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class OrdersAdapter(
    private val orders: List<Order>,
    private val onActivate: (Int) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val activateButton: Button = itemView.findViewById(R.id.activateButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order #${order.id}"
        holder.orderStatus.text = "Items: ${order.items.size} | Status: ${order.status}"
        holder.activateButton.setOnClickListener {
            onActivate(order.id)
        }
    }

    override fun getItemCount() = orders.size
}