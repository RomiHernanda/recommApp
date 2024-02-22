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
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.R
import com.example.recommendationapp.data.ProductsViewModel

class HomeFragment : Fragment() {

    private lateinit var spinner : Spinner
    private lateinit var listView : ListView
    private lateinit var addButton : Button

    private lateinit var chosenItemList : MutableList<String>
    private lateinit var chosenItemAdapter: ArrayAdapter<String>
    private lateinit var homeViewModel : ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        spinner = view.findViewById(R.id.prodId_spinner)
        listView = view.findViewById(R.id.prodId_listview)
        addButton = view.findViewById(R.id.addBtn)

        homeViewModel = ViewModelProvider(requireActivity()).get(ProductsViewModel::class.java)

        chosenItemList = mutableListOf()
        chosenItemAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, chosenItemList)
        listView.adapter = chosenItemAdapter

        addButton.setOnClickListener {
            val selectedItem = spinner.selectedItem.toString()
            homeViewModel.chosenItemList.value = homeViewModel.chosenItemList.value.orEmpty() + selectedItem
            updateListView()
        }

        return view
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            chosenItemList
        )
        listView.adapter = adapter
    }

    private fun prepInputData(chosenItemsList: List<String>): FloatArray {
        val arrayToIdMap = mapOf(
            "51" to 51051000101000100051f,
            "72" to 51051000101000100072f,
            "71" to 51051000101000100071f,
            "02" to 51051000101000100002f,
            "73" to 51051000101000100073f,
            "01" to 51051000101000100001f,
            "48" to 51051000101000100048f
        )
        //val numInputArray = chosenItemsList.map { arrayToIdMap[it] ?: "" }.toFloatArray()
        val floatList = chosenItemsList.map { arrayToIdMap[it] ?: -1.0f }
        val numInputArray = FloatArray(floatList.size) {floatList[it] }
        return numInputArray
    }

}