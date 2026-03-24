package com.inven.app

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @POST("categories")
    suspend fun createCategory(@Body category: Map<String, String>): Response<Category>

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("brands")
    suspend fun getBrands(): Response<List<Brand>>

    @GET("variants")
    suspend fun getVariants(): Response<List<Variant>>

    @GET("variants/qr/{qr_code}")
    suspend fun getVariantByQr(@Path("qr_code") qrCode: String): Response<Variant>

    @POST("orders")
    suspend fun createOrder(@Body order: OrderCreate): Response<Any>

    @PATCH("orders/{order_id}/activate")
    suspend fun activateOrder(@Path("order_id") orderId: Int): Response<Any>

    @POST("pending-bills")
    suspend fun createPendingBill(@Body bill: PendingBillCreate): Response<Any>

    @POST("pending-bills/{bill_id}/approve")
    suspend fun approvePendingBill(@Path("bill_id") billId: Int): Response<Any>

    @GET("pending-bills")
    suspend fun getPendingBills(): Response<List<PendingBill>>

    @GET("reports/sales")
    suspend fun getSalesReport(
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String
    ): Response<Any>

    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>
}