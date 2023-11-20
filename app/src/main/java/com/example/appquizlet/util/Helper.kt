package com.example.appquizlet.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object Helper {
    fun formatDateSignup(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkBorn(inputDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        // Chuyển đổi chuỗi ngày thành đối tượng LocalDate
        val dateOfBirth = LocalDate.parse(inputDate, formatter)
        val currentDate = LocalDate.now()
        // Tính số tuổi
        val age = ChronoUnit.YEARS.between(dateOfBirth, currentDate).toInt()
        if (age > 10) {
            return true
        }
        return false
    }
}