package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentAlbumMelodyCardBinding
import com.ajou.diggingclub.ground.FollowDataViewModel
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.ground.adapter.MelodyCardRVAdapter
import com.ajou.diggingclub.ground.fragments.BottomSheetFragment
import com.ajou.diggingclub.ground.fragments.GroundFragmentDirections
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.*
import com.ajou.diggingclub.profile.EditMelodyCardRVAdapter
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.AdapterToFragment
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AlbumMelodyCardFragment : Fragment(), AdapterToFragment {
    private var _binding : FragmentAlbumMelodyCardBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val TAG = AlbumMelodyCardFragment::class.java.simpleName

    private val cardService = RetrofitInstance.getInstance().create(CardService::class.java)
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicService = RetrofitInstance.getInstance().create(MusicService::class.java)
    private val followingService = RetrofitInstance.getInstance().create(FollowingService::class.java)
    private val viewModel : FollowDataViewModel by viewModels()

    private var list : ArrayList<ReceivedMelodyCardModel> = arrayListOf()
    private var originalList : ArrayList<ReceivedMelodyCardModel> = arrayListOf()
    private val args : AlbumMelodyCardFragmentArgs by navArgs()
    private lateinit var adapter : EditMelodyCardRVAdapter

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
        _binding = FragmentAlbumMelodyCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        val albumId = args.albumId
        val memberId = args.memberId
        var nickname = args.name
        val isMine = args.type // my or others
        var userId : String ?= null
        var isData : Boolean = false
        var cardList = ArrayList<ReceivedMelodyCardModel>()

        Log.d(TAG,"Args  : ${args.albumId} ${args.memberId} ${args.name} ${args.type}")
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId()
            Log.d("acessToken",accessToken.toString())
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            Log.d("albumId",albumId.toString())
            val followingStatusDeferred = async { followingService.getFollowingStatus(accessToken!!,refreshToken!!, args.memberId) }
            if(albumId == null){ // LikeMelodyFragment
                val result = favoriteService.getFavoriteList(accessToken!!,refreshToken!!,memberId)
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
                    originalList.addAll(favList)
                    withContext(Dispatchers.Main){
                        if(args.name == nickname){ // 둘 다 닉네임
                            adapter = EditMelodyCardRVAdapter(mContext!!,list,this@AlbumMelodyCardFragment, "rv")
                            binding.cardRV.adapter = adapter
                            binding.cardRV.layoutManager = LinearLayoutManager(mContext)

                        }else{
                            binding.cardRV.adapter = MelodyCardRVAdapter(mContext!!,favList,this@AlbumMelodyCardFragment,"rv")
                            binding.cardRV.layoutManager = LinearLayoutManager(mContext)
                        }
                        binding.albumName.text = "LIKE PLAY"
                        binding.nickname.text = nickname
                        isData = true
                    }
                }
            }else{ // AlbumFragment
                val result = cardService.getAlbumCards(accessToken!!,refreshToken!!,albumId)
                if(result.isSuccessful){
                    val tmp = result.body()?.melodyCardListResult
                    originalList.addAll(tmp!!)
                    list.addAll(tmp)
                    nickname = list[0].nickname
                    Log.d(TAG,"nickname is changed!")
                }
                else{
                    Log.d("error",result.errorBody()?.string().toString())
                }
                withContext(Dispatchers.Main){
                    if(nickname != args.name){ // args.name은 앨범이름, nickname은 닉네임
                        adapter = EditMelodyCardRVAdapter(mContext!!,list,this@AlbumMelodyCardFragment, "rv")
                        binding.cardRV.adapter = adapter
                        binding.cardRV.layoutManager = LinearLayoutManager(mContext)
                        binding.albumName.text = args.name
                        binding.nickname.text = nickname
                        isData = true
                    }else{
                        Log.d(TAG,"data is blank")
                        binding.noDataImage.visibility = View.VISIBLE
                        binding.noDataText1.visibility = View.VISIBLE
                        binding.noDataText2.visibility = View.VISIBLE
                        binding.moveBtn.visibility = View.VISIBLE

                        binding.profileIcon.visibility = View.GONE
                        binding.nickname.visibility = View.GONE
                        binding.cardRV.visibility = View.GONE
                        binding.editBtn.visibility = View.GONE
                        binding.followingBtn.visibility = View.GONE
                    }
                }
            }
            if(isData){
                Log.d(TAG,"isData is true")
                val followingStatusResponse = followingStatusDeferred.await()
                if(followingStatusResponse.isSuccessful){
                    // 버튼 변경
                    val body = JSONObject(followingStatusResponse.body()?.string())
                    withContext(Dispatchers.Main){
                        if(isMine == "my") binding.editBtn.visibility = View.VISIBLE
                        else binding.followingBtn.visibility = View.VISIBLE

                        if(body.get("isFollowing") == true){
                            binding.followingBtn.isSelected = true
                            binding.followingBtn.text = "팔로잉"
                        }else{
                            if(userId.toString() == args.memberId){
                                Log.d("userId is ","same")
                                binding.followingBtn.visibility = View.INVISIBLE
                                binding.editBtn.visibility = View.VISIBLE
                            }else{
                                binding.followingBtn.isSelected = false
                                binding.followingBtn.text = "팔로우"
                            }
                        }
                    }
                }
            }
        }

        binding.albumName.text = args.name // 여기서 name은 albumName임
        binding.editBtn.isSelected = true
        binding.cancelBtn.isSelected = true

        binding.followingBtn.setOnSingleClickListener {
            if(binding.followingBtn.isSelected){
                binding.followingBtn.isSelected = false
                binding.followingBtn.text = "팔로우"
                viewModel.deleteFollowing(accessToken!!, refreshToken!!,userId!!,args.memberId)
            }else{
                binding.followingBtn.isSelected = true
                binding.followingBtn.text = "팔로잉"
                viewModel.postFollowing(accessToken!!, refreshToken!!, args.memberId)
            }
        }
        binding.moveBtn.setOnClickListener {
            val intent = Intent(mContext, GroundActivity::class.java)
            startActivity(intent)
        }
        binding.editBtn.setOnSingleClickListener {
            binding.cancelBtn.visibility = View.VISIBLE
            binding.confirmBtn.visibility = View.VISIBLE
            binding.editBtn.visibility = View.INVISIBLE
            adapter.setViewType("edit")
            binding.cardRV.apply {
                itemAnimator = null
            }
        }
        binding.cancelBtn.setOnSingleClickListener {
            binding.cancelBtn.visibility = View.GONE
            binding.confirmBtn.visibility = View.GONE
            binding.editBtn.visibility = View.VISIBLE
            adapter.setViewType("default")
            Log.d(TAG,"list : $list")
            list.clear()
            list.addAll(originalList)
            Log.d(TAG, "originalList : $originalList")
            adapter.notifyDataSetChanged()

        }
        binding.confirmBtn.setOnSingleClickListener {
            binding.cancelBtn.visibility = View.GONE
            binding.confirmBtn.visibility = View.GONE
            binding.editBtn.visibility = View.VISIBLE
            adapter.setViewType("default")
            val removedList = originalList.subtract(list).toList()
            Log.d(TAG, "list : $list")
            Log.d(TAG,"originalList : $originalList")
            originalList.clear()
            originalList.addAll(list)
            Log.d("removedList",removedList.toString())
            for(i in removedList.indices){
                cardService.deleteCard(accessToken!!,refreshToken!!,removedList[i].melodyCardId.toString()).enqueue(object : Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful){
                            Log.d("success",response.body().toString())
                        }else{
                            Log.d("error",response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }

                })

            }

        }
        binding.backBtn.setOnSingleClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getSelectedItem(item: ReceivedAlbumModel) {
        // TODO
    }

    override fun getSelectedId(memberId: String, albumId: String, name : String, type: String) {
        when(type){
            "card" -> {
//                // TODO BottomSheetFragment를 아예 대체할 수 있으려면 좀 더 로직 고민해서 고쳐야 할 듯
                Log.d("findNavController","findNavController")

//                val action = GroundFragmentDirections.actionGroundFragmentToUserArchiveFragment(null,albumId,memberId)
//                findNavController().navigate(action)
            }
            "album" -> {
                val bottomSheetDialogFragment = BottomSheetFragment()
                val bundle = Bundle().apply {
                    putString("albumId",albumId)
                    putString("memberId",memberId)
                    putString("name",name)
                }
                bottomSheetDialogFragment.arguments = bundle
                bottomSheetDialogFragment.show(parentFragmentManager,bottomSheetDialogFragment.tag)
            }
            "delete" -> {
                // name에 index 값 전달
                val dialog = CardDeleteBottomSheetFragment(){value ->
                    list.removeAt(name.toInt())
                    adapter.notifyItemRemoved(name.toInt())
                    adapter.notifyItemRangeChanged(name.toInt(),list.size-name.toInt())
                }
                dialog.show(parentFragmentManager,dialog.tag)
            }
            else -> {
                Log.d("else","error....")
            }
        }
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
        val jsonObject = JsonObject().apply {
            addProperty("artistName",list[position].artistName)
            addProperty("songTitle", list[position].songTitle)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        musicService.addCount(accessToken!!,refreshToken!!,requestBody).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Timber.d(TAG+" success ${response.body().toString()}")
                }else{
                    Timber.d(TAG+" success ${response.errorBody()?.string().toString()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("fail",t.message.toString())
            }

        })
    }

    override fun shareCard(item: ReceivedMelodyCardModel) {

    }

    override fun onPause() {
        super.onPause()
        if(::adapter.isInitialized){
            for(i in 0 until list.size){
                adapter.stopMediaPlayer(i)
                list[i].isPlaying = false
            }
        }
    }
}