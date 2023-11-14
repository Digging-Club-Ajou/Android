package com.ajou.diggingclub.melody.album

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentMakeAlbum1Binding
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.UserApi
import com.ajou.diggingclub.start.StartViewModel
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeAlbumFragment1 : Fragment() {

    private var _binding: FragmentMakeAlbum1Binding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val client = RetrofitInstance.getInstance().create(UserApi::class.java)
    var accessToken : String? = null
    var refreshToken : String? = null
    private val viewModel : StartViewModel by viewModels()

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

        CoroutineScope(Dispatchers.IO).launch {
            var accessToken = dataStore.getAccessToken().toString()
            var refreshToken = dataStore.getRefreshToken().toString()
            val result = client.getNickname(accessToken,refreshToken)
            if(result.isSuccessful){
                val body = JSONObject(result.body()?.string())
                nickname = body.get("nickname").toString()
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveNickname(body.get("nickname").toString())
                }
            }else{
                Log.d("response is not successful", result.errorBody()?.string().toString())
            }
            withContext(Dispatchers.Main){
                var flag = dataStore.getAlbumExistFlag()
                if(flag){
                    findNavController().navigate(R.id.action_upload_to_findMusicFragment)
                }else{
                    binding.username.text = nickname
                }
            }
        }

        val videoPath =
            "android.resource://" + mContext!!.packageName + "/" + R.raw.albumexample
        val uri = Uri.parse(videoPath)
        binding.video.setVideoURI(uri)

        binding.video.setOnCompletionListener { mediaPlayer ->
            binding.video.stopPlayback()
        }

        binding.video.start()  // video 관련 코드

        binding.backBtn.setOnSingleClickListener {
            findNavController().navigate(R.id.action_upload_to_findMusicFragment)
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