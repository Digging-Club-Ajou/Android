package com.ajou.diggingclub.melody

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.FragmentCameraBinding
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    val args: CameraFragmentArgs by navArgs()
    private val TAG = CameraFragment::class.java.simpleName

    private var fragmentContainer: FragmentContainerView? = null
    private var layoutParams: ViewGroup.MarginLayoutParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        requireActivity().findViewById<LinearLayout>(R.id.bottomTab).visibility = View.GONE

        fragmentContainer = requireActivity().findViewById(R.id.fragmentContainerView)

        // layoutParams 초기화
        layoutParams = fragmentContainer?.layoutParams as? ViewGroup.MarginLayoutParams

        fragmentContainer?.let { container ->
            Log.d(TAG,"before margin ${layoutParams?.marginStart} ${layoutParams?.marginEnd} ${layoutParams?.topMargin} ${layoutParams?.bottomMargin}")
            layoutParams?.setMargins(0, 0, 0, 0)
            Log.d(TAG,"after margin ${layoutParams?.marginStart} ${layoutParams?.marginEnd} ${layoutParams?.topMargin} ${layoutParams?.bottomMargin}")
            container.requestLayout()
            }

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.imageCaptureButton.setOnSingleClickListener { takePhoto() }
        binding.cancelBtn.setOnSingleClickListener {
            findNavController().popBackStack()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(mContext!!.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mContext!!),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    if(args.type == "album"){
                        val action = CameraFragmentDirections.actionCameraFragmentToMakeAlbumFragment2(output.savedUri.toString())
                        findNavController().navigate(action)
                    }else if(args.type == "card"){
                        val action =
                            CameraFragmentDirections.actionCameraFragmentToMakeCardFragment2(
                                output.savedUri.toString(),
                                args.music
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mContext!!)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            } catch (exc: Exception) {
                Log.e(TAG, "Camera init error", exc)
            }
        }, ContextCompat.getMainExecutor(mContext!!))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            mContext!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        requireActivity().findViewById<LinearLayout>(R.id.bottomTab).visibility = View.VISIBLE

        val density = resources.displayMetrics.density
        val sideMargin = (24*density).toInt()
        val topMargin = (26*density).toInt()
        val bottomMargin = (66*density).toInt()

        fragmentContainer?.let { container ->
            layoutParams?.setMargins(sideMargin, topMargin, sideMargin, bottomMargin)
            container.requestLayout()
        }
        _binding = null
    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    mContext,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 0
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}