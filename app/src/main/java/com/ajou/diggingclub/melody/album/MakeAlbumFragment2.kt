package com.ajou.diggingclub.melody.album

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentMakeAlbum2Binding
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MakeAlbumFragment2 : Fragment() {

    private var _binding: FragmentMakeAlbum2Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakeAlbum2Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: MakeAlbumFragment2Args by navArgs()
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

        binding.use.setOnClickListener {
            val action = MakeAlbumFragment2Directions.actionMakeAlbumFragment2ToMakeAlbumFragment3(args.uri)
            findNavController().navigate(action)
        }

        binding.rePhoto.setOnClickListener {
            val action = MakeAlbumFragment2Directions.actionMakeAlbumFragment2ToCameraFragment("album",
                MusicSpotifyModel("","","","")
            )
            findNavController().navigate(action)
        }
    }

}