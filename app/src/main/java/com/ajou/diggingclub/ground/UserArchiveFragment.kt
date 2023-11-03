package com.ajou.diggingclub.ground

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentUserArchiveBinding

class UserArchiveFragment : Fragment() {
    private var _binding : FragmentUserArchiveBinding ?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.followingBtn.text = "팔로우" // TODO 팔로우하고 있는 상태인지 받아서 초기에 텍스트 값과 selected 여부 넣어줘야함
        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)
        binding.followingBtn.setOnClickListener {
            if(binding.followingBtn.isSelected){
                binding.followingBtn.isSelected = false
                binding.followingBtn.text = "팔로잉"
            }else{
                binding.followingBtn.isSelected = true
                binding.followingBtn.text = "팔로우"
            }
        }
    }
}