package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.Camera
import com.example.myapplication.entity.Photo
import com.example.myapplication.presentation.Navigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.MyViewModel
import com.google.firebase.messaging.FirebaseMessaging

const val FILE_NAME = "dd-M-yyyy"

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {

    private val viewModel: MyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val photoDao = (application as App).db.photoDao()
                return MyViewModel(photoDao) as T
            }
        }
    }
    private lateinit var camera: Camera
    private lateinit var previewView: PreviewView
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                camera.startCamera()
            } else {
                Toast.makeText(this, "Permissions are not granted", Toast.LENGTH_LONG).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewView = PreviewView(this)

        camera = Camera(
            contentResolver = contentResolver,
            context = this,
            lifecycleOwner = this,
            viewModel = viewModel,
            previewView = previewView
        )

        checkPermissions()

        setContent {
            MyApplicationTheme {
                val deleteList = remember {
                    mutableStateListOf<Photo>()
                }
                Navigation(
                    camera = camera,
                    deleteList = deleteList,
                    viewModel = viewModel,
                    previewView = previewView
                )
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("Registration token", it.result)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deppLink = intent.data.toString()
        Log.d("deppLink", intent.data.toString())
        viewModel.setRoute(deppLink)
    }

    companion object {
        const val NOTIFICATION_ID = 1000

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.POST_NOTIFICATIONS)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!isAllGranted) launcher.launch(REQUEST_PERMISSIONS)
    }
}