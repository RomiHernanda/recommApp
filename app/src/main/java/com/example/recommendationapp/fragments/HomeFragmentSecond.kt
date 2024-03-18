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
import androidx.navigation.fragment.findNavController
import com.example.recommendationapp.R
import com.example.recommendationapp.data.ProductList
import com.example.recommendationapp.data.ProductListSecond
import com.example.recommendationapp.data.ProductMappingRepo
import com.example.recommendationapp.data.ProductRepoSecond
import com.example.recommendationapp.ml.Danarecmodel
import com.example.recommendationapp.ml.Danarecmodelshortened
import com.example.recommendationapp.ml.Danarecomfeatures
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_homeadvanced, container, false)

        //These 3 only for runInference() (w/ interpreter)
//        val modelBuffer = loadModelFile()
//        val options = Interpreter.Options()
//        interpreter = Interpreter(modelBuffer, options)

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
            val productId = productList.getProdId(selectedItemName)
            val productCat = productList.getProdCat(selectedItemName)
            val productRat = productList.getProdRat(selectedItemName)
            val productPop = productList.getProdPop(selectedItemName)
            if (productId != null && productCat != null && productRat != null && productPop != null) {
                val productList = ProductListSecond(productId, selectedItemName, productCat, productRat, productPop)
                chosenItemList.add(productList)
                chosenProductIdList.add(ProductListSecond(productId, productCat, productRat, productPop))
                chosenItemAdapter.notifyDataSetChanged()
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
                val inferenceResult = runInferenceShort(chosenProductIdList)
//                val inferenceResultArray = inferenceResult.toTypedArray()
                val action = HomeFragmentSecondDirections.actionHomeFragmentSecondToRecommendationFragmentSecond(inferenceResult)
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

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = requireContext().assets.openFd("danarecmodel.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun runInference(inputArray: FloatArray): Array<String> {
        val outputArray = FloatArray(OUTPUT_SIZE)
        interpreter.run(inputArray, outputArray)
        return processOutput(outputArray)
    }

    private fun runInferenceSec(inputArray: FloatArray): List<String> {
        val model = Danarecmodel.newInstance(requireContext())

        // Prepare input buffer
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
        inputFeature0.loadArray(inputArray)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val outputArray = outputFeature0.floatArray

        // Release model resources
        model.close()

        // Process output tensor
        val outputList = mutableListOf<String>()
        for (value in outputArray) {
            outputList.add(value.toString())
        }

        return outputList
    }

    private fun runInferenceWithFrequencies(chosenProductList: List<ProductList>): List<String> {
        val model = Danarecmodel.newInstance(requireContext())
//        val outputFrequencies = mutableMapOf<String, Int>()
        val outputProductIds = mutableListOf<String>()
        val vocabSize = 7

        for (product in chosenProductList) {
            val encodedInput = encodeInput(product.productId, vocabSize)
            val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, vocabSize), DataType.FLOAT32) //other option : (1,1) instead of (1,product.productId.length)
            //inputFeature.loadArray(intArrayOf(product.productId.toInt()))
            //val productIdIndex = productIdToIndex(product.productId)
            //inputFeature.loadArray(floatArrayOf(productIdFloat.toFloat())) //original
            inputFeature.loadArray(encodedInput)


            println("${product.productId.toFloat()} | ${product.productId}")
            //println("${product.productId.toDouble()} | ${product.productId}")
            println("Encoded input: ")
            encodedInput.forEachIndexed { index, value ->
                print("Element $index: $value")
            }

            val outputs = model.process(inputFeature)
            val outputFeature = outputs.outputFeature0AsTensorBuffer
            println("Output tensor shape: ${outputFeature.shape.contentToString()}")
            println("Output tensor values: ${outputFeature.floatArray.contentToString()}")
            println("Output tensor values: $outputFeature")

            val outputProductId = outputFeature.floatArray[0].toString()
            outputProductIds.add(outputProductId)
//            val outputProductId = outputValue.toString()
            // Log the generated outputProductId for debugging
            Log.d("InferenceResult", "Generated Output Product ID: $outputProductId")

        }

        model.close()

        return outputProductIds
    }

    private fun runInferenceShort(chosenProductList: List<ProductListSecond>): Float {
        // Create the TFLite model instance
        val model = Danarecomfeatures.newInstance(requireContext())

        // Initialize lists to store input data
        val inputFeatureBuffers = mutableListOf<TensorBuffer>()

        // Prepare input data for each chosen product
        for (product in chosenProductIdList) {
            // Create input features for the chosen product
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
            inputFeature0.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(product.productId.toFloat()).rewind() as ByteBuffer) // Assuming productId is Float

            val inputFeature1 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
            inputFeature1.loadBuffer(ByteBuffer.allocateDirect(4).putFloat(product.productCat.toFloat()).rewind() as ByteBuffer) // Assuming productCat is Float

            val inputFeature2 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
            inputFeature2.loadBuffer((ByteBuffer.allocateDirect(4).putFloat(product.productRating.toFloat()) as ByteBuffer).rewind() as ByteBuffer) // Assuming productRating is Float

            val inputFeature3 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
            inputFeature3.loadBuffer((ByteBuffer.allocateDirect(4).putFloat(product.productPopular.toFloat()) as ByteBuffer).rewind() as ByteBuffer) // Assuming productPopular is Float


            // Add input features to the list
            inputFeatureBuffers.add(inputFeature0)
            inputFeatureBuffers.add(inputFeature1)
            inputFeatureBuffers.add(inputFeature2)
            inputFeatureBuffers.add(inputFeature3)
        }

        val inputFeature0 = inputFeatureBuffers[0]
        val inputFeature1 = inputFeatureBuffers[1]
        val inputFeature2 = inputFeatureBuffers[2]
        val inputFeature3 = inputFeatureBuffers[3]

        // Runs model inference and gets result
        val outputs = model.process(inputFeature0, inputFeature1, inputFeature2, inputFeature3)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Extract the recommended product ID from the output feature
        val recommendedProductId = outputFeature0.floatArray[0] // Retain as Float


//        for (inputFeatureBuffer in inputFeatureBuffers) {
//            // Runs model inference and gets result
//            val outputs = model.process(inputFeatureBuffer)
//            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//            // Extract the recommended product ID from the output feature
//            val recommendedProductId = outputFeature0.floatValue.toString() // Convert to String
//
//            // Add the recommended product ID to the list
//            outputProductIds.add(recommendedProductId)
//        }

        // Release model resources
        model.close()

        return recommendedProductId

    }

    private fun runInferenceShortTwo(context: Context, chosenProductList: List<ProductListSecond>): List<String> {
//        val assetManager = context.assets
//        val fileDescriptor = assetManager.openFd("danarecmodelshortened.tflite")
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//
//        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//        // Load the TensorFlow Lite model
//        val interpreter = Interpreter(modelBuffer)
        // Load the TensorFlow Lite model
        val assetManager = context.assets
        val modelInputStream = assetManager.open("danarecmodelshortened.tflite") //GANTI MODEL BARU
        val modelBuffer = modelInputStream.readBytes()

        val interpreter = Interpreter(ByteBuffer.wrap(modelBuffer))

        // Prepare input data for inference
        val inputShape = interpreter.getInputTensor(0).shape()
        val inputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        val productIds = chosenProductList.map { it.productId.toFloat() }.toFloatArray()
//        inputBuffer.loadArray(floatArrayOf(productIds.toFloat(),
//            displacement.toFloat(),
//            hp.toFloat(),weight.toFloat(),
//            acceleration.toFloat(),year.toFloat(),europe,japan,usa))
        //inputBuffer.loadArray(floatArrayOf(0f)) // Assuming input is always 0 for this example
        inputBuffer.loadArray(productIds)

        // Allocate space for the output tensor
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

        // Run inference
        interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())

        // Obtain the recommendations
        val outputData = outputBuffer.floatArray
        // Log the raw output data
        Log.d("OutputData", "Raw output data: ${outputData.contentToString()}")
        val recommendations = outputData.map { it.toString() }

        // Log the recommendations to the console
        Log.d("Recommendations", "Model output: $recommendations")

        // Close the interpreter
        interpreter.close()

        return recommendations
    }



    private fun encodeInput(productId: String, vocabSize: Int): FloatArray {
        val encodedInput = FloatArray(vocabSize)
        try {
            val index = productId.toInt()
            if (index in 0 until vocabSize) {
                encodedInput[index] = 1.0f
            } else {
                throw IllegalArgumentException("Product ID index out of range: $productId")
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid product ID format: $productId")
        }
        return encodedInput

//        val index = productId.toInt()
//        if (index < vocabSize) {
//            encodedInput[index] = 1.0f
//        } else {
//            throw IllegalArgumentException("Product ID index out of range: $productId")
//        }
//        return encodedInput
    }

//    private fun runInferencne(chosenItemList: List<ProductList>, productMappings: ProductMappingRepo): Pair<String?, String?> { //for counting freq manually
//        val frequencyMap = mutableMapOf<String, Int>()
//
//        for (item in chosenItemList) {
//            val productId = item.productId
//            if (frequencyMap.containsKey(productId)) {
//                frequencyMap[productId] = frequencyMap[productId]!! + 1
//            } else {
//                frequencyMap[productId] = 1
//            }
//        }
//
//        val mostFrequentProductId = frequencyMap.maxByOrNull { it.value }?.key
//        val mostFrequentProductName = mostFrequentProductId?.let { productMappings.getProdName(it) }
//        return Pair(mostFrequentProductId, mostFrequentProductName)
//    }

    private fun processOutput(outputArray: FloatArray): Array<String> {
        // Logic to process the output array and generate recommendations
        // This depends on how your model is designed and what output it generates
        // Replace this with your actual processing logic
        return outputArray.map { it.toString() }.toTypedArray()
    }

    companion object {
        private const val OUTPUT_SIZE = 5 // Adjust this according to your model's output
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

//    private fun runInference(inputArray: FloatArray) : FloatArray {
//        val model = Danarecmodel.newInstance(requireContext())
//
//
//        // Prepare input buffer
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, inputArray.size), DataType.FLOAT32)
//        inputFeature0.loadArray(inputArray)
//
//        // Run inference
//        val outputs = model.process(inputFeature0)
//        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//        // Convert output TensorBuffer to FloatArray
//        val outputArray = outputFeature0.floatArray
//
//        // Release model resources
//        model.close()
//
//        return outputArray
//    }
}