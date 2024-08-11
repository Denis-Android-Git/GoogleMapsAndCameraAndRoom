package com.example.myapplication.presentation

import android.content.Context
import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.load
import com.example.myapplication.data.Camera
import com.example.myapplication.data.Destinations
import com.example.myapplication.viewmodel.MyViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun CameraScreen(
    navController: NavController,
    camera: Camera,
    viewModel: MyViewModel,
    previewView: PreviewView
) {
    val photoList by viewModel.allPhotos.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        camera.startCamera()

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
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
                //isImageLoaded = true
            }
        }
        Button(
            onClick = {
                camera.takePicture()
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
