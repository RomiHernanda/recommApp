package com.example.recommendationapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationapp.R
import com.example.recommendationapp.RecommendationAdapterSecond
import com.example.recommendationapp.SharedViewModel
import com.example.recommendationapp.data.ProductListSecond
import com.example.recommendationapp.data.ProductRepoSecond

class RecommendationFragmentSecond : Fragment() {

    //private lateinit var recomList : ListView
    private lateinit var recomAdapter : RecommendationAdapterSecond
    private lateinit var backButton : Button

    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }


    private val productMappings = ProductRepoSecond

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommendationadvanced, container, false)

        val args: RecommendationFragmentSecondArgs by navArgs()
        val inferenceResult = args.inferenceResultSecond
        val favoProductId = sharedViewModel.favProductId
        Log.d("Inference Result","inferenceResult: $inferenceResult")
        val inferenceRes = sharedViewModel.inferenceRes

        //val productIdStr = probabilityToProductIdMap[inferenceResult]?.toInt()?.toString()
        Log.d("Product ID string","favoProductId: $favoProductId")
        val prodId = probabilityToProductIdMapOff
        val productIds = rawToProdIdInferenceRes.entries.firstOrNull { (key, _) ->
            key.first == sharedViewModel.favProductId && key.second == inferenceResult
        }

        val productId = productIds?.value // Retrieve the corresponding product ID
            ?: // Handle case where product ID is not found
            "Unknown"
//        val productIdStr = inferenceResult.toInt().toString() // Convert float to string

        //PLANNED ADJUSTMENT 2ND
//        val mostFrequentProductId = args.mostFrequentProductId
//
//        val productIdStr = if (inferenceResult == 0.03491015F) {
//            probabilityToProductIdMap.entries
//                .firstOrNull { (first, second) -> first?.first == mostFrequentProductId && second == inferenceResult }
//                ?.value ?: "Unknown"
//        } else {
//            probabilityToProductIdMap.entries
//                .firstOrNull { (first, second) -> first == null && second == inferenceResult }
//                ?.value ?: "Unknown"
//        }

        val productName = productMappings.getProdName(productId)
        val productCat = productMappings.getProdCat(productId)
        val productRat = productMappings.getProdRat(productId)
        val productPop = productMappings.getProdPop(productId)

        val recommendedProduct = if (productName != null && productCat != null && productRat != null && productPop != null) {
             ProductListSecond(productId, productName, productCat, productRat, productPop)
            // Now you can use recommendedProduct in your UI
        } else {
            // Handle case where product details couldn't be found
            ProductListSecond("0", "Unknown", "Unknown", "Unknown", "Unknown")
        }
//        val recommendations = inferenceResult.map { productId ->
//            val productIdStr = productId.toString()
//            val productName = productMappings.getProdName(productIdStr)
//            val productCat = productMappings.getProdCat(productIdStr)
//            val productRat = productMappings.getProdRat(productIdStr)
//            val productPop = productMappings.getProdPop(productIdStr)
//
//            if (productName != null && productCat != null && productRat != null && productPop != null) {
//                ProductListSecond(productIdStr, productName, productCat, productRat, productPop)
//            } else {
//                null
//            }
//        }

        //Manual
//        val mostFrequentProductId = args.productId
//        val mostFrequentProductName = args.productName
//        recomAdapter = RecommendationAdapter(listOf("$mostFrequentProductName $mostFrequentProductId"))
//        recommendationRecyclerView.adapter = recomAdapter
//        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        //recomViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)
        val recommendationRecyclerView = view.findViewById<RecyclerView>(R.id.recomList_recyclerviewAdv)
        recomAdapter = RecommendationAdapterSecond(recommendedProduct)

        recommendationRecyclerView.adapter = recomAdapter
        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //recomList = view.findViewById(R.id.recomId_listview)
        backButton = view.findViewById(R.id.backBtnAdv)
        backButton.setOnClickListener {
            findNavController().navigateUp()

        }


        return view
    }



    private val probabilityToProductIdMapOff: Map<Float, String> = mapOf(
        0.03491014F to "11",
        3.08219E22F to "22",
        0.03491085F to "73",
        0.03511409F to "71",
        0.03491313F to "72",
        0.06781015F to "48",
        2.1923238E7F to "51"
    )

    private val rawToProdIdInferenceRes: Map<Pair<String?, Float>, String> = mapOf (
        Pair("51", 0.24947235F) to "11",
        Pair("72", 5.5004696E22F) to "22",
        Pair("71", 0.24947235F) to "73",
        Pair("73", 0.24947235F) to "71",
        Pair("22", 0.24947235F) to "72",
        Pair("11", 0.24947235F) to "48",
        Pair("48", 5.5389056E7F) to "51"
    )

    private val rawToProductIdMapInferenceResult: Map<Pair<String?, Float>, String> = mapOf (
        Pair("51", 0.03491014F) to "22",
        Pair("72", -3.08219E22F) to "11",
        Pair("71", 0.03491015F) to "71",
        Pair("73", 0.03491015F) to "72",
        Pair("22", 0.03491015F) to "73",
        Pair("11", 0.03491015F) to "51",
        Pair("48", 2.1923238E7F) to "48"
    )
}