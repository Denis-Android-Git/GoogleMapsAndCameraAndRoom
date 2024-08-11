package com.example.myapplication.data

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.myapplication.FILE_NAME
import com.example.myapplication.viewmodel.MyViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class Camera(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val viewModel: MyViewModel,
    private val contentResolver: ContentResolver,
    private val previewView: PreviewView
) {
    private var imageCapture: ImageCapture? = null
    private val executor = ContextCompat.getMainExecutor(context)
    private val camera = ProcessCameraProvider.getInstance(context)
    private val name = SimpleDateFormat(FILE_NAME, Locale.ROOT).format(System.currentTimeMillis())


    fun startCamera() {
        camera.addListener({
            val cameraProvider = camera.get()
            val previewBuild = androidx.camera.core.Preview.Builder().build()
            previewBuild.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, previewBuild, imageCapture
            )
        }, executor)
    }

    fun takePicture() {
        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outPutOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()
        imageCapture.takePicture(
            outPutOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: return
                    viewModel.onPhotoMake(savedUri.toString(), name)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        context,
                        "Photo not saved: ${exception.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    exception.printStackTrace()
                }
            })
    }
}