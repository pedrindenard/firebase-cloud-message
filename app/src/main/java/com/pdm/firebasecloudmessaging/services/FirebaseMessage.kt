package com.pdm.firebasecloudmessaging.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pdm.firebasecloudmessaging.R
import com.pdm.firebasecloudmessaging.extension.CHANNEL_ID_2
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class FirebaseMessage : FirebaseMessagingService() {

    private var imageBitmap: Bitmap? = null
    private var imageUrl: String? = null

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            createNotificationChannel()

            //Get Image from Firebase
            if (message.notification!!.imageUrl != null) {
                imageUrl = message.notification!!.imageUrl.toString()
                imageBitmap = imageUrl?.getBitmapFromUrl()
            }

            showNotification(
                title = message.notification?.title ?: "",
                description = message.notification?.body ?: ""
            )
        }

        //Custom data object
        if (message.data.isNotEmpty()) {
            val myData: Map<String, String> = message.data
            Log.d("MyData", myData["key1"] ?: "")
            Log.d("MyData", myData["key2"] ?: "")
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun showNotification(title: String, description: String) {
        val builder = NotificationCompat.Builder(
            this@FirebaseMessage,
            CHANNEL_ID_2
        )
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(imageBitmap)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(imageBitmap)
                .bigLargeIcon(null))
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this@FirebaseMessage, R.color.purple_200))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat = NotificationManagerCompat.from(this@FirebaseMessage)
        notificationManagerCompat.notify(2, builder.build())
    }

    //On API 26+ devices do not create automatically notification channels
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_2,
                "Firebase Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = this@FirebaseMessage.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(serviceChannel)
        }
    }
}

private fun String.getBitmapFromUrl(): Bitmap? {
    return try {
        val url = URL(this)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val inputStream = connection.inputStream
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}


