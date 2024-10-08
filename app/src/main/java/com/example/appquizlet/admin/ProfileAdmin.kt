package com.example.appquizlet.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentProfileAdminBinding
import com.example.appquizlet.viewmodel.admin.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProfileAdmin : Fragment() {
    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminViewModel.loginAdminResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.txtProfileEmail.text = it.loginName
                binding.txtProfileName.text = resources.getString(R.string.hello, it.userName)
            }.onFailure {
                Timber.e(it, "Get user failed")
            }
        }

        binding.layoutTerm.setOnClickListener {
            val termsOfServiceUrl = "https://quizlet.com/tos"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
            startActivity(intent)
        }

        binding.layoutPolicy.setOnClickListener {
            val privacyPolicyUrl = "https://quizlet.com/privacy"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
            startActivity(intent)
        }

        binding.layoutFaq.setOnClickListener {
            val faqUrl = "https://quizlet.com/faq"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(faqUrl))
            startActivity(intent)

        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.title))
                .setMessage(resources.getString(R.string.are_u_sure_logout))
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
//                    val intent = Intent(requireContext(), AuthActivity::class.java)
//                    startActivity(intent)
//                    requireActivity().finish()
                }
                .show()

        }

        binding.languageSelector.setOnClickListener {
            showLanguageSelectionDialog()
        }

    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            resources.getString(R.string.vietnamese),
            resources.getString(R.string.english),
            resources.getString(R.string.chinese)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language)
            .setItems(languages) { dialog, which ->
                when (which) {
                    0 -> {
                        // Handle Vietnamese selection
                    }

                    1 -> {
                        // Handle English selection
                    }

                    2 -> {
                        // Handle Chinese selection
                    }
                }
            }
            .show()
    }

}