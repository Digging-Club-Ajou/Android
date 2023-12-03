package com.ajou.diggingclub.profile.fragments

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentEditAlbumBinding
import com.ajou.diggingclub.melody.album.MakeAlbumFragment1Directions
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.models.EditAlbum
import com.ajou.diggingclub.profile.EditAlbumInfoViewModel
import com.ajou.diggingclub.profile.MyAlbumViewModel
import com.ajou.diggingclub.utils.getMultipartFile
import com.ajou.diggingclub.utils.hideKeyboard
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAlbumFragment : Fragment() {
    private var _binding : FragmentEditAlbumBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel : MyAlbumViewModel
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private lateinit var editViewModel : EditAlbumInfoViewModel

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
                val action = EditAlbumFragmentDirections.actionEditAlbumFragmentToEditSelectImageFragment(imageUri.toString())
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
        editViewModel = ViewModelProvider(requireActivity())[EditAlbumInfoViewModel::class.java]
        _binding = FragmentEditAlbumBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            hideKeyboard(requireActivity())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var accessToken : String? = null
        var refreshToken : String? = null
        val dataStore = UserDataStore()
        var img : MultipartBody.Part? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken()
            refreshToken = dataStore.getRefreshToken()
        }
        binding.nickname.text = viewModel.albumInfo.value!!.nickname
        Log.d("editViewmodel",editViewModel.title.value.toString())
        binding.title.setText(editViewModel.title.value)

        if(editViewModel.uri.value != null){
            Log.d("uri",editViewModel.uri.value.toString())
            Glide.with(mContext!!)
                .load(editViewModel.uri.value)
                .apply(multiOptions)
                .into(binding.image)
        }else{
            Glide.with(mContext!!)
                .load(viewModel.albumInfo.value!!.imageUrl)
                .apply(multiOptions)
                .into(binding.image)
        }

        binding.title.hint = viewModel.albumInfo.value!!.albumName

        binding.done.setOnSingleClickListener {
            if(editViewModel.uri.value == null){
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), "")
                img = MultipartBody.Part.createFormData("albumImage", "", requestFile)
            }else img = getMultipartFile(editViewModel.uri.value!!,requireActivity(),"albumImage")
            var requestBody : EditAlbum
            if(binding.title.text.toString() == ""){
                requestBody = EditAlbum(null)
            }else{
                requestBody = EditAlbum(binding.title.text.toString())
            }
            albumService.updateAlbum(accessToken!!, refreshToken!!,requestBody, img).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        Log.d("response is successful", response.body().toString())
                        findNavController().navigate(R.id.action_editAlbumFragment_to_myArchiveFragment)
                    }else{
                        Log.d("response is not successful", response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("onFailure", t.message.toString())
                }

            })
        }
        binding.backBtn.setOnSingleClickListener {
            findNavController().popBackStack()
        }

        binding.title.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus) binding.title.hint = ""
        }
        binding.camera.setOnSingleClickListener {
            findNavController().navigate(R.id.action_editAlbumFragment_to_cameraFragment)
            editViewModel.setTitle(binding.title.text.toString())
        }

        binding.gallery.setOnSingleClickListener {
            editViewModel.setTitle(binding.title.text.toString())
            val intent = Intent().also { intent ->
                intent.type = "image/"
                intent.action = Intent.ACTION_GET_CONTENT
            }
            launcher.launch(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        editViewModel.setEmpty()
    }
}