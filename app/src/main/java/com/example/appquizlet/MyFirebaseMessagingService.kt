package com.example.appquizlet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.appquizlet.ui.activities.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channel_id = "Noti_ID"
const val channel_name = "com.example.appquizlet.notification"
const val NOTIFICATION_ID = 10

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews =
            RemoteViews("com.example.appquizlet", R.layout.layout_notification)
        remoteViews.setTextViewText(R.id.txtTitleNotification, title)
        remoteViews.setTextViewText(R.id.txtContentNotification, message)
        remoteViews.setImageViewResource(R.id.imgNotification, R.drawable.ac204)

        return remoteViews
    }


    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
//        channel id, channel name
        var builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.bell)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channel_id,
                channel_name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channel_id,
                channel_name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRemindNotification() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
//        channel id, channel name
        var builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.bell)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentTitle(resources.getString(R.string.daily_reminder))
            .setContentText(
                resources.getString(R.string.remind_notification)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channel_id,
                channel_name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMsgService", "Message received from: ${remoteMessage.data["type"]}")

        if (remoteMessage.notification != null) {
//            generateNotification(
//                remoteMessage.notification!!.title.toString(),
//                remoteMessage.notification!!.body.toString()
//            )
            remoteMessage.notification?.let {
                Log.d("FCM", "Message Notification Title: ${it.title}")
                Log.d("FCM", "Message Notification Body: ${it.body}")
                // Display notification to user using NotificationManager
            }
            createRemindNotification()
        }
    }


}