package com.example.recommendationapp.data

import org.tensorflow.lite.schema.Int32Vector

data class ProductList(val productId: String, val  productName : String)


object ProductMappingRepo {
    private val productMappings = listOf(
        ProductList("51051000101000100051", "Merchants"),
        ProductList("51051000101000100072","Voucher"),
        ProductList("51051000101000100071","Game Voucher"),
        ProductList("51051000101000100073","Game Topup"),
        ProductList("51051000101000100002","Electricity Token"),
        ProductList("51051000101000100001","E-Giftcard"),
        ProductList("51051000101000100048","Mobile credit")

    )

    fun getProdNames(): List<String> {
        return productMappings.map { it.productName }
    }

    fun getProdId(productName: String): String? {
        return productMappings.find { it.productName == productName }?.productId
    }

    fun getProdName(productId: Float): String {
        val prodIdString = productId.toString()
        return productMappings.find { it.productId == prodIdString }?.productName ?: "Unknown Product"
    }
}