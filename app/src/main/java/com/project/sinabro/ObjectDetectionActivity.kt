package com.project.sinabro

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.project.sinabro.databinding.ActivityObjectDetectionBinding
import com.project.sinabro.retrofit.ModelAPI
import com.project.sinabro.retrofit.RetrofitServiceForHeadCount
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit


class ObjectDetectionActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityObjectDetectionBinding

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var aLoadingDialog: ALoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityObjectDetectionBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.scanbutton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Implements VideoCapture use case, including start and stop capturing.
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        viewBinding.scanbutton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val temppath = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, temppath)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    //스캔
                    is VideoRecordEvent.Start -> {
                        viewBinding.scanbutton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    //스캔 완료
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)

                            // play loading animation
                            aLoadingDialog = ALoadingDialog(this)
                            aLoadingDialog.show()


                            //해당 비디오 재생
//                            Toast.makeText(this, "i will out", Toast.LENGTH_SHORT).show()
//                            val myIntent = Intent(this, PlayActivity::class.java)
//                            myIntent.putExtra("path", path)
//                            startActivity(myIntent)

                            //서버에 파일 전송
                            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath +
                                    File.separator + "CameraX-Video" + File.separator + mediaStoreOutputOptions.contentValues.getAsString(MediaStore.MediaColumns.DISPLAY_NAME)+".mp4"
                            val file = File(path)
                            val retrofitService = RetrofitServiceForHeadCount()
                            val modelAPI = retrofitService.retrofit.create(ModelAPI::class.java)

                            val requestFile = RequestBody.create(MediaType.parse("video/mp4"), file)
                            val video = MultipartBody.Part.createFormData("file", file.name, requestFile)

                            val startTime = System.currentTimeMillis()

                            modelAPI.uploadVideo(video).enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    val endTime = System.currentTimeMillis()
                                    val elapsedSeconds = (endTime - startTime) / 1000.0
                                    Log.d("MyApp", "Server response time: $elapsedSeconds seconds")

                                    if (response.isSuccessful) {
                                        val responseBody = response.body()?.string()
                                        if (!responseBody.isNullOrEmpty()) {
                                            try {
                                                val serverValue: Int = responseBody.toInt()
                                                intent.putExtra("serverValue", serverValue)
                                                startActivity(intent)
                                            } catch (e: NumberFormatException) {
                                                Log.e("MyApp", "Error parsing integer from response body: $responseBody")
                                            }
                                        } else {
                                            Log.e("MyApp", "Empty response body")
                                        }
                                    } else {
                                        Log.e("MyApp", "HTTP response unsuccessful: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.e("MyApp", "Network error: ${t.message}")
                                }
                            })
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        //스캔
                        viewBinding.scanbutton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            /*
            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }
            */

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider
                    .bindToLifecycle(this, cameraSelector, preview, videoCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    // (dialog_place_remove) 다이얼로그를 디자인하는 함수
//    private fun showDialog_people_count_detection_result() {
//        val builder = AlertDialog.Builder(this)
//        val peopleCnt=2
//        builder.setTitle("is title needed?")
//            .setMessage(peopleCnt.toString() + "명의 인원이 확인되었습니다.")
//            .setPositiveButton("확인",DialogInterface.OnClickListener{dialog, id-> })
//            .setNegativeButton("취소",DialogInterface.OnClickListener{dialog, id-> })
//        builder.show()
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

