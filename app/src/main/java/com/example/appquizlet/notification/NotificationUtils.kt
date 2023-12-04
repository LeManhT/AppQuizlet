package com.example.appquizlet.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.appquizlet.BroadcastReceiver.AlarmReceiver
import com.example.appquizlet.MainActivity_Logged_In
import com.example.appquizlet.R
import com.example.appquizlet.database.MyDBHelper
import com.example.appquizlet.model.NotificationModel
import java.util.Calendar

fun MyDBHelper.addNotification(notificationItem: NotificationModel) {
    val db = this.writableDatabase
    val values = ContentValues()
    values.put("notificationTitle", notificationItem.notificationTitle)
    values.put("notificationContent", notificationItem.notificationContent)
    values.put("notificationTimestamp", notificationItem.notificationTimestamp)

    // Thêm thông báo vào cơ sở dữ liệu
    db.insert("NOTIFICATION", null, values)
    Log.d("gg","add success")
    db.close()
}

class NotificationUtils {
    companion object {
        private const val CHANNEL_ID = "DailyReminder"
        private const val NOTIFICATION_ID = 1

        fun scheduleNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 11) // Thay đổi giờ theo mong muốn
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)

            // Nếu thời gian đã qua, đặt lịch cho ngày tiếp theo
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Đặt báo thức hằng ngày
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            createNotificationChannel(context)
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Daily Reminder Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context) {
            val intent = Intent(context, MainActivity_Logged_In::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle("Daily Reminder")
                .setContentText(
                    "Nhắc nhở bạn về điều gì đó quan trọng." +
                            "Vào app học thôi nào"
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, builder.build())

            // Lưu thông báo vào cơ sở dữ liệu
            val notificationDb = MyDBHelper(context)
            val timestamp = System.currentTimeMillis()
            val notificationItem = NotificationModel(
                0,
                "Daily Reminder",
                "Nhắc nhở bạn về điều gì đó quan trọng. Vào app học thôi nào",
                timestamp
            )
            notificationDb.addNotification(notificationItem)
        }
    }


}