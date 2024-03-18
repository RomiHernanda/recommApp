package com.example.recommendationapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationapp.R
import com.example.recommendationapp.RecommendationAdapterSecond
import com.example.recommendationapp.data.ProductListSecond
import com.example.recommendationapp.data.ProductRepoSecond

class RecommendationFragmentSecond : Fragment() {

    //private lateinit var recomList : ListView
    private lateinit var recomAdapter : RecommendationAdapterSecond
    private lateinit var backButton : Button

    private val productMappings = ProductRepoSecond
    //private lateinit var recomViewModel : ProductsViewModel
    //private lateinit var tfliteInter : Interpreter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        tfliteInter = Interpreter(loadModelFileFromAssets("danarecmodel.tflite"))
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommendationadvanced, container, false)

        val args: RecommendationFragmentSecondArgs by navArgs()
        val inferenceResult = args.inferenceResultSecond.toString()
        val recommendations = inferenceResult.map { productId ->
            val productIdStr = productId.toString()
            val productName = productMappings.getProdName(productIdStr)
            val productCat = productMappings.getProdCat(productIdStr)
            val productRat = productMappings.getProdRat(productIdStr)
            val productPop = productMappings.getProdPop(productIdStr)

            if (productName != null && productCat != null && productRat != null && productPop != null) {
                ProductListSecond(productIdStr, productName, productCat, productRat, productPop)
            } else {
                null
            }
        }

        //Manual
//        val mostFrequentProductId = args.productId
//        val mostFrequentProductName = args.productName
//        recomAdapter = RecommendationAdapter(listOf("$mostFrequentProductName $mostFrequentProductId"))
//        recommendationRecyclerView.adapter = recomAdapter
//        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        //recomViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)
        val recommendationRecyclerView = view.findViewById<RecyclerView>(R.id.recomList_recyclerviewAdv)
        recomAdapter = RecommendationAdapterSecond(recommendations)

        recommendationRecyclerView.adapter = recomAdapter
        recommendationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //recomList = view.findViewById(R.id.recomId_listview)
        backButton = view.findViewById(R.id.backBtnAdv)
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