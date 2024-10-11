package com.example.appquizlet

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.FragmentStoryArgs
import com.example.appquizlet.adapter.NewWordAdapter
import com.example.appquizlet.databinding.FragmentStoryBinding
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.entity.Story
import com.example.appquizlet.model.ModelLanguage
import com.example.appquizlet.viewmodel.favourite.FavouriteNewWordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.Locale

@AndroidEntryPoint
class FragmentStory : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private val args: FragmentStoryArgs by navArgs()
    private lateinit var newWordAdapter: NewWordAdapter
    private var targetLanguage: String = "vi"
    private var targetTitle: String = "Vietnamese"
    private val listLanguages = arrayListOf<ModelLanguage>()
    private var dualLanguageTranslator: Translator? = null
    private lateinit var progressDialog: ProgressDialog
    private val favouriteViewModel: FavouriteNewWordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayStoryContent(args.story)
        loadAvailableLanguage()
    }

    private fun displayStoryContent(story: Story) {
        val spannableString = SpannableString(story.content)

        story.newWords.forEach { newWord ->
            val startIndex = story.content.indexOf(newWord.word)
            if (startIndex >= 0) {
                val endIndex = startIndex + newWord.word.length

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showWordDefinitionDialog(newWord)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                    }
                }

                spannableString.setSpan(
                    clickableSpan,
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        binding.contentTextView.text = spannableString
        binding.contentTextView.movementMethod = LinkMovementMethod.getInstance()

        newWordAdapter = NewWordAdapter(object : NewWordAdapter.INewWordClick {
            override fun handleTranslateClick(newWord: NewWord, position: Int, view: View) {
                showLanguageSelectionPopup(newWord, position, view)
            }

            override fun handleAddToFavourite(newWord: NewWord) {
                favouriteViewModel.addFavouriteWord(newWord.word, newWord.meaning)
            }

            override fun showSnackbar(message: String) {
                view?.let {
                    Snackbar.make(
                        it.findViewById(R.id.btnAddToFavourite),
                        message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun deleteFavourite(newWord: NewWord) {
                favouriteViewModel.deleteFavouriteWord(newWord)
            }
        })
        newWordAdapter.updateData(story.newWords)
        binding.rvListNewWords.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = newWordAdapter
        }

    }

    private fun showWordDefinitionDialog(newWord: NewWord) {
        AlertDialog.Builder(requireContext())
            .setTitle(newWord.word)
            .setMessage(newWord.meaning)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun translateAndUpdateNewWord(newWord: NewWord, position: Int) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage("en")
            .setTargetLanguage(targetLanguage)
            .build()
        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().requireWifi().build()

        showLoading(resources.getString(R.string.please_wait_to_load_model))
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translator.translate(newWord.word)
                .addOnSuccessListener { translatedText ->
                    progressDialog.dismiss()
                    newWord.meaning = translatedText
                    newWordAdapter.notifyItemChanged(position)
                }
                .addOnFailureListener { exception ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Translation failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }.addOnFailureListener { exception ->
            progressDialog.dismiss()
            Toast.makeText(
                requireContext(),
                "Model download failed: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun loadAvailableLanguage() {
        val listLanguageCode = mutableListOf<String>()
        listLanguageCode.addAll(TranslateLanguage.getAllLanguages())
        listLanguageCode.map {
            val languageTitle = Locale(it).displayLanguage
            listLanguages.add(ModelLanguage(it, languageTitle))
        }
    }

    private fun showLanguageSelectionPopup(newWord: NewWord, position: Int, anchorView: View) {
        val weakView = WeakReference(anchorView)
        val popupMenu = weakView.get()?.let { PopupMenu(requireContext(), it) }

        popupMenu?.let {
            listLanguages.forEachIndexed { index, modelLanguage ->
                it.menu.add(Menu.NONE, index, index, modelLanguage.languageTitle)
            }

            it.setOnMenuItemClickListener { menuItem ->
                val selectedLanguage = listLanguages[menuItem.itemId]
                targetLanguage = selectedLanguage.languageCode
                targetTitle = selectedLanguage.languageTitle
                translateAndUpdateNewWord(newWord, position)
                true
            }

            it.show()
        }
    }
    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }
}