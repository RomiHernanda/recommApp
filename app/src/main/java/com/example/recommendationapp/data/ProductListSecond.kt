package com.example.recommendationapp.data

data class ProductListSecond(val productId: String, val  productName : String, val productCat : String, val productRating : String, val productPopular : String ){
    constructor(productId: String, productCat: String, productRating: String, productPopular: String) : this(productId, "",productCat,productRating,productPopular)
}

//object ProductRepoSecond {
//    private val productMappingsListSecond = listOf(
//        ProductListSecond("51051000101000100051","Merchants", "Cat1", "3.8", "100"),
//        ProductListSecond("51051000101000100072","Voucher","Cat2", "4.2", "80"),
//        ProductListSecond("51051000101000100071","Game Voucher","Cat3","3.5", "120"),
//        ProductListSecond("51051000101000100073","Game Topup","Cat3","2.4","60"),
//        ProductListSecond("51051000101000100002","Electricity Token","Cat2","4.8","90"),
//        ProductListSecond("51051000100000000001","E-Giftcard","Cat1","4.5","110"),
//        ProductListSecond("51051000101000100048","Mobile credit","Cat1","2.2","70")
//    )
object ProductRepoSecond {
    private val productMappingsListSecond = listOf(
        ProductListSecond("51","Merchants", "111", "3.8", "100"),
        ProductListSecond("72","Voucher","222", "4.2", "80"),
        ProductListSecond("71","Game Voucher","333","3.5", "120"),
        ProductListSecond("73","Game Topup","333","2.4","60"),
        ProductListSecond("22","Electricity Token","222","4.8","90"),
        ProductListSecond("11","E-Giftcard","111","4.5","110"),
        ProductListSecond("48","Mobile credit","111","2.2","70")
    )


    fun getProdNames(): List<String> {
        return ProductRepoSecond.productMappingsListSecond.map { it.productName }
    }

    fun getProdId(productName: String): String? {
        return ProductRepoSecond.productMappingsListSecond.find { it.productName == productName }?.productId
    }

    fun getProdCat(productId: String): String? {
        return ProductRepoSecond.productMappingsListSecond.find { it.productId == productId }?.productCat
    }

    fun getProdRat(productId: String): String? {
        return ProductRepoSecond.productMappingsListSecond.find { it.productId == productId }?.productRating
    }

    fun getProdPop(productId: String): String? {
        return ProductRepoSecond.productMappingsListSecond.find { it.productId == productId }?.productPopular
    }

    fun getProdName(productId: String): String {
        //val prodIdString = productId.toString()
        return ProductRepoSecond.productMappingsListSecond.find { it.productId == productId }?.productName ?: "Unknown Product"
    }


}




