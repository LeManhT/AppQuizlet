package com.example.appquizlet.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.appquizlet.NoDataFragment
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserResponse
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

    fun getDataUserId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        return sharedPreferences.getString("key_userid", null).toString()
    }

    fun getDataUsername(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        return sharedPreferences.getString("key_username", null).toString()
    }

    fun replaceWithNoDataFragment(fragmentManager: FragmentManager, id: Int) {
        val noDataFragment =
            NoDataFragment()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(id, noDataFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun getAllStudySets(userData: UserResponse): List<StudySetModel> {
        val studySetsInFolders = userData.documents.folders.flatMap { it.studySets }
        val standaloneStudySets = userData.documents.studySets

        return studySetsInFolders + standaloneStudySets
    }

    fun flipCard(cardView: CardView, txtTerm: TextView, currentItem: FlashCardModel) {
        val scaleXInvisible = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f)
        val scaleXVisible = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f)

        scaleXInvisible.interpolator = AccelerateDecelerateInterpolator()
        scaleXVisible.interpolator = AccelerateDecelerateInterpolator()

        scaleXInvisible.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (currentItem.isUnMark == true) {
                    txtTerm.text = currentItem.definition
                } else {
                    txtTerm.text = currentItem.term
                }
                cardView.scaleX = 1f
                cardView.translationX = 0f
                scaleXVisible.start()
            }
        })

        scaleXInvisible.start()
    }
}