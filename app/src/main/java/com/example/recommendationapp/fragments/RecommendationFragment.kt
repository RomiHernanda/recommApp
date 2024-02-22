package com.example.recommendationapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.R
import com.example.recommendationapp.data.ProductsViewModel
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class RecommendationFragment : Fragment() {

    private lateinit var recomList : ListView
    private lateinit var recomButton : Button
    private lateinit var recomViewModel : ProductsViewModel
    private lateinit var tfliteInter : Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tfliteInter = Interpreter(loadModelFileFromAssets("danarecmodel.tflite"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommendation, container, false)

        recomViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)

        recomList = view.findViewById(R.id.recomId_listview)
        recomButton = view.findViewById(R.id.recomBtn)
        recomButton.setOnClickListener {
            val chosenItemList : List<String> = recomViewModel.chosenItemList.value?.split(",") ?: emptyList()
            val recommendations = prepareAndRunInference(chosenItemList)
            displayRecommendations(recommendations)

        }


        return view
    }

    private fun loadModelFileFromAssets(fileName: String): ByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    private fun prepareAndRunInference(chosenItemsList: List<String>): List<String> {
        val inputData = prepareInputData(chosenItemsList)

        val recommendations = runInference(inputData)

        return recommendations
    }

    private fun prepareInputData(chosenItemsList: List<String>): List<String> {
        val fullItemNumbers = mutableListOf<String>()
        chosenItemsList.forEach { item ->
            fullItemNumbers.add("510510001010001000$item")
        }
        return fullItemNumbers
    }

    private fun runInference(inputData: List<String>): List<String> {
        val recommendations = mutableListOf<String>()
        inputData.forEach { item ->
            recommendations.add("Recommendation for $item")
        }
        return recommendations.take(2)
    }

    private fun displayRecommendations(recommendations: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recommendations)
        recomList.adapter = adapter
    }
}