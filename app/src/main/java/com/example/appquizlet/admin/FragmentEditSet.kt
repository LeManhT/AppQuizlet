package com.example.appquizlet.admin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.adapter.CreateSetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentEditSetBinding
import com.example.appquizlet.model.CreateSetRequest
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.viewmodel.admin.AdminViewModel
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class FragmentEditSet : Fragment(), CreateSetItemAdapter.OnIconClickListener {
    private lateinit var binding: FragmentEditSetBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    private var listSet = mutableListOf<FlashCardModel>() // Declare as a class-level property
    private lateinit var adapterCreateSet: CreateSetItemAdapter // Declare adapter as a class-level property
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var speechRecognitionPosition: Int = -1
    private val args: FragmentEditSetArgs by navArgs()
    private val adminViewModel by activityViewModels<AdminViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        // Set up the toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        listSet = mutableListOf()
        adapterCreateSet = CreateSetItemAdapter(listSet)
        binding.RvCreateSets.layoutManager = LinearLayoutManager(requireContext())
        binding.RvCreateSets.adapter = adapterCreateSet
        adapterCreateSet.setOnIconClickListener(this)

        binding.addNewCard.setOnClickListener {
            val newItem = FlashCardModel()
            newItem.id = ""
            listSet.add(newItem)
            adapterCreateSet.notifyItemInserted(listSet.size - 1)

            binding.RvCreateSets.scrollToPosition(listSet.size - 1)

            binding.createSetScrollView.post {
                binding.createSetScrollView.smoothScrollTo(0, binding.RvCreateSets.bottom)
            }
        }

        binding.txtDesc.setOnClickListener {
            addSecondLayout()
        }

        binding.txtSetName.text = Editable.Factory.getInstance().newEditable(args.targetSet.name)
        binding.txtDescription.text =
            Editable.Factory.getInstance().newEditable(args.targetSet.description)

        listSet.clear()
        listSet.addAll(args.targetSet.cards)

        binding.iconTick.setOnClickListener {
            val name = binding.txtSetName.text.toString()
            val desc = binding.txtDescription.text.toString()

            if (listSet.isNotEmpty()) {
                val isEmptyItemExist =
                    listSet.any { it.term?.isEmpty() == true || it.definition?.isEmpty() == true }

                if (!isEmptyItemExist) {
                    if (name.isEmpty()) {
                        CustomToast(requireContext()).makeText(
                            requireContext(),
                            resources.getString(R.string.set_name_is_required),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else {
                        updateStudySet(
                            args.targetSet.idOwner,
                            name,
                            desc,
                            args.targetSet.id,
                            listSet
                        )
                    }
                } else {
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        "Please fill in all flashcards before updating.",
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } else {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    "Please add at least 4 flashcards.",
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }

    private fun updateStudySet(
        userId: String,
        studySetName: String,
        studySetDesc: String,
        setId: String,
        dataSet: List<FlashCardModel>
    ) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.updating_study_set))
            try {
                val body = CreateSetRequest(
                    name = studySetName,
                    description = studySetDesc,
                    allNewCards = dataSet
                )
                if (studySetName.isEmpty()) {
                    validateSetName(studySetName)
                    return@launch
                }
                val result = apiService.updateStudySet(userId, setId, body)

                if (result.isSuccessful) {
                    result.body()?.let {
                        requireContext().let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                resources.getString(R.string.update_study_set_success),
                                CustomToast.LONG,
                                CustomToast.SUCCESS
                            ).show()
                            val result = Bundle().apply {
                                putBoolean("isUpdated", true) // Bạn cũng có thể gửi StudySetModel đã chỉnh sửa nếu cần
                            }
                            setFragmentResult("editStudySetRequestKey", result) // Gửi kết quả
                            findNavController().popBackStack()
                        }
                        findNavController().popBackStack()
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        requireContext().let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                it,
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun validateSetName(name: String): Boolean {
        var errMess: String? = null
        if (name.trim().isEmpty()) {
            errMess = resources.getString(R.string.studyset_name_required)
        }
        if (errMess != null) {
            binding.layoutSetName.apply {
                isErrorEnabled = true
                error = errMess
            }
        }
        return errMess == null
    }

    override fun onIconClick(position: Int) {
        speechRecognitionPosition = position
        startSpeechRecognition(position)
    }

    override fun onDeleteClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            listSet.removeAt(position)
            adapterCreateSet.notifyItemRemoved(position)
            adapterCreateSet.notifyDataSetChanged()
        }
    }

    override fun onAddNewCard(position: Int) {
        val newItem = FlashCardModel()
        newItem.id = ""
        listSet.add(position + 1, newItem)
        adapterCreateSet.notifyItemInserted(position + 1)
        adapterCreateSet.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val matches: ArrayList<String>? = data?.getStringArrayListExtra(
            RecognizerIntent.EXTRA_RESULTS
        )


        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val position = speechRecognitionPosition

            if (position != -1) {
                val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if (!speechResults.isNullOrEmpty()) {
                    val spokenText = speechResults[0]
                    if (adapterCreateSet.getIsDefinition() == true) {
                        updateRecyclerViewItemDefinition(position, spokenText)
                    } else {
                        updateRecyclerViewItemTerm(position, spokenText)
                    }
                } else {
                    Toast.makeText(requireContext(), "No speech results found", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.e("onActivityResult", "Position is not available in the intent")
            }
            speechRecognitionPosition = -1
        }
    }


    private fun updateRecyclerViewItemTerm(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.term = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun updateRecyclerViewItemDefinition(position: Int, spokenText: String?) {
        if (position < listSet.size && spokenText != null) {
            val item = listSet[position]
            item.definition = spokenText
            adapterCreateSet.notifyItemChanged(position)
        }
    }

    private fun startSpeechRecognition(position: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        intent.putExtra(RecognizerIntent.EXTRA_ORIGIN, position)


        Log.d("startSpeechRecognition", "Intent extras: ${intent.extras}")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

    private fun btnShowDialogChooseTranslate(position: Int, text: String) {
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.1f).build()
        )
        languageIdentifier.identifyLanguage(text).addOnSuccessListener { languageCode ->
            if (languageCode == "und") {
                Toast.makeText(requireContext(), "Can't identify language.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                val itemsArray = when (languageCode) {
                    "en" -> arrayOf("Vietnamese", "Chinese")
                    "vi" -> arrayOf("English", "Chinese")
                    "zh" -> arrayOf("English", "Vietnamese")
                    else -> arrayOf("English", "Vietnamese", "Chinese")
                }
                builder.setTitle("Choose Language Format").setItems(itemsArray) { _, which ->
                    translateText(
                        position,
                        text,
                        languageCode,
                        getTranslateLanguageCode(itemsArray[which])
                    )
                }
                builder.create().show()

            }
        }.addOnFailureListener {

        }
    }

    private fun getTranslateLanguageCode(languageName: String): String {
        return when (languageName.toUpperCase()) {
            "ENGLISH" -> TranslateLanguage.ENGLISH
            "VIETNAMESE" -> TranslateLanguage.VIETNAMESE
            "CHINESE" -> TranslateLanguage.CHINESE
            else -> TranslateLanguage.ENGLISH
        }
    }

    private fun translateText(
        position: Int, text: String, sourceLanguage: String, targetLanguage: String
    ) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val options = TranslatorOptions.Builder().setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage).build()
            val dualLanguageTranslator = Translation.getClient(options)
            var conditions = DownloadConditions.Builder().requireWifi().build()
            dualLanguageTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener {
                dualLanguageTranslator.translate(text).addOnSuccessListener { translatedText ->
                    if (adapterCreateSet.getIsDefinitionTranslate() == true) {
                        listSet[position].definition = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    } else {
                        listSet[position].term = translatedText
                        adapterCreateSet.notifyDataSetChanged()
                    }
                }.addOnFailureListener { exception ->
                    Log.i("detectL3", "translatedText: $exception")
                }
            }.addOnFailureListener { exception ->
                Log.i("exception", "exception: $exception")
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onTranslateIconClick(position: Int, currentText: String) {
        if (adapterCreateSet.getIsDefinitionTranslate() == true) {
            adapterCreateSet.notifyDataSetChanged()
            btnShowDialogChooseTranslate(position, currentText)
        } else {
            Log.d("termValue", currentText)
            adapterCreateSet.notifyDataSetChanged()
            btnShowDialogChooseTranslate(position, currentText)
        }
    }

    private fun addSecondLayout() {
        binding.layoutDesc.visibility = View.VISIBLE
        val params = binding.createSetScrollView.layoutParams as RelativeLayout.LayoutParams
        params.topMargin =
            resources.getDimensionPixelSize(R.dimen.h_200) + 200
        binding.createSetScrollView.layoutParams = params
        binding.txtHideDesc.visibility = View.VISIBLE
        binding.txtDesc.visibility = View.GONE
        binding.txtHideDesc.setOnClickListener {
            hideSecondLayout()
        }
    }

    private fun hideSecondLayout() {
        binding.layoutDesc.visibility = View.GONE
        val params = binding.createSetScrollView.layoutParams as RelativeLayout.LayoutParams
        params.topMargin = resources.getDimensionPixelSize(R.dimen.h_200)
        binding.createSetScrollView.layoutParams = params
        binding.txtHideDesc.visibility = View.GONE
        binding.txtDesc.visibility = View.VISIBLE
    }


}