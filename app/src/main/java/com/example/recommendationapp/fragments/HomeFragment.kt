package com.example.recommendationapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recommendationapp.R
import com.example.recommendationapp.data.ProductList
import com.example.recommendationapp.data.ProductMappingRepo
import com.example.recommendationapp.data.ProductsViewModel
import com.example.recommendationapp.ml.Danarecmodel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer

class HomeFragment : Fragment() {

    private lateinit var spinner : Spinner
    private lateinit var listView : ListView
    private lateinit var addButton : Button
    private lateinit var recomButton: Button
    private lateinit var resetButton: Button

    private lateinit var chosenItemList : MutableList<ProductList>
    private lateinit var chosenItemAdapter: ArrayAdapter<ProductList>
    //private lateinit var homeViewModel : ProductsViewModel
    private lateinit var productList : ProductMappingRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        spinner = view.findViewById(R.id.prodId_spinner)
        listView = view.findViewById(R.id.prodId_listview)
        addButton = view.findViewById(R.id.addBtn)
        recomButton = view.findViewById(R.id.recomBtnTemp)
        resetButton = view.findViewById(R.id.resetBtn)

        //homeViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)

        chosenItemList = mutableListOf()

        productList = ProductMappingRepo

        val productNames = productList.getProdNames()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        chosenItemAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            chosenItemList)

        listView.adapter = chosenItemAdapter

        addButton.setOnClickListener {
            val selectedItemName = spinner.selectedItem.toString()
            val productId = productList.getProdId(selectedItemName)
            if (productId != null) {
                val productList = ProductList(productId, selectedItemName)
                chosenItemList.add(productList)
                chosenItemAdapter.notifyDataSetChanged()
            }
            //homeViewModel.chosenItemList.value = homeViewModel.chosenItemList.value.orEmpty() + selectedItem
            //updateListView()
        }

        resetButton.setOnClickListener {
            chosenItemList.clear()
            chosenItemAdapter.notifyDataSetChanged()
        }

        recomButton.setOnClickListener {
            if (chosenItemList.size >= 1) {
                val inputArray = prepareInputData(chosenItemList)
                val inferenceResult = runInference(inputArray)

                val action = HomeFragmentDirections.actionHomeFragmentToRecommendationFragment(inferenceResult)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select at least one item", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun prepareInputData(chosenItemsList: List<ProductList>): FloatArray {
        // Convert chosenItemsList to the required input format for the TensorFlow Lite model
        val inputArray = FloatArray(chosenItemsList.size)

        for ((index, item) in chosenItemsList.withIndex()) {
            // Convert each item to the appropriate format expected by the model
            // Here, you might perform any necessary preprocessing, normalization, or encoding
            // For simplicity, let's assume your input is already in a suitable format
            inputArray[index] = item.productId.toFloat()
        }

        return inputArray
    }

//    private fun runInference(inputArray: FloatArray): FloatArray {
//        try {
//            val assetManager = requireContext().assets
//            val modelFileName = "ml/danarecmodel.tflite"
//            val inputStream = assetManager?.open(modelFileName)
//            val modelBuffer = inputStream?.readBytes()
//
//            // Load the TensorFlow Lite model
//            val interpreter = Interpreter(ByteBuffer.wrap(modelBuffer))
//
//            // Allocate input and output buffers
//            val inputBuffer = inputArray.toTypedArray().toFloatArray()
//            val outputBuffer = FloatArray(10)
//
//            // Run inference
//            interpreter.run(inputBuffer, outputBuffer)
//
//            // Clean up resources
//            interpreter.close()
//
//            return outputBuffer
//
//        } catch (e: IOException){
//            e.printStackTrace()
//            return floatArrayOf()
//
//        }
//
//    }

    private fun runInference(inputArray: FloatArray) : FloatArray {
        val model = Danarecmodel.newInstance(requireContext())


        // Prepare input buffer
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, inputArray.size), DataType.FLOAT32)
        inputFeature0.loadArray(inputArray)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Convert output TensorBuffer to FloatArray
        val outputArray = outputFeature0.floatArray

        // Release model resources
        model.close()

        return outputArray
    }

//    private fun updateListView() {
//        chosenItemList.clear()
//        //chosenItemList.add(homeViewModel.chosenItemList.value.orEmpty())
//        chosenItemAdapter.notifyDataSetChanged()
//    }

//    private fun updateListView() {
//        val adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_list_item_1,
//            chosenItemList
//        )
//        listView.adapter = adapter
//    }

//    private fun prepInputData(chosenItemsList: List<String>): FloatArray {
//        val arrayToIdMap = mapOf(
//            "51" to 51051000101000100051f,
//            "72" to 51051000101000100072f,
//            "71" to 51051000101000100071f,
//            "02" to 51051000101000100002f,
//            "73" to 51051000101000100073f,
//            "01" to 51051000101000100001f,
//            "48" to 51051000101000100048f
//        )
//        //val numInputArray = chosenItemsList.map { arrayToIdMap[it] ?: "" }.toFloatArray()
//        val floatList = chosenItemsList.map { arrayToIdMap[it] ?: -1.0f }
//        val numInputArray = FloatArray(floatList.size) {floatList[it] }
//        return numInputArray
//    }

}