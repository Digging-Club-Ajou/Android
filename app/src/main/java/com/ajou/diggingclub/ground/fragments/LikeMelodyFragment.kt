package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentLikeMelodyBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.ground.adapter.MelodyLikeRVAdapter
import com.ajou.diggingclub.ground.models.FavoritesModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.FavoriteService
import com.ajou.diggingclub.network.api.MusicService
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.requestOptions
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject

class LikeMelodyFragment : Fragment() {
    private var _binding : FragmentLikeMelodyBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicServiceClient = RetrofitInstance.getInstance().create(MusicService::class.java)
    private val TAG = LikeMelodyFragment::class.java.simpleName
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
        _binding = FragmentLikeMelodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetDialogFragment = BottomSheetFragment()
        var accessToken : String? = null
        var refreshToken : String? = null
        val dataStore = UserDataStore()
        val args : LikeMelodyFragmentArgs by navArgs()
        var list : List<FavoritesModel> = arrayListOf()
        var recordData : JSONObject? = null
        var isRecordExist = false

        lifecycleScope.launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            try{
                val favoriteDeferred = async{ favoriteService.getFavoriteList(accessToken!!, refreshToken!!, args.id) }
                val recordDeferred = async { musicServiceClient.getUserRecord(accessToken!!, refreshToken!!, args.id) }

                val favoriteResponse = favoriteDeferred.await()
                val recordResponse = recordDeferred.await()
                val isFavSuccess = favoriteResponse.isSuccessful
                val isRecordSuccess = recordResponse.isSuccessful
                if(isFavSuccess){
                    list = favoriteResponse.body()!!.cardFavoriteResponses
                    Log.d(TAG,favoriteResponse.body()!!.cardFavoriteResponses.toString())
                    withContext(Dispatchers.Main){
                        if(list.isNotEmpty()) {
                            binding.likeListRV.apply {
                                adapter = MelodyLikeRVAdapter(mContext!!, list)
                                layoutManager = LinearLayoutManager(mContext)
                            }
                            binding.playIcon.visibility = View.VISIBLE
                            setupLikeImage(list)
                        }else {
                            binding.likeListRV.adapter = null
                        }
                    }
                }
                withContext(Dispatchers.Main){
                    if(isRecordSuccess) {
                        recordData = JSONObject(recordResponse.body()?.string())
                        isRecordExist = true
                        binding.record.text = String.format(resources.getString(R.string.record),args.nickname,recordData?.get("genre").toString(),recordData?.get("mostPlayedArtistName").toString(),recordData?.get("favoriteArtistName").toString(),recordData?.get("songTitle").toString())
                        binding.record.text = setBoldStyle(recordData!!,binding.record.text.toString())
                        binding.record.visibility = View.VISIBLE
                    }else{
                        Log.d("errorBody",recordResponse.errorBody()?.string().toString())
                    }
                    when{
                        !isRecordExist && list.isNotEmpty() -> {
                            binding.noRecordText.visibility = View.VISIBLE
                        }
                        isRecordExist && list.isEmpty() -> {
                            binding.logo.visibility = View.VISIBLE
                            binding.noLikeText1.visibility = View.VISIBLE
                            binding.noLikeText2.visibility = View.VISIBLE
                        }
                        !isRecordExist && list.isEmpty() -> {
                            binding.noDataText1.visibility = View.VISIBLE
                            binding.noDataText2.visibility = View.VISIBLE
                            binding.noDataImage.visibility = View.VISIBLE
                            binding.moveBtn.visibility = View.VISIBLE
                            binding.likeImage.visibility = View.INVISIBLE
                            binding.likeListNickname.visibility = View.GONE
                        }
                    }
                }
            }catch (e:java.lang.Exception){
                Log.d(TAG,e.message.toString())
            }
        }
        binding.userNickname.text = String.format(resources.getString(R.string.melody_nickname),args.nickname)
        binding.likeListNickname.text = String.format(resources.getString(R.string.likelist_nickname),args.nickname)
        binding.noRecordText.text = String.format(resources.getString(R.string.no_record),args.nickname)

        binding.userNickname.text = setBoldType(binding.userNickname.text.toString(),args.nickname.length)
        binding.noRecordText.text = setBoldType(binding.noRecordText.text.toString(),args.nickname.length)


        val bundle = Bundle().apply {
            putString("albumId",null)
            putString("memberId",args.id)
            putString("name",args.nickname)
        }
        bottomSheetDialogFragment.arguments = bundle

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.playIcon.setOnClickListener {
            if(activity is GroundActivity){
                val action = LikeMelodyFragmentDirections.actionLikeMelodyFragmentToAlbumMelodyCardFragment(args.id, null,args.nickname,"others")
                findNavController().navigate(action)
            }else if(activity is ProfileActivity){
                val action = LikeMelodyFragmentDirections.actionLikeMelodyFragmentToAlbumMelodyCardFragment(args.id, null,args.nickname,"my")
                findNavController().navigate(action)
            }
                // TODO profile activity일 때의 navigation이..?
        }
        binding.likeImage.setOnClickListener {
            if(activity is GroundActivity){
                val action = LikeMelodyFragmentDirections.actionLikeMelodyFragmentToAlbumMelodyCardFragment(args.id,null,args.nickname,"others")
                findNavController().navigate(action)
            }else{
                val action = LikeMelodyFragmentDirections.actionLikeMelodyFragmentToAlbumMelodyCardFragment(args.id, null,args.nickname,"my")
                findNavController().navigate(action)
            }
        }
        binding.moveBtn.setOnClickListener {
            val intent = Intent(mContext, GroundActivity::class.java)
            startActivity(intent)
        }
    }
    private suspend fun setupLikeImage(list: List<FavoritesModel>) {
        val drawable = ContextCompat.getDrawable(mContext!!, R.drawable.like_image_full)?.mutate() as LayerDrawable
        val smallDrawable = ContextCompat.getDrawable(mContext!!, R.drawable.like_image_two)?.mutate() as LayerDrawable

        CoroutineScope(Dispatchers.IO).launch {
            val layerCount = drawable.numberOfLayers
            when(list.size){
                1 ->{
                    withContext(Dispatchers.Main){
                        Glide.with(mContext!!)
                            .load(list[0].albumCoverImageUrl)
                            .into(binding.likeImage)
                    }
                }
                2 -> {
                    for (i in list.indices) {
                        val bitmap = Glide.with(binding.likeImage.context)
                            .asBitmap()
                            .load(list[i].albumCoverImageUrl)
                            .submit()
                            .get()
                        val layerId = smallDrawable.getId(i)
                        val newDrawable = BitmapDrawable(binding.likeImage.resources, bitmap)
                        smallDrawable.setDrawableByLayerId(layerId, newDrawable)
                    }
                    withContext(Dispatchers.Main) {
                        Glide.with(mContext!!)
                            .load(smallDrawable)
                            .apply(requestOptions)
                            .into(binding.likeImage)
                    }
                }
                else -> {
                    for (i in 0 until layerCount) {
                        val bitmap = Glide.with(binding.likeImage.context)
                            .asBitmap()
                            .load(list[i].albumCoverImageUrl)
                            .submit()
                            .get()
                        val layerId = drawable.getId(i)
                        val newDrawable = BitmapDrawable(binding.likeImage.resources, bitmap)
                        drawable.setDrawableByLayerId(layerId, newDrawable)
                    }
                    withContext(Dispatchers.Main) {
                        Glide.with(mContext!!)
                            .load(drawable)
                            .apply(requestOptions)
                            .into(binding.likeImage)
                    }
                }
            }
            }

    }
    private fun setBoldStyle(recordData: JSONObject, text : String) : SpannableString {
        val spannableString = SpannableString(text)
        val textColor = ColorStateList.valueOf(Color.parseColor("#EE8DC6"))

        spannableString.setSpan(
            TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
            binding.record.text.indexOf(recordData?.get("genre").toString()),
            binding.record.text.indexOf(recordData?.get("genre").toString()) + recordData?.get("genre").toString().length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
            binding.record.text.indexOf(recordData?.get("mostPlayedArtistName").toString()),
            binding.record.text.indexOf(recordData?.get("mostPlayedArtistName").toString()) + recordData?.get("mostPlayedArtistName").toString().length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
            binding.record.text.indexOf(recordData?.get("favoriteArtistName").toString()),
            binding.record.text.indexOf(recordData?.get("favoriteArtistName").toString()) + recordData?.get("favoriteArtistName").toString().length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
            binding.record.text.indexOf(recordData?.get("songTitle").toString()),
            binding.record.text.indexOf(recordData?.get("songTitle").toString()) + recordData?.get("songTitle").toString().length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun setBoldType(text : String, length : Int) : SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    } // TODO setBoldyStyle, setBoldType 같은 역할 하는 함수니까 정리하기
}