package com.ajou.diggingclub.melody.album

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentMakeAlbum1Binding
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.profile.MyAlbumViewModel
import com.ajou.diggingclub.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MakeAlbumFragment1 : Fragment() {

    private var _binding: FragmentMakeAlbum1Binding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val userService = RetrofitInstance.getInstance().create(UserService::class.java)
    var accessToken : String? = null
    var refreshToken : String? = null

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
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakeAlbum1Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var nickname : String ?= null

        CoroutineScope(Dispatchers.Main).launch {
            var flag = dataStore.getAlbumExistFlag()
            if(flag) findNavController().navigate(R.id.action_upload_to_findMusicFragment)
            else{
                nickname = dataStore.getNickname()
                binding.username.text = nickname
                binding.video.visibility = View.VISIBLE
            }
            withContext(Dispatchers.IO){
                var accessToken = dataStore.getAccessToken().toString()
                var refreshToken = dataStore.getRefreshToken().toString()
                val result = userService.getNickname(accessToken,refreshToken)
                if(result.isSuccessful){
                    val body = JSONObject(result.body()?.string())
                    nickname = body.get("nickname").toString()
                    dataStore.saveNickname(nickname!!)
                    withContext(Dispatchers.Main){
                        binding.username.text = nickname
                        binding.video.visibility = View.VISIBLE
                    }
                }else{
//                    Log.d("response is not successful", result.errorBody()?.string().toString())
                }
            }
        }

        val videoPath =
            "android.resource://" + mContext!!.packageName + "/" + R.raw.albumexample
        val uri = Uri.parse(videoPath)
        binding.video.setVideoURI(uri)

        binding.video.setOnPreparedListener {mediaPlayer ->
            mediaPlayer.setOnInfoListener { _, what, _ ->
                what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                }
        }
        binding.video.setOnCompletionListener { mediaPlayer ->
            binding.video.stopPlayback()
        }

        binding.video.start()  // video 관련 코드

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
    }

}