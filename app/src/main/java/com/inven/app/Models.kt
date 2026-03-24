package com.inven.app

data class Category(
    val id: Int,
    val name: String
)

data class Product(
    val id: Int,
    val name: String,
    val category: Category
)

data class Brand(
    val id: Int,
    val name: String,
    val product: Product
)

data class Variant(
    val id: Int,
    val size_label: String,
    val selling_price: Double,
    val purchase_price: Double,
    val stock_count: Int,
    val low_stock_threshold: Int,
    val qr_code: String?,
    val brand: Brand
)

data class OrderItemCreate(
    val variant_id: Int,
    val quantity: Int
)

data class OrderCreate(
    val items: List<OrderItemCreate>
)

data class PendingBillItemCreate(
    val variant_id: Int,
    val quantity: Int
)

data class PendingBillCreate(
    val items: List<PendingBillItemCreate>
)

data class OrderItem(
    val id: Int,
    val variant_id: Int,
    val quantity: Int,
    val variant: Variant
)

data class Order(
    val id: Int,
    val status: String,
    val created_at: String,
    val items: List<OrderItem>
)

data class PendingBillItem(
    val id: Int,
    val variant_id: Int,
    val quantity: Int,
    val variant: Variant
)

data class PendingBill(
    val id: Int,
    val status: String,
    val created_at: String,
    val items: List<PendingBillItem>
)