package com.ajou.diggingclub.intro.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ajou.diggingclub.R

class IntroFragment4 : Fragment() {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }

                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        // 다음 step으로 ~
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.CAMERA)){
                            // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                        } else {
                            // 접근 권한 거부하였음.
                        }
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun requestPermissions(): Boolean {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true
            }

            val permissions: Array<String> = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)

            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
            return false
        } // 권한 요청

        requestPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro4, container, false)
    }

}