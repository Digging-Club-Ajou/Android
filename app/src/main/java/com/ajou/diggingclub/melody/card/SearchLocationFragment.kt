package com.ajou.diggingclub.melody.card

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.SearchDataViewModel
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentSearchLocationBinding
import com.ajou.diggingclub.melody.card.adapter.LocationListRVAdapter
import com.ajou.diggingclub.melody.models.LocationModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.LocationService
import com.ajou.diggingclub.network.models.LocationResponse
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.hideKeyboard
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SearchLocationFragment : Fragment() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private lateinit var resolutionResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    var x : String = "127.046250"
    var y : String = "37.282944"

    private var _binding: FragmentSearchLocationBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private var job: Job? = null
    private lateinit var viewModel : SearchDataViewModel
    private lateinit var adapter : LocationListRVAdapter
    private val TAG = SearchLocationFragment::class.java.simpleName

    private val locationService = RetrofitInstance.getInstance().create(LocationService::class.java)
    val args : SearchLocationFragmentArgs by navArgs()

    var page = 1
    var isLoading = false
    var isEnd = false
    val dataStore = UserDataStore()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            Log.d("latitude",p0.lastLocation?.latitude.toString())
            binding.curLocBtn.isActivated = true
            onLocationChanged(p0.lastLocation!!)
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }

    inner class AdapterToFragment {
        fun getSelectedItem(data : String) {
            val action =
                SearchLocationFragmentDirections.actionSearchLocationFragmentToShareCardFragment(
                    args.uri,
                    args.color,
                    data,
                    args.music
                )
            findNavController().navigate(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        resolutionResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                startLocationUpdates()
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
        _binding = FragmentSearchLocationBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.root.setOnClickListener{
            hideKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var accessToken : String? = null
        var refreshToken : String? = null
        var keyword = ""

        viewModel = ViewModelProvider(requireActivity())[SearchDataViewModel::class.java]
        viewModel.setEmptyList()

        checkPermissionForLocation(mContext!!)

        val link = AdapterToFragment()
        adapter = LocationListRVAdapter(mContext!!, emptyList(),link)
        binding.listRV.adapter = adapter
        binding.listRV.layoutManager = LinearLayoutManager(mContext)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.skip.setOnSingleClickListener {
            val action =
                SearchLocationFragmentDirections.actionSearchLocationFragmentToShareCardFragment(
                    args.uri,
                    args.color,
                    null,
                    args.music
                )
            findNavController().navigate(action)
        }

        binding.removeBtn.setOnClickListener {
            binding.editText.setText("")
            viewModel.setEmptyList()
            adapter.updateList(viewModel.locationList.value!!)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.listRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!binding.listRV.canScrollVertically(1)&&!isEnd&&!isLoading){
                    getData(accessToken!!, refreshToken!!, keyword, x, y)
                    isLoading = true
                }
            }
        })

        binding.editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                job?.cancel()
                if (str != null) {
                    if(str.isNotEmpty()) {
                        viewModel.setEmptyList()
                        page = 1
                        binding.removeBtn.visibility = View.VISIBLE
                        binding.curLocBtn.visibility = View.GONE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            delay(500)
                            keyword = str.toString()
                            getData(accessToken!!, refreshToken!!, keyword, x, y)
                        }
                    }else{
                        binding.removeBtn.visibility = View.GONE
                    }
                }
            }
        })
//
//        binding.curLocBtn.setOnSingleClickListener {
//            checkPermissionForLocation(mContext!!)
//        }
    }
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getSettingsClient(mContext!!).checkLocationSettings(builder.build()).run {
            addOnSuccessListener { response ->
                mFusedLocationProviderClient?.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
            }
            addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        resolutionResultLauncher!!.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.d("gps", sendEx.message.toString())
                    }
                }
            }
        }
    }
    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        x = mLastLocation.longitude.toString()
        y = mLastLocation.latitude.toString()
        Log.d("location",x)
        Log.d("location",y)
        val address = Geocoder(mContext!!, Locale.KOREAN).getFromLocation(mLastLocation.latitude,mLastLocation.longitude,1)?.first()?.getAddressLine(0)
        Log.d("address",address.toString()) // 도로명 주소
        mFusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private fun getData(accessToken : String, refreshToken : String, keyword : String, x : String, y : String) {
        Log.d(TAG,"page : $page")
        locationService.searchLocation(accessToken,refreshToken,keyword,x,y,page.toString()).enqueue(object :
            Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                if(response.isSuccessful){
                    if(response.headers()["AccessToken"] != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                        }
                    }
                    val data : List<LocationModel> = response.body()!!.locationListResult
                    if(data.isEmpty()){
                        Log.d(TAG,"isEnd is true")
                        isEnd = true
                    }else{
                        Log.d(TAG,"isEnd is false")
                        viewModel.addLocationList(data)
                        adapter.updateList(viewModel.locationList.value!!)
                        page++
                        isLoading = false

                    }
                }else{
                    Log.d("error",response.errorBody()?.string().toString())
                    isEnd = true
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.d("fail",t.message.toString())
                isEnd = true
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setEmptyList()
    }


    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
            return true
        } else {
            // 권한이 없으므로 권한 요청 알림 보내기
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            return false
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            var allPermissionsGranted = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                startLocationUpdates()
                Log.d("location permission granted","permission granted")
            } else {
                Toast.makeText(mContext, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}