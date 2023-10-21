package com.ajou.diggingclub.start.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentSwipe1Binding

class SwipeFragment1 : Fragment() {
    private var _binding : FragmentSwipe1Binding ?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSwipe1Binding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

}