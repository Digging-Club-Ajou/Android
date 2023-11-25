package com.ajou.diggingclub.ground.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentBottomSheetBinding
import com.ajou.diggingclub.ground.adapter.MelodyCardRVAdapter
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.CardService
import com.ajou.diggingclub.network.api.FavoriteService
import com.ajou.diggingclub.network.api.FollowingService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.AdapterToFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*
import org.json.JSONObject

class BottomSheetFragment() : BottomSheetDialogFragment(), AdapterToFragment {
    private var mContext : Context? = null
    private var _binding : FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val cardService = RetrofitInstance.getInstance().create(CardService::class.java)
    private val followingService = RetrofitInstance.getInstance().create(FollowingService::class.java)
    private var list : ArrayList<ReceivedMelodyCardModel> = arrayListOf()

    var accessToken : String? = null
    var refreshToken : String? = null
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
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(mContext!!,R.style.CustomDialog)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            // 다이얼로그 크기 설정 (인자값 : DialogInterface)
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataStore = UserDataStore()
        var memberId : String? = null
        var albumId : String? = null
        var nickname : String? = null
        var userId : String? = null

        arguments?.let {
            memberId = it.getString("memberId")
            albumId = it.getString("albumId")
            nickname = it.getString("name")
        }
        binding.backBtn.setOnClickListener {
            dismiss()
        }
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            val followingStatusDeferred = async { followingService.getFollowingStatus(accessToken!!,refreshToken!!, memberId!!) }
            if(albumId == null){ // LikeMelodyFragment
                val result = favoriteService.getFavoriteList(accessToken!!,refreshToken!!,memberId!!)
                if(result.isSuccessful){
                    val favList = result.body()!!.cardFavoriteResponses.map { favoritesModel ->
                        async {
                            val cardResult = cardService.getCard(
                                accessToken!!,
                                refreshToken!!,
                                favoritesModel.melodyCardId.toString()
                            ).execute()
                            cardResult.body()
                        }
                    }.awaitAll().filterNotNull()
                    list.addAll(favList)
                    withContext(Dispatchers.Main){
                        binding.cardRV.adapter = MelodyCardRVAdapter(mContext!!,favList,this@BottomSheetFragment,"rv")
                        binding.cardRV.layoutManager = LinearLayoutManager(mContext)
                        binding.title.text = String.format(resources.getString(R.string.likelist_nickname),nickname)
                        binding.nickname.text = nickname
                    }
                }
            }else{ // AlbumFragment
                val result = cardService.getAlbumCards(accessToken!!,refreshToken!!,albumId!!)
                if(result.isSuccessful){
                    val list = result.body()?.melodyCardListResult
                    val title = nickname // nickname으로 앨범명 넘겨받았기 때문에 title에 값 넣어주는 과정
                    nickname = list?.get(0)?.nickname
                    withContext(Dispatchers.Main){
                        binding.cardRV.adapter = MelodyCardRVAdapter(mContext!!,list!!,this@BottomSheetFragment, "rv")
                        binding.cardRV.layoutManager = LinearLayoutManager(mContext)
                        binding.title.text = title
                        binding.nickname.text = nickname
                    }

                } //  TODO 마저 작성하기
                else{
                    Log.d("error",result.errorBody()?.string().toString())
                }

            }

            val followingStatusResponse = followingStatusDeferred.await()
            if(followingStatusResponse.isSuccessful){
                // 버튼 변경
                val body = JSONObject(followingStatusResponse.body()?.string())
                if(body.get("isFollowing") == true){
                    binding.followingBtn.isSelected = true
                    binding.followingBtn.text = "팔로우"
                }else{
                    if(memberId.toString() == userId){
                        binding.followingBtn.isSelected = true
                        binding.followingBtn.text = "팔로우"
                    }else{
                        binding.followingBtn.isSelected = false
                        binding.followingBtn.text = "팔로잉"
                    }
                }
            }
        }
    }

    override fun getSelectedItem(item: ReceivedAlbumModel) {
        // TODO
    }

    override fun getSelectedId(memberId: String, albumId: String, name : String, type: String) {
        // TODO
    }
    override fun postFavoriteId(melodyCardId: String, isLike: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            if(isLike){
                val response = favoriteService.postFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
                    Log.d("success add",response.body().toString())
                }else{
                    Log.d("error ",response.errorBody()?.string().toString())
                    // TODO 실패했으면 하트 눌린 거 취소되어야 할 듯?
                }
            }else{
                val response = favoriteService.deleteFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
                    Log.d("success delete",response.body().toString())
                }else{
                    Log.d("error",response.errorBody()?.string().toString())
                }
            }
        }
    }

    override fun addRecordCount(position: Int) {
        // TODO
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 90 / 100
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}