package com.example.recommendationapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.recommendationapp.SharedViewModel
import com.example.recommendationapp.data.ProductList
import com.example.recommendationapp.data.ProductMappingRepo
import com.example.recommendationapp.ml.Danarecomfrequency
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HomeFragment : Fragment() {

    private lateinit var spinner : Spinner
    private lateinit var listView : ListView
    private lateinit var addButton : Button
    private lateinit var recomButton: Button
    private lateinit var resetButton: Button
    private lateinit var advButton: Button

    private lateinit var chosenItemList : MutableList<ProductList>
    private lateinit var chosenProductIdList: MutableList<ProductList>
    private lateinit var chosenItemAdapter: ArrayAdapter<ProductList>
    private lateinit var productList : ProductMappingRepo
    private lateinit var interpreter : Interpreter

    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

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
        advButton = view.findViewById(R.id.advBtn)


        chosenItemList = mutableListOf()
        chosenProductIdList = mutableListOf()

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
                chosenProductIdList.add(ProductList(productId))
                chosenItemAdapter.notifyDataSetChanged()
            }

        }

        advButton.setOnClickListener {
            if (chosenItemList.isNotEmpty() || chosenProductIdList.isNotEmpty()){
                chosenProductIdList.clear()
                chosenItemList.clear()
                chosenItemAdapter.notifyDataSetChanged()
            }
            val action = HomeFragmentDirections.actionHomeFragmentToHomeFragmentSecond()
            findNavController().navigate(action)
        }

        resetButton.setOnClickListener {
            chosenItemList.clear()
            chosenProductIdList.clear()
            chosenItemAdapter.notifyDataSetChanged()
        }

        recomButton.setOnClickListener {
            if (chosenItemList.isNotEmpty() || chosenProductIdList.isNotEmpty()) {
                val productFrequencies = chosenProductIdList.groupingBy { it.productId }.eachCount()
                // Find the most frequent product ID
                val freqProductId = productFrequencies.maxByOrNull { it.value }?.key

                // Get the frequency of the most frequent product ID
                val freqFrequency = productFrequencies[freqProductId] ?: 0


                if (freqProductId != null) {
                    sharedViewModel.mostFrequentProductId = freqProductId
                }

                val inferenceResult = runInferenceShort(freqProductId!!, freqFrequency.toFloat())

                val action = HomeFragmentDirections.actionHomeFragmentToRecommendationFragment(inferenceResult)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select at least one item", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }


    private fun runInferenceShort(productId: String, frequency: Float): Float {

        // Create the TFLite model instance
        val model = Danarecomfrequency.newInstance(requireContext())

        // Prepare input data for inference
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature0.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(productId.toFloat()).rewind() as ByteBuffer)

        val inputFeature1 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature1.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(frequency).rewind() as ByteBuffer)


        // Runs model inference and gets result
        val outputs = model.process(inputFeature0, inputFeature1)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Extract the recommended product ID from the output feature
        val recommendedProductId = outputFeature0.floatArray[0]

//

        // Release model resources
        model.close()

        return recommendedProductId
    }


//    Log.d("Inference", "Input Product ID: $productId")
//        Log.d("Inference", "Input Frequency: $frequency")
//        Log.d("Inference","inputFeature0: $inputFeature0")
//        Log.d("Inference","inputFeature1: $inputFeature1")
//        Log.d("Inference","OutputFeature0: $outputFeature0")
//        Log.d("Inference","recommendedProductId: $recommendedProductId")






}