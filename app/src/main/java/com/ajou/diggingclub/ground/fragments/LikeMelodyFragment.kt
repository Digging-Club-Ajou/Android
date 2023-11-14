package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentLikeMelodyBinding

class LikeMelodyFragment : Fragment() {
    private var _binding : FragmentLikeMelodyBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null

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
        _binding = FragmentLikeMelodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetDialogFragment = BottomSheetFragment(mContext!!)
        val drawableResourceId = R.drawable.likeimage
        val drawable = resources.getDrawable(drawableResourceId, null)
        binding.likeImage.setImageDrawable(drawable)

        binding.playIcon.setOnClickListener {
            bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
        }
    }
}