package com.example.recommendationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationapp.data.ProductListSecond

class RecommendationAdapterSecond(private val recommendationList: List<ProductListSecond?>) : RecyclerView.Adapter<RecommendationAdapterSecond.RecommendationViewHolder>() {
    inner class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.prodNameTextViewSec)
        val productIdTextView: TextView = itemView.findViewById(R.id.prodIdTextViewSec)
        val productCatTextView: TextView = itemView.findViewById(R.id.prodCatTextViewSec)
        val productRatTextView: TextView = itemView.findViewById(R.id.prodRatTextViewSec)
        val productPopTextView: TextView = itemView.findViewById(R.id.prodPopTextViewSec)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommendationsecond, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
//        val currentItem = recommendationList[position]
//        // Split the current item to extract product name and ID
//        val (productName, productId) = currentItem.split(" ")
        val product = recommendationList[position] // Retrieve product name and ID from Pair


        holder.productNameTextView.text = product.productName
        holder.productIdTextView.text = product.productId
        holder.productCatTextView.text = product.productCat
        holder.productRatTextView.text = product.productRating
        holder.productPopTextView.text = product.productPopular
    }

    override fun getItemCount() = recommendationList.size



}