package com.example.recommendationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecommendationAdapter(private val recommendationList: List<String>) : RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {
    inner class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.prodNameTextView)
        val productIdTextView: TextView = itemView.findViewById(R.id.prodIdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommendation, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val currentItem = recommendationList[position]
        // Split the current item to extract product name and ID
        val (productName, productId) = currentItem.split(" ")

        holder.productNameTextView.text = productName
        holder.productIdTextView.text = productId
    }

    override fun getItemCount() = recommendationList.size



}