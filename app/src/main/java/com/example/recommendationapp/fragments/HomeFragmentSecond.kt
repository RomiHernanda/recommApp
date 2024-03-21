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
import com.example.recommendationapp.data.ProductListSecond
import com.example.recommendationapp.data.ProductMappingRepo
import com.example.recommendationapp.data.ProductRepoSecond
import com.example.recommendationapp.ml.Danarecfeats
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HomeFragmentSecond : Fragment() {

    private lateinit var spinner : Spinner
    private lateinit var listView : ListView
    private lateinit var addButton : Button
    private lateinit var recomButton: Button
    private lateinit var resetButton: Button
    private lateinit var backButtonNormal : Button

    private lateinit var chosenItemList : MutableList<ProductListSecond>
    private lateinit var chosenProductIdList: MutableList<ProductListSecond>
    private lateinit var chosenItemAdapter: ArrayAdapter<ProductListSecond>
    private lateinit var productList : ProductRepoSecond
    private lateinit var interpreter : Interpreter

    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_homeadvanced, container, false)

        spinner = view.findViewById(R.id.prodId_spinnerAdv)
        listView = view.findViewById(R.id.prodId_listviewAdv)
        addButton = view.findViewById(R.id.addBtnAdv)
        recomButton = view.findViewById(R.id.recomBtnTempAdv)
        resetButton = view.findViewById(R.id.resetBtnAdv)
        backButtonNormal = view.findViewById(R.id.backBtnNorm)

        chosenItemList = mutableListOf()
        chosenProductIdList = mutableListOf()

        productList = ProductRepoSecond

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
            Log.d("HomeFragmentSecond", "Selected item name: $selectedItemName")

            val productId = productList.getProdId(selectedItemName)
            val productCat = productList.getProdCatAdd(selectedItemName)
            val productRat = productList.getProdRatAdd(selectedItemName)
            val productPop = productList.getProdPopAdd(selectedItemName)
            Log.d("HomeFragmentSecond", "Product ID: $productId, Category: $productCat, Rating: $productRat, Popularity: $productPop")


            if (productId != null && productCat != null && productRat != null && productPop != null) {
                val productList = ProductListSecond(productId, selectedItemName, productCat, productRat, productPop)
                chosenItemList.add(productList)
                chosenProductIdList.add(ProductListSecond(productId, productCat, productRat, productPop))
                Log.d("HomeFragmentSecond", "Item added to lists")
                chosenItemAdapter.notifyDataSetChanged()
            } else {
                Log.e("HomeFragmentSecond", "Failed to retrieve product information")
            }

        }

        backButtonNormal.setOnClickListener {
            if (chosenItemList.isNotEmpty() || chosenProductIdList.isNotEmpty()){
                chosenProductIdList.clear()
                chosenItemList.clear()
                chosenItemAdapter.notifyDataSetChanged()
            }
            val action = HomeFragmentSecondDirections.actionHomeFragmentSecondToHomeFragment()
            findNavController().navigate(action)
        }

        resetButton.setOnClickListener {
            chosenItemList.clear()
            chosenProductIdList.clear()
            chosenItemAdapter.notifyDataSetChanged()
        }

        recomButton.setOnClickListener {
            if (chosenItemList.isNotEmpty()) {
                //PLANNED ADJUSTMENT
                val firstProductId = chosenProductIdList.first().productId
                val firstProductCat = chosenProductIdList.first().productCat
                val firstProductRat = chosenProductIdList.first().productRating
                val firstProductPop = chosenProductIdList.first().productPopular
                if (firstProductId != null) {
                    sharedViewModel.favProductId = firstProductId
                }

                val inferenceRes = runInferenceFeats(firstProductId, firstProductCat, firstProductRat, firstProductPop)
                val action = HomeFragmentSecondDirections.actionHomeFragmentSecondToRecommendationFragmentSecond(inferenceRes)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select at least one item", Toast.LENGTH_SHORT).show()
            }

        }


        return view
    }

    private fun runInferenceFeats(productId: String, productCat: String, productRat: String, productPop: String ) : Float {
        val model = Danarecfeats.newInstance(requireContext())

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature0.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(productId.toFloat()).rewind() as ByteBuffer)

        val inputFeature1 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature1.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(productCat.toFloat()).rewind() as ByteBuffer)

        val inputFeature2 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature2.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(productRat.toFloat()).rewind() as ByteBuffer)

        val inputFeature3 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature3.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(productPop.toFloat()).rewind() as ByteBuffer)


        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0, inputFeature1, inputFeature2, inputFeature3)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val recommendedIdFeat = outputFeature0.floatArray[0]

        Log.d("InferenceResult", "outputs: $outputs")
        Log.d("InferenceResult", "outputFeature0: $outputFeature0")
        Log.d("InferenceResult", "recommendedIdFeat: $recommendedIdFeat")

        // Releases model resources if no longer used.
        model.close()

        return recommendedIdFeat
    }


    //Example for using TFLite Interpreter (NOT TFLite Library)
//    private fun runInferenceShortTwo(context: Context, chosenProductList: List<ProductListSecond>): List<String> {
//        // Load the TensorFlow Lite model
//        val assetManager = context.assets
//        val modelInputStream = assetManager.open("danarecmodelshortened.tflite") //GANTI MODEL BARU
//        val modelBuffer = modelInputStream.readBytes()
//
//        val interpreter = Interpreter(ByteBuffer.wrap(modelBuffer))
//
//        // Prepare input data for inference
//        val inputShape = interpreter.getInputTensor(0).shape()
//        val inputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
//        val productIds = chosenProductList.map { it.productId.toFloat() }.toFloatArray()
//        inputBuffer.loadArray(productIds)
//
//        // Allocate space for the output tensor
//        val outputShape = interpreter.getOutputTensor(0).shape()
//        val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
//
//        // Run inference
//        interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())
//
//        // Obtain the recommendations
//        val outputData = outputBuffer.floatArray
//        // Log the raw output data
//        Log.d("OutputData", "Raw output data: ${outputData.contentToString()}")
//        val recommendations = outputData.map { it.toString() }
//
//        // Log the recommendations to the console
//        Log.d("Recommendations", "Model output: $recommendations")
//
//        // Close the interpreter
//        interpreter.close()
//
//        return recommendations
//    }
}