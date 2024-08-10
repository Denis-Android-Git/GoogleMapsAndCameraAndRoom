package com.example.myapplication

import android.Manifest
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.rememberAsyncImagePainter
import coil.load
import com.example.myapplication.data.MyFirebaseMessage
import com.example.myapplication.data.URI
import com.example.myapplication.entity.Photo
import com.example.myapplication.presentation.Destinations
import com.example.myapplication.presentation.MapScreen
import com.example.myapplication.presentation.MyViewModel
import com.example.myapplication.presentation.TextContent
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

private const val FILE_NAME = "dd-M-yyyy"

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

    private val name = SimpleDateFormat(FILE_NAME, Locale.ROOT).format(System.currentTimeMillis())
    private lateinit var executor: Executor
    private var imageCapture: ImageCapture? = null
    private lateinit var previewView: PreviewView
    private var isImageLoaded by mutableStateOf(false)
    private var photoList by mutableStateOf(emptyList<Photo>())
    private var deleteList by mutableStateOf(emptyList<Photo>())

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions are not granted", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executor = ContextCompat.getMainExecutor(this)
        previewView = PreviewView(this)
        checkPermissions()

        setContent {
            MyApplicationTheme {
                Navigation()
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            Log.d("Registration token", it.result)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deppLink = intent.data.toString()
        Log.d("deppLink", "${intent.data.toString()}")
        viewModel.setRoute(deppLink)
    }

    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        val routeLink = viewModel.routeLink.collectAsState()

        routeLink.value?.let {
            navController.navigate(it)
            viewModel.setRoute(null)
        }

        NavHost(
            navController = navController, startDestination = Destinations.MainScreen.routes
        ) {
            composable(
                route = Destinations.MainScreen.routes,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$URI/${Destinations.MainScreen.routes}"
                })
            ) {
                MainScreen(navController)
            }
            composable(
                route = Destinations.CameraScreen.routes,
            ) {
                CameraScreen(navController)
            }
            composable(
                route = Destinations.DetailScreen.routes + "/{name}/{date}",
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                        defaultValue = "No photo"
                        nullable = true
                    },
                    navArgument("date") {
                        type = NavType.StringType
                        defaultValue = "No date"
                        nullable = true
                    }
                )
            ) { entry ->
                DetailScreen(
                    name = entry.arguments?.getString("name"),
                    date = entry.arguments?.getString("date"),
                    navController = navController
                )
            }
            composable(
                route = Destinations.MapScreen.routes,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "$URI/${Destinations.MapScreen.routes}"
                })
            ) {
                MapScreen()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Photo(
        photo: Photo,
        navController: NavController,
    ) {
        val painter = rememberAsyncImagePainter(model = photo.uri)

        Box(
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .combinedClickable(
                    onClick = {
                        val args = URLEncoder.encode(photo.uri, StandardCharsets.UTF_8.toString())
                        val date = photo.date
                        navController.navigate(Destinations.DetailScreen.withArgs(args, date))
                        deleteList = emptyList()
                    },
                    onLongClick = {
                        deleteList = if (deleteList.contains(photo)) {
                            deleteList - photo
                        } else {
                            deleteList + photo
                        }
                    }
                )
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
            if (deleteList.contains(photo)) {
                Checkbox(
                    checked = true, onCheckedChange = null
                )
            }
        }
    }

    @Preview(name = "Light Mode")
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        name = "Dark Mode"
    )
    @Composable
    fun SimpleComposablePreview() {
        MainScreen(navController = NavController(this))
    }

    @Composable
    fun MainScreen(navController: NavController) {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#1F585A")))
        ) {
            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.allPhotos.collect {
                            photoList = it
                        }
                    }
                }
            }
            if (photoList.isEmpty()) {
                TextContent()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), contentPadding = PaddingValues(2.dp)
                ) {
                    items(photoList) { photo ->
                        Column {
                            Photo(
                                photo = photo,
                                navController = navController,
                            )
                            HorizontalDivider(thickness = 5.dp, color = Color.Transparent)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    navController.navigate(Destinations.CameraScreen.routes)
                    startCamera()
                    deleteList = emptyList()
//                    FirebaseCrashlytics.getInstance().log("Additional info")
//                    try {
//                        throw Exception("My test")
//                    } catch (e: Exception) {
//                        FirebaseCrashlytics.getInstance().recordException(e)
//                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    Color(android.graphics.Color.parseColor("#101B20"))
                )
            ) {
                Text(
                    text = "Add a picture", color = Color.White
                )
            }

            var showDeleteConfirmation by remember { mutableStateOf(false) }

            if (showDeleteConfirmation && deleteList.isNotEmpty()) {
                AlertDialog(onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Confirmation") },
                    text = { Text("Delete all or selected?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteSelectedPhotos(deleteList)
                                showDeleteConfirmation = false
                                deleteList = emptyList()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Delete selected", color = Color.DarkGray)
                        }
                        Button(
                            onClick = {
                                viewModel.onDeleteClick()
                                showDeleteConfirmation = false
                                deleteList = emptyList()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Delete all", color = Color.DarkGray)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDeleteConfirmation = false
                                deleteList = emptyList()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Cancel", color = Color.DarkGray)
                        }
                    })
            }
            if (deleteList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = "Delete all photos",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .clickable { showDeleteConfirmation = true })
                }
            } else {
                Button(
                    onClick = {
                        navController.navigate(Destinations.MapScreen.routes)
                        MyFirebaseMessage().createNotification(context)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(android.graphics.Color.parseColor("#101B20"))
                    )
                ) {
                    Text(text = "Open Maps", color = Color.White)
                }
            }
        }
    }

    @Composable
    fun CameraScreen(navController: NavController) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            startCamera()

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    previewView = PreviewView(context)
                    previewView
                }
            )
            AndroidView(
                factory = { context ->
                    val imageView = ImageView(context)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView
                },
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable {
                        val encode = URLEncoder.encode(
                            photoList.lastOrNull()?.uri, StandardCharsets.UTF_8.toString()
                        )
                        val date = photoList.lastOrNull()?.date
                        navController.navigate(Destinations.DetailScreen.withArgs(encode, date))
                    }
            ) { view ->
                photoList.lastOrNull()?.let {
                    view.load(it.uri)
                    isImageLoaded = true
                }
            }
            Button(
                onClick = {
                    takePicture()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    Color(android.graphics.Color.parseColor("#101B20"))
                )
            ) {
                Text(text = "take a picture", color = Color.White)
            }
        }
    }

    @Composable
    fun DetailScreen(name: String?, date: String?, navController: NavController) {

        val painter = rememberAsyncImagePainter(model = name)
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var showImage by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            (if (!showImage) null else painter)?.let {
                Image(
                    painter = it, contentDescription = null, modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = date ?: "",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .offset(y = (-60).dp)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable { showDeleteConfirmation = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete all photos",
                    tint = Color.Black,
                    modifier = Modifier.padding(12.dp)
                )
            }
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Confirmation") },
                    text = { Text("Are you sure?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                photoList.lastOrNull()?.let {
                                    viewModel.deleteOnePhoto(it)
                                }
                                showDeleteConfirmation = false
                                showImage = false
                                navController.navigate(Destinations.MainScreen.routes)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Delete", color = Color.DarkGray)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDeleteConfirmation = false
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Cancel", color = Color.DarkGray)
                        }
                    }
                )
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1000

        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!isAllGranted) launcher.launch(REQUEST_PERMISSIONS)
    }

    private fun startCamera() {
        val camera = ProcessCameraProvider.getInstance(this)
        camera.addListener({
            val cameraProvider = camera.get()
            val previewBuild = androidx.camera.core.Preview.Builder().build()
            previewBuild.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, previewBuild, imageCapture
            )
        }, executor)
    }

    private fun takePicture() {
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
                        this@MainActivity,
                        "Photo not saved: ${exception.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    exception.printStackTrace()
                }
            })
    }
}
