package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.data.Camera
import com.example.myapplication.presentation.BottomNaviScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val FILE_NAME = "dd-M-yyyy"

class MainActivity : FragmentActivity() {

    private val viewModel: MyViewModel by viewModel()

    private lateinit var previewView: PreviewView

    private val scope = CoroutineScope(Dispatchers.IO)

    private val camera: Camera by inject { parametersOf(this, previewView) }

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
        enableEdgeToEdge()
        checkPermissions()

        setContent {
            MyApplicationTheme {
                BottomNaviScreen(
                    viewModel = viewModel,
                )
            }
        }

//        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            Log.d("Registration token", it.result)
//        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deppLink = intent.data.toString()
        scope.launch {
            viewModel.setRoute(deppLink)
        }
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