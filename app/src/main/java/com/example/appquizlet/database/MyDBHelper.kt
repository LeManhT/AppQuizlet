package com.example.appquizlet.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appquizlet.model.NotificationModel

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "db_notifi", null, 1) {
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "db_notifi"
        const val TABLE_NAME = "NOTIFICATION"
        const val COLUMN_NOTIFICATION_ID = "notificationId"
        const val COLUMN_NOTIFICATION_TITLE = "notificationTitle"
        const val COLUMN_NOTIFICATION_CONTENT = "notificationContent"
        const val COLUMN_NOTIFICATION_TIMESTAMP = "notificationTimestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $TABLE_NAME ("
                    + "$COLUMN_NOTIFICATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$COLUMN_NOTIFICATION_TITLE TEXT,"
                    + "$COLUMN_NOTIFICATION_CONTENT TEXT,"
                    + "$COLUMN_NOTIFICATION_TIMESTAMP INTEGER)"
        )
        db?.execSQL("INSERT INTO $TABLE_NAME(notificationTitle,notificationContent,notificationTimestamp) VALUES ('hh','hhh',4)")
        db?.execSQL("INSERT INTO TABLE_NAME(notificationTitle,notificationContent,notificationTimestamp) VALUES ('gg','gg',5)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

//    fun getAllNotifications(): List<NotificationModel> {
//        val notificationList = mutableListOf<NotificationModel>()
//        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NOTIFICATION_TIMESTAMP DESC"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(selectQuery, null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATION_ID))
//                val title = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TITLE))
//                val content = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_CONTENT))
//                val timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_NOTIFICATION_TIMESTAMP))
//
//                val notificationItem = NotificationModel(id, title, content, timestamp)
//                notificationList.add(notificationItem)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return notificationList
//    }
}
