package com.example.appquizlet.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Patterns
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.appquizlet.R
import com.example.appquizlet.entity.Story
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.SearchSetModel
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserResponse
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Base64
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

    fun getAllStudySets(userData: UserResponse): List<StudySetModel> {
        val newStudySets = mutableListOf<StudySetModel>()
        val studySetsInFolders = userData.documents.folders.flatMap { it ->
            it.studySets
        }.distinctBy { it.id }.distinctBy { it.timeCreated }
//            .filterNot { folderSet ->
//                userData.documents.studySets.any { standaloneSet ->
//                    folderSet.name == standaloneSet.name && folderSet.timeCreated == standaloneSet.timeCreated
//                }
//            }
        val standaloneStudySets = userData.documents.studySets.filter { set ->
            !studySetsInFolders.any { it.id == set.id }
        }.distinctBy { it.id }
        newStudySets.addAll(studySetsInFolders)
        newStudySets.addAll(standaloneStudySets)
        return newStudySets
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

    fun toGrayscale(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val pixels = IntArray(width * height)
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]

            val alpha = Color.alpha(pixel)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

            pixels[i] = Color.argb(alpha, gray, gray, gray)
        }

        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        grayscaleBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return grayscaleBitmap
    }


    fun updateAppTheme(isDarkMode: Boolean) {
        setAppTheme(isDarkMode)
    }

    private fun setAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun hashPassword(password: String): String {
        val salt: String = generateRandomSalt()
        val hashedPassword: String = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        return hashedPassword
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }

    fun generateRandomSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun validateEmail(context: Context, email: String, inputLayout: TextInputLayout): Boolean {
        var errorMessage: String? = null
        if (email.trim().isEmpty()) {
            errorMessage = context.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage = context.getString(R.string.errEmailInvalid)
        }
        inputLayout.apply {
            isErrorEnabled = errorMessage != null
            error = errorMessage
        }
        return errorMessage == null
    }

    fun getStories(context: Context): List<Story> {
        // Open the stories.json file from the assets folder
        val inputStream = context.assets.open("stories.json")
        val reader = InputStreamReader(inputStream)
        val storyType = object : TypeToken<List<Story>>() {}.type

        return Gson().fromJson(reader, storyType)
    }

    fun calculateJaccardSimilarity(left: String, right: String): Double {
        val intersectionSet = mutableSetOf<Char>()
        val unionSet = mutableSetOf<Char>()
        var unionFilled = false
        val leftLength = left.length
        val rightLength = right.length
        if (leftLength == 0 || rightLength == 0) {
            return 0.0
        }

        for (leftIndex in left.indices) {
            unionSet.add(left[leftIndex])
            for (rightIndex in right.indices) {
                if (!unionFilled) {
                    unionSet.add(right[rightIndex])
                }
                if (left[leftIndex] == right[rightIndex]) {
                    intersectionSet.add(left[leftIndex])
                }
            }
            unionFilled = true
        }
        return intersectionSet.size.toDouble() / unionSet.size.toDouble()
    }

    fun getRecommendedStudySets(
        allStudySets: List<SearchSetModel>, // Tất cả các study set trong thư viện
        userStudySets: List<StudySetModel> // Các study set mà người dùng đã tạo
    ): List<SearchSetModel> {
        val recommendedSets = mutableListOf<SearchSetModel>()
        val threshold = 0.5

        // Lặp qua từng study set trong thư viện
        for (librarySet in allStudySets) {
            // Lặp qua từng study set mà người dùng đã tạo
            for (userSet in userStudySets) {
                val nameSimilarity =
                    calculateJaccardSimilarity(librarySet.name ?: "", userSet.name ?: "")
                val descriptionSimilarity = calculateJaccardSimilarity(
                    librarySet.description ?: "",
                    userSet.description ?: ""
                )

                // Nếu độ tương đồng của tên hoặc mô tả vượt qua ngưỡng, thêm vào danh sách gợi ý
                if (nameSimilarity > threshold || descriptionSimilarity > threshold) {
                    recommendedSets.add(librarySet)
                    break // Dừng lặp qua study set của người dùng, vì đã tìm thấy một set phù hợp
                }
            }
        }

        return recommendedSets
    }

    fun readJsonFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun parseStoriesFromJson(jsonString: String): List<Story>? {
        return try {
            val gson = Gson()
            val storyListType = object : TypeToken<List<Story>>() {}.type
            gson.fromJson<List<Story>>(jsonString, storyListType)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun maskEmail(email: String): String {
        val index = email.indexOf("@")
        return if (index > 2) {
            email.substring(0, 2) + "****" + email.substring(index)
        } else {
            "****" + email.substring(index)
        }
    }

    fun getAccessToken(context: Context): String? {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("accessToken", null)
    }

     fun saveAccessToken(context: Context, token: String) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit().putString("accessToken", token).apply()
    }

}