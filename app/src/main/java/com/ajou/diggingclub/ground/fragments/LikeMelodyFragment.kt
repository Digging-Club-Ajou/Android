package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentLikeMelodyBinding
import com.ajou.diggingclub.ground.adapter.MelodyLikeRVAdapter
import com.ajou.diggingclub.ground.models.FavoritesModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.FavoriteService
import com.ajou.diggingclub.network.api.MusicService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.requestOptions
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LikeMelodyFragment : Fragment() {
    private var _binding : FragmentLikeMelodyBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicServiceClient = RetrofitInstance.getInstance().create(MusicService::class.java)

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

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            val response = favoriteService.getFavoriteList(accessToken!!, refreshToken!!, args.id)
            if(response.isSuccessful){
                list = response.body()!!.cardFavoriteResponses
                withContext(Dispatchers.Main){
                    binding.likeListRV.adapter = MelodyLikeRVAdapter(mContext!!, list!!)
                    binding.likeListRV.layoutManager = LinearLayoutManager(mContext)
                }
            }
        }
        binding.userNickname.text = String.format(resources.getString(R.string.melody_nickname),args.nickname)
        binding.likeListNickname.text = String.format(resources.getString(R.string.likelist_nickname),args.nickname)
        binding.noRecordText.text = String.format(resources.getString(R.string.no_record),args.nickname)

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
            bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
        }
        binding.likeImage.setOnClickListener {
            bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val response = musicServiceClient.getUserRecord(accessToken!!, refreshToken!!, args.id)
            if(response.isSuccessful){
                Log.d("response",response.body().toString())
                val body = JSONObject(response.body()?.string())
                Log.d("body",body.get("genre").toString())
                Log.d("body",body.get("artistName").toString())
                Log.d("body",body.get("songTitle").toString())
                binding.record.text = String.format(resources.getString(R.string.record),args.nickname,body.get("genre").toString(),body.get("artistName").toString(),body.get("songTitle").toString())

                val spannableString = SpannableString(binding.record.text)
                val textColor = ColorStateList.valueOf(Color.parseColor("#EE8DC6"),)

                spannableString.setSpan(
                    TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
                    binding.record.text.indexOf(body.get("genre").toString()),
                    binding.record.text.indexOf(body.get("genre").toString()) + body.get("genre").toString().length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
                    binding.record.text.indexOf(body.get("artistName").toString()),
                    binding.record.text.indexOf(body.get("artistName").toString()) + body.get("artistName").toString().length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    TextAppearanceSpan(null, android.graphics.Typeface.BOLD, 0, textColor, null),
                    binding.record.text.indexOf(body.get("songTitle").toString()),
                    binding.record.text.indexOf(body.get("songTitle").toString()) + body.get("songTitle").toString().length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )

                binding.record.text = spannableString
            }else{
                Log.d("error in like",response.errorBody()?.string().toString())
            }

            if(list.isNotEmpty()){
                binding.noRecordText.visibility = View.GONE
                val drawable = ContextCompat.getDrawable(mContext!!,R.drawable.likeimage)?.mutate() as LayerDrawable
                withContext(Dispatchers.IO){
                        val layerCount = drawable.numberOfLayers
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
            }else{
                binding.record.visibility = View.GONE
                binding.likeListNickname.visibility = View.GONE
                binding.likeListRV.visibility = View.GONE
            }
        }

    }
}