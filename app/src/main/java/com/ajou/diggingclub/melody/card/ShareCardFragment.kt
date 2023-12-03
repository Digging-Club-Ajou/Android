package com.ajou.diggingclub.melody.card

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentShareCardBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.melody.models.SendingMelodyCardModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.CardService
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.getMultipartFile
import com.ajou.diggingclub.utils.hideKeyboard
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShareCardFragment : Fragment() {

    private var _binding: FragmentShareCardBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private val cardService = RetrofitInstance.getInstance().create(CardService::class.java)

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
        _binding = FragmentShareCardBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            hideKeyboard(requireActivity())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layerDrawable = ContextCompat.getDrawable(mContext!!,R.drawable.playicon)?.mutate() as LayerDrawable
        val args : ShareCardFragmentArgs by navArgs()

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null
        var img : MultipartBody.Part? = null
        var nickname : String? = null
        var cardDescription : String? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            if(nickname !=null){
                binding.nickname.text = nickname
            }
        }

        binding.artist.text = args.music.artist
        binding.title.text = args.music.title
        binding.location.text = args.address

        layerDrawable.getDrawable(0).setColorFilter(android.graphics.Color.parseColor(args.color),
            PorterDuff.Mode.SRC_IN)
        binding.playIcon.setImageDrawable(layerDrawable)
        binding.image.setBackgroundColor(android.graphics.Color.parseColor(args.color))

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if(args.address==null){
            binding.location.visibility = View.GONE
            binding.locationIcon.visibility = View.GONE
        }

        if(args.uri.isNotEmpty()){
            val parsedUri = Uri.parse(args.uri)
            img = getMultipartFile(parsedUri,requireActivity(),"melodyImage")

            Glide.with(requireActivity())
                .load(parsedUri)
                .centerCrop()
                .into(binding.image)
            // TODO 제대로 들어가있는지 확인하기
            binding.image.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        binding.cardDescription.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus) binding.cardDescription.hint = ""
        }
        binding.cardDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0!=null){
                    binding.cardDescription.hint = ""
                    if(p0.isNotEmpty()){
                        cardDescription = binding.cardDescription.text.toString()
                    }else{
                        cardDescription = null
                    }
                }
            }
        })

        binding.shareBtn.setOnSingleClickListener {
            binding.shareBtn.isClickable = false
            val data = SendingMelodyCardModel(args.music.artist, args.music.title,"", args.music.previewUrl,args.address,args.music.imageUrl,cardDescription,args.color)
            if(img!=null){
                cardService.createCard(accessToken!!,refreshToken!!,data,img).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful){
                            if(response.headers()["AccessToken"] != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                }
                            }
                            val currentActivity = activity
                            currentActivity?.finish()
                            val intent = Intent(mContext, ProfileActivity::class.java)
                            startActivity(intent)
                        }else{
                            val errorBody = JSONObject(response.errorBody()?.string())
                            Log.d("response not successsss",errorBody.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }

                })
            }else{
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), "")
                img = MultipartBody.Part.createFormData("melodyImage", "", requestFile)

                cardService.createCard(accessToken!!,refreshToken!!,data,img).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful){
                            if(response.headers()["AccessToken"] != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                }
                            }
                            val currentActivity = activity
                            currentActivity?.finish()
                            val intent = Intent(mContext, ProfileActivity::class.java)
                            startActivity(intent)
                        }else{
                            val errorBody = JSONObject(response.errorBody()?.string())
                            Log.d("response not successsss here",errorBody.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }

                })
            }

        }


    }

}