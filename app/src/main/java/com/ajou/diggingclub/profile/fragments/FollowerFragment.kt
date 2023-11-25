package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentFollowerBinding
import com.ajou.diggingclub.ground.FollowDataViewModel
import com.ajou.diggingclub.ground.adapter.FollowRVAdapter
import com.ajou.diggingclub.melody.card.SearchLocationFragmentDirections

class FollowerFragment : Fragment() {
    private var mContext : Context? = null
    private lateinit var viewModel : FollowDataViewModel
    private var _binding : FragmentFollowerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFollowerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
        viewModel = ViewModelProvider(requireActivity())[FollowDataViewModel::class.java]
        val adapter = FollowRVAdapter(mContext!!, emptyList(),viewModel.userId.value)

        binding.followerRV.adapter = adapter
        binding.followerRV.layoutManager = LinearLayoutManager(mContext)

        viewModel.followerList.observe(viewLifecycleOwner) { followers ->
            adapter.updateList(followers)
        }
    }

    inner class AdapterToFragment{
        fun getSelectedItem(data : String) {

        }
    }
}