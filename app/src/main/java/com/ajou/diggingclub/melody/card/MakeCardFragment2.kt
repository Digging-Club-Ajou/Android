package com.ajou.diggingclub.melody.card

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentMakeCard2Binding
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MakeCardFragment2 : Fragment() {

    private var _binding: FragmentMakeCard2Binding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

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
        _binding = FragmentMakeCard2Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : MakeCardFragment2Args by navArgs()

        binding.artist.text = args.music.artist
        binding.title.text = args.music.title

        val parsedUri = Uri.parse(args.uri)
        val multiOptions = MultiTransformation(
            CenterCrop(),
            RoundedCorners(10)
        ) // glide 옵션

        Glide.with(requireActivity())
            .load(parsedUri)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(multiOptions))
            .into(binding.image)

        binding.use.setOnSingleClickListener {
            val action = MakeCardFragment2Directions.actionMakeCardFragment2ToMakeCardFragment3(
                args.uri,
                args.music
            )
            findNavController().navigate(action)
        }
        binding.rePhoto.setOnSingleClickListener {
            val action = MakeCardFragment2Directions.actionMakeCardFragment2ToCameraFragment("card",args.music)
            findNavController().navigate(action)
        }

    }
}