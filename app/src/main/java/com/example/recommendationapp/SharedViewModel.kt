package com.example.recommendationapp

import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    var mostFrequentProductId: String = ""
    var favProductId: String = ""
    var favProdId: String = ""
    var inferenceRes: String = ""
}