package com.example.recommendationapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.recommendationapp.R


class RecommendationFragment : Fragment() {

    private lateinit var recomList : ListView
    private lateinit var recomAdapter : ArrayAdapter<String>
    private lateinit var recomButton : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recommendation, container, false)

        recomList = view.findViewById(R.id.recomId_listview)
        recomAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        recomList.adapter = recomAdapter

        recomButton = view.findViewById(R.id.recomBtn)
        recomButton.setOnClickListener {

        }


        return view
    }

}