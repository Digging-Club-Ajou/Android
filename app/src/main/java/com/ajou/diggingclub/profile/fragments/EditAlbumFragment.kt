package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.databinding.FragmentEditAlbumBinding
import com.ajou.diggingclub.melody.album.MakeAlbumFragment1Directions
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.profile.MyAlbumViewModel
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide

class EditAlbumFragment : Fragment() {
    private var _binding : FragmentEditAlbumBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel : MyAlbumViewModel

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = checkNotNull(result.data)
                val imageUri = intent.data // 갤러리에서 선택한 사진 받아옴
                val action = MakeAlbumFragment1Directions.actionUploadToMakeAlbumFragment3(imageUri.toString())
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[MyAlbumViewModel::class.java]
        _binding = FragmentEditAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnSingleClickListener {
            findNavController().popBackStack()
        }

        binding.camera.setOnSingleClickListener {
            val action = MakeAlbumFragment1Directions.actionUploadToCameraFragment("album",
                MusicSpotifyModel("","","","")
            )
            findNavController().navigate(action)
        }

        binding.gallery.setOnSingleClickListener {
            val intent = Intent().also { intent ->
                intent.type = "image/"
                intent.action = Intent.ACTION_GET_CONTENT
            }
            launcher.launch(intent)
        }

        Glide.with(mContext!!)
            .load(viewModel.albumInfo.value!!.imageUrl)
            .apply(multiOptions)
            .into(binding.image)
        binding.title.hint = viewModel.albumInfo.value!!.albumName
    }
}