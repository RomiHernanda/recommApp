package com.example.recommendationapp.data

import org.tensorflow.lite.schema.Int32Vector

data class ProductList(val productId: String, val productName: String) {
    constructor(productId: String) : this(productId, "")
}


object ProductMappingRepo {
    private val productMappings = listOf(
        ProductList("51", "Merchants"),
        ProductList("72","Voucher"),
        ProductList("71","Game Voucher"),
        ProductList("73","Game Topup"),
        ProductList("22","Electricity Token"),
        ProductList("11","E-Giftcard"),
        ProductList("48","Mobile credit")

    )

    fun getProdNames(): List<String> {
        return productMappings.map { it.productName }
    }

    fun getProdId(productName: String): String? {
        return productMappings.find { it.productName == productName }?.productId
    }

    fun getProdName(productId: String): String {
        //val prodIdString = productId.toString()
        return productMappings.find { it.productId == productId }?.productName ?: "Unknown Product"
    }

}