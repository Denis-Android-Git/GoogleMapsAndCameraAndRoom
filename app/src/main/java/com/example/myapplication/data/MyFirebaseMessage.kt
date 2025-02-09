package com.example.myapplication.data

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.myapplication.MainActivity
import com.example.myapplication.NOTIFICATION_CHANNEL_ID
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Locale

const val URI = "myapp://interesting.places"

class MyFirebaseMessage : FirebaseMessagingService() {

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val idMainScreen = Destinations.MainScreen.routes

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "$URI/$idMainScreen".toUri(),
            this,
            MainActivity::class.java
        )

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, deepLinkIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, deepLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(message.data["nickname"])
            .setContentText(message.data["message"] + ", time: " + convertTime(message.data["timestamp"]))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(MainActivity.NOTIFICATION_ID, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    private fun convertTime(timestamp: String?): String {
        timestamp ?: return ""
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(timestamp.toLong())
    }

    @SuppressLint("UnspecifiedImmutableFlag", "MissingPermission")
    fun createNotification(context: Context) {
        val idMainScreen = Destinations.MainScreen.routes

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            idMainScreen.toUri(),
            context,
            MainActivity::class.java
        )

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, deepLinkIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, deepLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText("Нажмите на маркер и на его заголовок, чтобы посмотреть информацию об объекте")

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Использование маркеров")
            .setContentText("Нажмите на маркер и на его...")
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(MainActivity.NOTIFICATION_ID, notification)
    }

}