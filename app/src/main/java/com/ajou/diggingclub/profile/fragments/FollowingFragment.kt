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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentFollowingBinding
import com.ajou.diggingclub.ground.FollowDataViewModel
import com.ajou.diggingclub.ground.adapter.FollowRVAdapter
import com.ajou.diggingclub.utils.ManageFollow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class FollowingFragment : Fragment(), ManageFollow {
    private var mContext : Context? = null
    private lateinit var viewModel : FollowDataViewModel
    private var _binding : FragmentFollowingBinding? = null
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
        _binding = FragmentFollowingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataStore = UserDataStore()
        var adapter : FollowRVAdapter
        viewModel = ViewModelProvider(requireActivity())[FollowDataViewModel::class.java]
        CoroutineScope(Dispatchers.Main).launch {
            val userId = dataStore.getMemberId()
            adapter = FollowRVAdapter(mContext!!, emptyList(),viewModel.memberId.value, userId, "following",this@FollowingFragment)
            binding.followingRV.adapter = adapter
            binding.followingRV.layoutManager = LinearLayoutManager(mContext)
            viewModel.followingList.observe(viewLifecycleOwner) { followings ->
                adapter.updateList(followings)
            }
        }

    }

    override fun getFollowId(id: String, connect: Boolean, myFollower: Boolean) {
        Log.d("here??","here??")
    }

}