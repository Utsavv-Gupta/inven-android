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

class PendingBillsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_bills)

        recyclerView = findViewById(R.id.pendingBillsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadPendingBills()
    }

    private fun loadPendingBills() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getPendingBills()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val bills = response.body() ?: emptyList()
                        recyclerView.adapter = PendingBillAdapter(bills) { billId ->
                            approveBill(billId)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PendingBillsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun approveBill(billId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.approvePendingBill(billId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PendingBillsActivity, "Bill approved!", Toast.LENGTH_SHORT).show()
                        loadPendingBills()
                    } else {
                        Toast.makeText(this@PendingBillsActivity, "Failed to approve", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PendingBillsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class PendingBillAdapter(
    private val bills: List<Any>,
    private val onApprove: (Int) -> Unit
) : RecyclerView.Adapter<PendingBillAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billId: TextView = itemView.findViewById(R.id.billId)
        val billItems: TextView = itemView.findViewById(R.id.billItems)
        val approveButton: Button = itemView.findViewById(R.id.approveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.billId.text = "Bill #${position + 1}"
        holder.billItems.text = "Tap approve to process"
        holder.approveButton.setOnClickListener {
            onApprove(position + 1)
        }
    }

    override fun getItemCount() = bills.size
}