package com.ajou.diggingclub.melody.card

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentSearchLocationBinding
import com.ajou.diggingclub.melody.card.adapter.LocationListRVAdapter
import com.ajou.diggingclub.melody.models.LocationModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.LocationApi
import com.ajou.diggingclub.network.models.LocationResponse
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchLocationFragment : Fragment() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    var x : String = "127.046250"
    var y : String = "37.282944"

    private var _binding: FragmentSearchLocationBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private var job: Job? = null

    private val client = RetrofitInstance.getInstance().create(LocationApi::class.java)
    val args : SearchLocationFragmentArgs by navArgs()

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val locationClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = locationClient.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            // GPS가 꺼져있을 경우
            if (exception is ResolvableApiException) {
                Log.d("gps", "OnFailure")
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("gps", sendEx.message.toString())
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        if(checkPermissionForLocation(mContext!!)) startLocationUpdates()

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
        }

        binding.editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                job?.cancel()
                if (str != null) {
                    if(str.isNotEmpty()) {
                        binding.removeBtn.visibility = View.VISIBLE
                        binding.curLocBtn.visibility = View.GONE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            delay(1000)
                            Log.d("location x y",x+"   "+y)
                            client.searchLocation(accessToken!!,refreshToken!!,str.toString(),x,y).enqueue(object :
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
                                        val list : List<LocationModel> = response.body()!!.locationListResult
                                        Log.d("success",list.toString())
                                        val link = AdapterToFragment()
                                        val locationListRVAdapter = LocationListRVAdapter(mContext!!,list,link)
                                        binding.listRV.adapter = locationListRVAdapter
                                        binding.listRV.layoutManager = LinearLayoutManager(mContext)
                                    }else {
                                        Log.d("response not successful",response.errorBody()?.string().toString())
                                    }
                                }

                                override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                                    Log.d("fail",t.message.toString())
                                }
                            })
                        }
                    }else{
                        binding.removeBtn.visibility = View.GONE
                    }
                }
            }

        })
    }
    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationProviderClient!!.lastLocation.addOnSuccessListener {location: Location? ->
            location?.let {
                onLocationChanged(it)
                binding.curLocBtn.setBackgroundColor(resources.getColor(R.color.primaryColor))
                binding.curLocBtn.setTextColor(resources.getColor(R.color.textColor))
                binding.linearLayout.visibility = View.GONE
                binding.scrollview.visibility = View.VISIBLE
            } ?: run {
                Log.d("check","Location not available")
            }
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        x = mLastLocation.latitude.toString()
        y = mLastLocation.longitude.toString()
        Log.d("location",x)
        Log.d("location",y)
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
            } else {
                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(mContext, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}