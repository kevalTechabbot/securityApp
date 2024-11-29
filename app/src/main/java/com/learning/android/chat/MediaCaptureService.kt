package com.learning.android.chat

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import linc.com.pcmdecoder.PCMDecoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class MediaCaptureService : Service() {

    companion object {
        const val CAMERA_TYPE_EXTRA = "CAMERA_TYPE_EXTRA"
        const val MEDIA_TYPE_EXTRA = "MEDIA_TYPE_EXTRA"
        const val CAMERA_BACK = "CAMERA_BACK"
        const val CAMERA_FRONT = "CAMERA_FRONT"
        const val MEDIA_TYPE_AUDIO = "AUDIO"
        const val MEDIA_TYPE_VIDEO = "VIDEO"
        const val MEDIA_TYPE_IMAGE = "IMAGE"  // Added for image capture
        const val CHANNEL_ID = "MediaCaptureServiceChannel"


        const val CAPTURE_VIDEO_BACK = "CAPTURE_VIDEO_BACK"
        const val CAPTURE_VIDEO_FRONT = "CAPTURE_VIDEO_FRONT"
        const val CAPTURE_BACK = "CAPTURE_BACK"
        const val CAPTURE_FRONT = "CAPTURE_FRONT"
        const val CAPTURE_AUDIO = "CAPTURE_AUDIO"
        const val SHOW_LOST_MESSAGE = "SHOW_LOST_MESSAGE"
        const val START_ALERT_SOUND = "START_ALERT_SOUND"
    }

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private val handler = Handler(Looper.getMainLooper()) // Handler for camera operations

    private lateinit var audioRecord: AudioRecord
    private var mediaRecorder: MediaRecorder? = null

    override fun onCreate() {
        super.onCreate()
        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Media Capture Service", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for media capture service"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val mediaType = intent.getStringExtra(MEDIA_TYPE_EXTRA) ?: MEDIA_TYPE_VIDEO
        val cameraType = intent.getStringExtra(CAMERA_TYPE_EXTRA) ?: CAMERA_BACK
//        startForegroundService()
        captureMedia(mediaType, cameraType)
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Media Capture Service")
                .setContentText("Running media capture service...")
                .setSmallIcon(android.R.drawable.ic_menu_camera) // Use your own app icon
                .build()

//        startForeground(1, notification)
    }

    private fun captureMedia(mediaType: String, cameraType: String) {
        println("mediaType-->$mediaType")
        when (mediaType) {
            MEDIA_TYPE_AUDIO -> captureAudio()
            MEDIA_TYPE_VIDEO -> captureVideo(cameraType)
            MEDIA_TYPE_IMAGE -> captureImage(cameraType)  // Handle image capture
            else -> Log.e("MediaCaptureService", "Unsupported media type")
        }
    }

    private fun captureAudio() {
        val audioBufferSize = AudioRecord.getMinBufferSize(
            44100, // Sample rate
            AudioFormat.CHANNEL_IN_MONO, // Mono channel
            AudioFormat.ENCODING_PCM_16BIT // 16-bit PCM format
        )

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission or handle appropriately
            return
        }

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, 44100, // Sample rate
            AudioFormat.CHANNEL_IN_MONO, // Mono channel
            AudioFormat.ENCODING_PCM_16BIT, // 16-bit PCM format
            audioBufferSize
        )

        val audioFile = File(
            getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "captured_audio_${System.currentTimeMillis()}.pcm" // Save as raw PCM
        )

        try {
            val outputStream = FileOutputStream(audioFile)
            val buffer = ByteArray(audioBufferSize)
            audioRecord.startRecording()
            Log.i("AudioCapture", "Recording started")

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 10000) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    outputStream.write(buffer, 0, read)
                }
            }

            audioRecord.stop()
            audioRecord.release()
            outputStream.close()
            Log.d("MediaCaptureService", "Audio capture completed: $audioFile")

            val newAudioFile = audioFile.absolutePath.replace(".pcm", ".mp3")

            PCMDecoder.encodeToMp3(
                audioFile.absolutePath,     // Input PCM file
                1,                                            // Number of channels
                96000,                                        // Bit rate
                22000,                                        // Sample rate
                newAudioFile // Output MP3 file
            )

            // Save the audio file
            saveMediaToStorage(File(newAudioFile), MEDIA_TYPE_AUDIO)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun captureVideo(cameraType: String) {
        // If there's an existing camera device or session, release it first
        cameraCaptureSession?.close()
        cameraCaptureSession = null
        cameraDevice?.close()
        cameraDevice = null

        // Set up camera manager and select front or back camera
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList

        // Find the camera id based on the camera type (front/back)
        for (id in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (cameraType == CAMERA_BACK && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                cameraId = id
                cameraCharacteristics = characteristics
                break
            } else if (cameraType == CAMERA_FRONT && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraId = id
                cameraCharacteristics = characteristics
                break
            }
        }

        // Open the camera
        if (cameraId != null) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
                        override fun onOpened(camera: CameraDevice) {
                            cameraDevice = camera
                            createVideoCaptureSession()
                        }

                        override fun onDisconnected(camera: CameraDevice) {
                            cameraDevice?.close()
                        }

                        override fun onError(camera: CameraDevice, error: Int) {
                            cameraDevice?.close()
                        }
                    }, handler)
                } else {
                    Log.e("MediaCaptureService", "Camera permission not granted")
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun createVideoCaptureSession() {
        try {
            mediaRecorder = MediaRecorder()
            val videoFile = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                "video_${System.currentTimeMillis()}.mp4"
            )

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setOutputFile(videoFile.absolutePath)
                setVideoFrameRate(30)
                setVideoSize(1280, 720)
                setAudioEncodingBitRate(3_000_000)
                setVideoEncodingBitRate(3_000_000)
                prepare()
            }

            // Initialize a dummy SurfaceTexture and a Surface
            val surfaceTexture = SurfaceTexture(10)
            surfaceTexture.setDefaultBufferSize(1280, 720)
            val previewSurface = Surface(surfaceTexture)
            val recorderSurface = mediaRecorder?.surface

            // Create capture request for continuous frame capture
            val captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            captureRequestBuilder?.addTarget(recorderSurface!!)

            cameraDevice?.createCaptureSession(
                listOf(previewSurface, recorderSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        captureRequestBuilder?.build()?.let {
                            session.setRepeatingRequest(it, null, handler)
                        }
                        startRecording(videoFile)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("VideoCaptureService", "Failed to configure session")
                    }
                },
                handler
            )
        } catch (e: CameraAccessException) {
            Log.e("VideoCaptureService", "Camera access error: ${e.message}")
        } catch (e: IOException) {
            Log.e("VideoCaptureService", "MediaRecorder preparation error: ${e.message}")
        }
    }

    private fun startRecording(videoFile: File) {
        mediaRecorder?.start()
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                mediaRecorder?.stop()
            } catch (e: RuntimeException) {
                Log.e("VideoCaptureService", "Stop error: ${e.message}")
            } finally {
                mediaRecorder?.release()
                mediaRecorder = null
            }

            // Save video to storage
            saveMediaToStorage(videoFile, MEDIA_TYPE_VIDEO)
        }, 10000) // Record for 10 seconds
    }

    /*

        private fun captureImage(cameraType: String) {
            // Set up camera manager and select front or back camera
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIdList = cameraManager.cameraIdList

            // Find the camera id based on the camera type (front/back)
            for (id in cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

                if (cameraType == CAMERA_BACK && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    cameraCharacteristics = characteristics
                    break
                } else if (cameraType == CAMERA_FRONT && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraId = id
                    cameraCharacteristics = characteristics
                    break
                }
            }

            // Open the camera
            if (cameraId != null) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
                            override fun onOpened(camera: CameraDevice) {
                                cameraDevice = camera
                                captureImageFromCamera()  // Capture image
                            }

                            override fun onDisconnected(camera: CameraDevice) {
                                cameraDevice?.close()
                            }

                            override fun onError(camera: CameraDevice, error: Int) {
                                cameraDevice?.close()
                            }
                        }, handler)
                    } else {
                        Log.e("MediaCaptureService", "Camera permission not granted")
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }

        private fun captureImageFromCamera() {
            try {
                // Set up ImageReader to capture images in JPEG format
                val imageReader = ImageReader.newInstance(
                    1920, 1080, ImageFormat.JPEG, 1 // Set size and format as per your requirement
                )

                // Create a CaptureRequest.Builder for still image capture
                val captureRequestBuilder =
                    cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder?.addTarget(imageReader.surface)

                // Set up the ImageReader listener to handle captured images
                imageReader.setOnImageAvailableListener({ reader ->
                    val image: Image = reader.acquireNextImage()
                    saveImageToStorage(image)  // Save image after capturing
                    image.close()  // Always close the image to avoid memory leaks
                }, handler)

                // Create capture session with ImageReader Surface
                cameraDevice?.createCaptureSession(
                    listOf(imageReader.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            try {
                                // Capture the image
                                session.capture(captureRequestBuilder?.build()!!, object : CameraCaptureSession.CaptureCallback() {
                                    override fun onCaptureCompleted(
                                        session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult
                                    ) {
                                        super.onCaptureCompleted(session, request, result)
                                        // Image is captured and saved in the listener
                                    }
                                }, handler)
                            } catch (e: CameraAccessException) {
                                Log.e("ImageCapture", "Error capturing image: ${e.message}")
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Log.e("ImageCapture", "Failed to configure session")
                        }
                    },
                    handler
                )
            } catch (e: CameraAccessException) {
                Log.e("ImageCapture", "Error accessing camera: ${e.message}")
            }
        }
    */

    private fun captureImage(cameraType: String) {
        // Before opening the new camera, close the previous one if it's not null
        cameraDevice?.close()  // Close previous camera device to release resources
        cameraDevice = null  // Reset the camera device variable

        // Set up camera manager and select front or back camera
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList

        // Find the camera id based on the camera type (front/back)
        for (id in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (cameraType == CAMERA_BACK && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                cameraId = id
                cameraCharacteristics = characteristics
                break
            } else if (cameraType == CAMERA_FRONT && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraId = id
                cameraCharacteristics = characteristics
                break
            }
        }

        // Open the camera after ensuring the previous one is closed
        if (cameraId != null) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
                        override fun onOpened(camera: CameraDevice) {
                            cameraDevice = camera
                            captureImageFromCamera()  // Capture image
                        }

                        override fun onDisconnected(camera: CameraDevice) {
                            cameraDevice?.close()
                        }

                        override fun onError(camera: CameraDevice, error: Int) {
                            cameraDevice?.close()
                        }
                    }, handler)
                } else {
                    Log.e("MediaCaptureService", "Camera permission not granted")
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun captureImageFromCamera() {
        try {
            // Set up ImageReader to capture images in JPEG format
            val imageReader = ImageReader.newInstance(
                1920, 1080, ImageFormat.JPEG, 1 // Set size and format as per your requirement
            )

            // Create a CaptureRequest.Builder for still image capture
            val captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder?.addTarget(imageReader.surface)

            // Set up the ImageReader listener to handle captured images
            imageReader.setOnImageAvailableListener({ reader ->
                val image: Image = reader.acquireNextImage()
                saveImageToStorage(image)  // Save image after capturing
                image.close()  // Always close the image to avoid memory leaks
            }, handler)

            // Create capture session with ImageReader Surface
            cameraDevice?.createCaptureSession(
                listOf(imageReader.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            // Capture the image
                            session.capture(
                                captureRequestBuilder?.build()!!,
                                object : CameraCaptureSession.CaptureCallback() {
                                    override fun onCaptureCompleted(
                                        session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult
                                    ) {
                                        super.onCaptureCompleted(session, request, result)
                                        // Image is captured and saved in the listener
                                    }
                                },
                                handler
                            )
                        } catch (e: CameraAccessException) {
                            Log.e("ImageCapture", "Error capturing image: ${e.message}")
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("ImageCapture", "Failed to configure session")
                    }
                },
                handler
            )
        } catch (e: CameraAccessException) {
            Log.e("ImageCapture", "Error accessing camera: ${e.message}")
        }
    }

    private fun saveImageToStorage(image: Image) {
        // Convert Image to ByteArray
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Save to file
        val imageFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image_${System.currentTimeMillis()}.jpg"
        )

        try {
            val outputStream = FileOutputStream(imageFile)
            outputStream.write(bytes)  // Write the byte data to the file
            outputStream.flush()
            outputStream.close()

            // Optionally, save the image to media store
            saveMediaToStorage(imageFile, MEDIA_TYPE_IMAGE)
        } catch (e: IOException) {
            Log.e("ImageCapture", "Error saving image: ${e.message}")
        }
    }


    private fun saveMediaToStorage(file: File, mediaType: String) {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(
                MediaStore.Images.Media.MIME_TYPE,
                if (mediaType == MEDIA_TYPE_AUDIO) "audio/*" else if (mediaType == MEDIA_TYPE_IMAGE) "image/*" else "video/*"
            )
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        try {
            val uri: Uri? =
                contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

            uri?.let {
                contentResolver.openOutputStream(it).use { outputStream ->
                    val inputStream = file.inputStream()
                    inputStream.copyTo(outputStream!!)

                    // Mark as not pending
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, values, null, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


//    private fun saveMediaToStorage(file: File, mediaType: String) {
//        // Use the content resolver to add the image to the MediaStore
//        val contentResolver: ContentResolver = applicationContext.contentResolver
//        val values = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
//            put(
//                MediaStore.Images.Media.MIME_TYPE,
//                if (mediaType == MEDIA_TYPE_IMAGE) "image/jpeg" else "image/*"
//            )
//            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//            put(MediaStore.Images.Media.IS_PENDING, 1)
//        }
//
//        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        uri?.let {
//            contentResolver.openOutputStream(it).use { outputStream ->
//                val inputStream = file.inputStream()
//                inputStream.copyTo(outputStream!!)
//
//                // Mark the image as not pending
//                values.clear()
//                values.put(MediaStore.Images.Media.IS_PENDING, 0)
//                contentResolver.update(uri, values, null, null)
//            }
//        }
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
