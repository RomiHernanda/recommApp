package com.example.recommendationapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationapp.R
import com.example.recommendationapp.RecommendationAdapter
import com.example.recommendationapp.SharedViewModel
import com.example.recommendationapp.data.ProductMappingRepo

class RecommendationFragment : Fragment() {

    //private lateinit var recomList : ListView
    private lateinit var recomAdapter : RecommendationAdapter
    private lateinit var backButton : Button

    private val productMappings = ProductMappingRepo

    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    //private lateinit var recomViewModel : ProductsViewModel
    //private lateinit var tfliteInter : Interpreter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        tfliteInter = Interpreter(loadModelFileFromAssets("danarecmodel.tflite"))
//    }

        private val probabilityToProductIdMap: Map<Pair<String?, Float>, String> = mapOf (
        Pair("51", 0.044060796F) to "51",
        Pair("72", 0.044060796F) to "72",
        Pair("71", 0.044060796F) to "71",
        Pair("73", 0.044060796F) to "73",
        Pair("22", 0.044060796F) to "22",
        Pair("11", 0.044060796F) to "11",
        Pair("48", 0.044060796F) to "48"
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommendation, container, false)

        val args: RecommendationFragmentArgs by navArgs()
        val inferenceResult = args.inferenceResult.toString()
        val mostFrequentProdId = sharedViewModel.mostFrequentProductId
        Log.d("InferenceResult", "Inference Result: $inferenceResult")
        val recommendation: Pair<String, String>? = probabilityToProductIdMap[Pair(mostFrequentProdId, inferenceResult.toFloat())]?.let { productId ->
            val productName = productMappings.getProdName(productId) ?: "Unknown Product"
            Pair(productName, productId)
        }
        val recommendations = inferenceResult.map { productId ->
            val productIdStr = productId.toString()
            val productName = productMappings.getProdName(productIdStr) ?: "Unknown Product"
            Pair(productName, productIdStr)
        }

        val retrievedProductId = probabilityToProductIdMap[Pair(mostFrequentProdId, inferenceResult.toFloat())]


//        // If a valid product ID is retrieved, display it as the recommendation
//        retrievedProductId?.let { productId ->
//            val productName = productMappings.getProdName(productId) ?: "Unknown Product"
//            val recommendation = Pair(productName, productId)
//            //displayRecommendation(recommendation)
//        }

        //Manual
//        val mostFrequentProductId = args.productId
//        val mostFrequentProductName = args.productName
//        recomAdapter = RecommendationAdapter(listOf("$mostFrequentProductName $mostFrequentProductId"))
//        recommendationRecyclerView.adapter = recomAdapter
//        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        if (recommendation != null) {
            val recommendationRecyclerView = view.findViewById<RecyclerView>(R.id.recomList_recyclerview)
            recomAdapter = RecommendationAdapter(recommendation!!)

            recommendationRecyclerView.adapter = recomAdapter
            recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        else {
            Toast.makeText(requireContext(), "Error: Recommendation not found", Toast.LENGTH_SHORT).show()

        }

        //recomViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)


        //recomList = view.findViewById(R.id.recomId_listview)
        backButton = view.findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            findNavController().navigateUp()
//            val chosenItemList : List<String> = recomViewModel.chosenItemList.value?.split(",") ?: emptyList()
//            val recommendations = prepareAndRunInference(chosenItemList)
//            displayRecommendations(recommendations)

        }


        return view
    }



//    private fun loadModelFileFromAssets(fileName: String): ByteBuffer {
//        val fileDescriptor = requireContext().assets.openFd(fileName)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//    private fun prepareAndRunInference(chosenItemsList: List<String>): List<String> {
//        val inputData = prepareInputData(chosenItemsList)
//
//        val recommendations = runInference(inputData)
//
//        return recommendations
//    }
//
//    private fun prepareInputData(chosenItemsList: List<String>): List<String> {
//        val fullItemNumbers = mutableListOf<String>()
//        chosenItemsList.forEach { item ->
//            fullItemNumbers.add("510510001010001000$item")
//        }
//        return fullItemNumbers
//    }
//
//    private fun runInference(inputData: List<String>): List<String> {
//        val recommendations = mutableListOf<String>()
//        inputData.forEach { item ->
//            recommendations.add("Recommendation for $item")
//        }
//        return recommendations.take(2)
//    }
//
//    private fun displayRecommendations(recommendations: List<String>) {
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recommendations)
//        recomList.adapter = adapter
//    }
}