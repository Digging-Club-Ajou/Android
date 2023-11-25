package com.ajou.diggingclub.melody.card

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentMakeCard1Binding
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class MakeCardFragment1 : Fragment() {

    private var _binding: FragmentMakeCard1Binding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intent = checkNotNull(result.data)
                val imageUri = intent.data // 갤러리에서 선택한 사진 받아옴
                val args : MakeCardFragment1Args by navArgs()
                val action = MakeCardFragment1Directions.actionMakeCardFragment1ToMakeCardFragment3(imageUri.toString(),args.music)
                findNavController().navigate(action)
            }
        }
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
        _binding = FragmentMakeCard1Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args : MakeCardFragment1Args by navArgs()

        Log.d("args",args.music.artist)
        binding.artist.text = args.music.artist
        binding.title.text = args.music.title

        binding.camera.setOnSingleClickListener {
            val action = MakeCardFragment1Directions.actionMakeCardFragment1ToCameraFragment("card",args.music)
            findNavController().navigate(action)
        }
        binding.gallery.setOnSingleClickListener {
            val intent = Intent().also { intent ->
                intent.type = "image/"
                intent.action = Intent.ACTION_GET_CONTENT
            }
            launcher.launch(intent)
        }
        binding.palette.setOnSingleClickListener {
            val action = MakeCardFragment1Directions.actionMakeCardFragment1ToMakeCardFragment3(
                "",
                args.music
            )
            findNavController().navigate(action)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}