package com.example.recommendationapp.data

import org.tensorflow.lite.schema.Int32Vector

data class ProductList(var productName: String, var productId : Int32Vector)
