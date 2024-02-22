package com.example.recommendationapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductsViewModel : ViewModel() {
    val chosenItemList : MutableLiveData<String> = MutableLiveData()
}