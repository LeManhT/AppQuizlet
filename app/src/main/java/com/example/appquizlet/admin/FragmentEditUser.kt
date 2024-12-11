package com.example.appquizlet.admin

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentEditUserBinding
import com.example.appquizlet.model.UpdateUserResponse
import com.example.appquizlet.viewmodel.document.DocumentViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

@AndroidEntryPoint
class FragmentEditUser : DialogFragment(), View.OnFocusChangeListener {
    private lateinit var binding: FragmentEditUserBinding
    private val documentViewModel by viewModels<DocumentViewModel>()
    private val args: FragmentEditUserArgs by navArgs()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditUserBinding.inflate(layoutInflater, container, false)

        binding.edtEditDOBLayout.onFocusChangeListener = this
        binding.edtEditEmailLayout.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtDOB.onFocusChangeListener = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.edtEmail.text = Editable.Factory.getInstance().newEditable(args.user.email)
        binding.edtName.text = Editable.Factory.getInstance().newEditable(args.user.userName)
        binding.edtDOB.text =
            Editable.Factory.getInstance().newEditable(args.user.dateOfBirth)
//        binding.edtEditImageUrl.text =
//            Editable.Factory.getInstance().newEditable(args.user)

        documentViewModel.updateUserInfoResponse.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
            progressDialog.dismiss()
        }

        binding.btnSaveEditContact.setOnClickListener {
            val newName = binding.edtName.text.toString()
            val newDOB = binding.edtDOB.text.toString()
            val newEmail = binding.edtEmail.text.toString()
            val newImageUrl = binding.edtEditImageUrl.text.toString()

            if (newName == args.user.userName && newEmail == args.user.email) {
                Toast.makeText(
                    context, "No changes detected. Data remains unchanged.", Toast.LENGTH_SHORT
                ).show()
            } else {
                if (newName.isNotEmpty() && newDOB.isNotEmpty() && newEmail.isNotEmpty()) {
                    if (context?.let { it1 ->
                            com.example.appquizlet.util.Helper.validateEmail(
                                it1, newEmail, binding.edtEditEmailLayout
                            )
                        } == true) {
                        val userInfo = UpdateUserResponse(
                            userName = newName,
                            email = newEmail,
                            dateOfBirth = newDOB,
                            avatar = newImageUrl
                        )
                        val json = Gson().toJson(userInfo)
                        val requestBody =
                            RequestBody.create("application/json".toMediaTypeOrNull(), json)

                        lifecycleScope.launch {
                            showLoading("Updating user info ...")
                            documentViewModel.updateUserInfo(
                                requireContext(),
                                args.user.id,
                                requestBody
                            )
                        }

                    } else {
                        Toast.makeText(
                            context,
                            resources.getString(R.string.email_not_valid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.you_must_field_all),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
                R.id.edtEmail -> {
                    if (hasFocus) {
                        if (binding.edtEditEmailLayout.isErrorEnabled) {
                            binding.edtEditEmailLayout.isErrorEnabled = false
                        }
                    } else {
                        context?.let {
                            com.example.appquizlet.util.Helper.validateEmail(
                                it, binding.edtEmail.text.toString(), binding.edtEditEmailLayout
                            )
                        }
                    }
                }

                R.id.edtDOB -> {
                    if (hasFocus) {
                        if (binding.edtEditDOBLayout.isErrorEnabled) {
                            binding.edtEditDOBLayout.isErrorEnabled = false
                        }
                    } else {

                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setFullScreen()
    }

    private fun DialogFragment.setFullScreen() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(context, null, msg)
    }

}
