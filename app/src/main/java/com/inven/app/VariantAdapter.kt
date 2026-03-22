package com.inven.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VariantAdapter(private val variants: List<Variant>) :
    RecyclerView.Adapter<VariantAdapter.VariantViewHolder>() {

    class VariantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val variantName: TextView = itemView.findViewById(R.id.variantName)
        val variantDetails: TextView = itemView.findViewById(R.id.variantDetails)
        val variantStock: TextView = itemView.findViewById(R.id.variantStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_variant, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        val variant = variants[position]
        holder.variantName.text = "${variant.brand.name} - ${variant.size_label}"
        holder.variantDetails.text = "${variant.brand.product.name} > ${variant.brand.product.category.name} | ₹${variant.selling_price}"
        holder.variantStock.text = "Stock: ${variant.stock_count}"
        if (variant.stock_count < variant.low_stock_threshold) {
            holder.variantStock.setTextColor(android.graphics.Color.RED)
        } else {
            holder.variantStock.setTextColor(android.graphics.Color.BLACK)
        }
    }

    override fun getItemCount() = variants.size
}