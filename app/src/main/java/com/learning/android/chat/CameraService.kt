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
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.nio.ByteBuffer

class CameraService : Service() {

    companion object {
        const val CAMERA_TYPE_EXTRA = "CAMERA_TYPE_EXTRA"
        const val CAMERA_BACK = "CAMERA_BACK"
        const val CAMERA_FRONT = "CAMERA_FRONT"
    }

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val cameraType = intent.getStringExtra(CAMERA_TYPE_EXTRA) ?: CAMERA_BACK
//        startForegroundService()
        captureImage(cameraType)
        return START_STICKY
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CameraServiceChannel"
            val channel = NotificationChannel(
                channelId, "Camera Service Channel", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for camera service notifications"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "CameraServiceChannel")
            .setContentTitle("Camera Service")
            .setContentText("Running camera service...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

//        startForeground(1, notification)
    }

    private fun captureImage(cameraType: String) {
        try {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIds = cameraManager.cameraIdList

            cameraDevice?.close()
            cameraCaptureSession?.close()

            for (id in cameraIds) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                if ((cameraType == CAMERA_BACK && facing == CameraCharacteristics.LENS_FACING_BACK) ||
                    (cameraType == CAMERA_FRONT && facing == CameraCharacteristics.LENS_FACING_FRONT)) {
                    cameraId = id
                    break
                }
            }

            if (cameraId != null) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("CameraService", "Camera permission not granted.")
                    return
                }
                cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        createCameraCaptureSession()
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        cameraDevice?.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        Log.e("CameraService", "Error opening camera: $error")
                        cameraDevice?.close()
                    }
                }, handler)
            } else {
                Log.e("CameraService", "No suitable camera found")
            }

        } catch (e: CameraAccessException) {
            Log.e("CameraService", "Camera access exception: ${e.message}")
        }
    }

    private fun createCameraCaptureSession() {
        try {
            val imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1)
            val surface = imageReader.surface

            val captureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder?.addTarget(surface)

            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSession = session
                        captureImageFromCamera(captureRequestBuilder, imageReader)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("CameraService", "Failed to configure capture session")
                    }
                },
                handler
            )

            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    processCapturedImage(image)
                    image.close()
                } else {
                    Log.w("CameraService", "No image available")
                }
            }, handler)

        } catch (e: CameraAccessException) {
            Log.e("CameraService", "Camera access exception: ${e.message}")
        }
    }

    private fun captureImageFromCamera(
        captureRequestBuilder: CaptureRequest.Builder?,
        imageReader: ImageReader
    ) {
        try {
            captureRequestBuilder?.set(
                CaptureRequest.CONTROL_MODE,
                CameraMetadata.CONTROL_MODE_AUTO
            )
            cameraCaptureSession?.capture(
                captureRequestBuilder?.build()!!,
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        Log.i("CameraService", "Image capture completed")
                    }
                },
                handler
            )
        } catch (e: CameraAccessException) {
            Log.e("CameraService", "Camera access exception: ${e.message}")
        }
    }

    private fun processCapturedImage(image: Image) {
        val buffer: ByteBuffer = image.planes[0].buffer
        val byteArray = ByteArray(buffer.remaining())
        buffer.get(byteArray)
        saveImage(byteArray)
    }

    private fun saveImage(imageBytes: ByteArray) {
        try {
            val fileName = "captured_image_${System.currentTimeMillis()}.jpg"
            val contentResolver: ContentResolver = applicationContext.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val imageUri: Uri? =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            if (imageUri != null) {
                contentResolver.openOutputStream(imageUri).use { outputStream ->
                    outputStream?.write(imageBytes)
                    outputStream?.flush()
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageUri, values, null, null)
                Log.i("CameraService", "Image saved successfully")
            } else {
                Log.e("CameraService", "Failed to save image")
            }
        } catch (e: Exception) {
            Log.e("CameraService", "Failed to save image: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
