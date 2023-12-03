package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentEditSelectImageBinding
import com.ajou.diggingclub.profile.EditAlbumInfoViewModel
import com.ajou.diggingclub.utils.getMultipartFile
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide

class EditSelectImageFragment : Fragment() {

    private var _binding: FragmentEditSelectImageBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private lateinit var viewModel : EditAlbumInfoViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[EditAlbumInfoViewModel::class.java]
        _binding = FragmentEditSelectImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : EditSelectImageFragmentArgs by navArgs()
        val parsedUri = Uri.parse(args.uri)

        Glide.with(mContext!!)
            .load(parsedUri)
            .apply(multiOptions)
            .into(binding.image)

        viewModel.setUri(parsedUri)

        binding.use.setOnSingleClickListener {
            findNavController().navigate(R.id.action_editSelectImageFragment_to_editAlbumFragment)
        }

        binding.rePhoto.setOnSingleClickListener {
            findNavController().navigate(R.id.action_editSelectImageFragment_to_cameraFragment)
            viewModel.setEmpty()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
            viewModel.setEmpty()
        }
    }
}