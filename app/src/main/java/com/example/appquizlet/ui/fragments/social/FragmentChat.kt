package com.example.appquizlet.ui.fragments.social

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.ChatAdapter
import com.example.appquizlet.databinding.FragmentChatBinding
import com.example.appquizlet.model.newfeature.Attachment
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.services.WebRTCManager
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FragmentChat : Fragment() {
    private val viewModel: SocialViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var binding : FragmentChatBinding
    private val navArgs by navArgs<FragmentChatArgs>()

    @Inject
    lateinit var webRTCManager: WebRTCManager

    private val REQUEST_CODE_SPEECH_INPUT = 150
    private var speechRecognitionPosition: Int = -1
    private val REQUEST_CAMERA_CODE = 2404
    private var uri: Uri? = null
    private val STORAGE_CODE = 1001
    private val storageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = ChatAdapter(emptyList())
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())

        binding.textUserName.text = navArgs.conversation.name
        binding.iconMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startSpeechRecognition()
            } else {
                requestSpeechRecognitionPermission()
            }
        }

        binding.iconCall.setOnClickListener {
//            val intent = Intent(requireContext(), ActivityVideoCall::class.java)
//            startActivity(intent)
            webRTCManager.createOffer("6596c374a34b72b309b19985", binding.remoteVideoView)
            webRTCManager.startLocalStream(binding.remoteVideoView)
        }

        binding.iconConversationDetail.setOnClickListener {
            val action = FragmentChatDirections.actionFragmentChatToFragmentConversationDetail(navArgs.conversation)
            findNavController().navigate(action)
        }

        binding.iconHome.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.add_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.option_files -> {
                        Toast.makeText(requireContext(), "Files clicked", Toast.LENGTH_SHORT).show()
                    }

                    R.id.option_location -> {
                        Toast.makeText(requireContext(), "Location clicked", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                true
            }
            popupMenu.show()
        }

        binding.iconCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val permission = Manifest.permission.READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    ImagePicker.with(this).compress(1024).maxResultSize(
                        1080, 1080
                    ).start()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(permission),
                        REQUEST_CAMERA_CODE
                    )
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CAMERA_CODE
                    )
                } else {
                    ImagePicker.with(this).compress(1024).maxResultSize(
                        1080, 1080
                    ).start()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    storageActivityLauncher.launch(intent)
                } catch (e: Exception) {
                    Log.e("requestPermission", e.toString())
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    storageActivityLauncher.launch(intent)
                }
            }
        } else {
            if (requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    STORAGE_CODE
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE
            )
        }

        lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                Log.d("FragmentChat", "Messages: $messages")
                if (messages.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.rvMessages.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.rvMessages.visibility = View.VISIBLE
                    adapter.updateMessages(messages)
                    binding.rvMessages.scrollToPosition(messages.size - 1)
                }
            }
        }

        binding.sendMessageButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotBlank()) {
                val newMessage = Message(
                    senderId = Helper.getDataUserId(requireContext()),
                    receiverId = navArgs.conversation.members[1].userId,
                    content = message,
                    timestamp = System.currentTimeMillis(),
                    conversationId = navArgs.conversation.conversationId,
                    isRead = false,
                    isDeleted = false,
                    attachments = listOf(
                        Attachment(
                            type = "image",
                            url = "https://example.com/image.jpg",
                            fileName = "image.jpg",
                            fileSize = 2048L
                        ),
                        Attachment(
                            type = "pdf",
                            url = "https://example.com/document.pdf",
                            fileName = "document.pdf",
                            fileSize = 1048576L
                        )
                    ),
                    isPinned = false,
                    isSentByUser = true
                )
                viewModel.sendMessage(newMessage)
                binding.messageInput.text.clear()
            }
        }
        viewModel.fetchMessages(Helper.getDataUserId(requireContext()), navArgs.conversation.members[1].userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val matches: ArrayList<String>? = data?.getStringArrayListExtra(
            RecognizerIntent.EXTRA_RESULTS
        )

        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && data != null) {
                    val position = speechRecognitionPosition

                    if (position != -1) {
                        val speechResults =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                        if (!speechResults.isNullOrEmpty()) {
                            val spokenText = speechResults[0]
                            binding.messageInput.text =
                                Editable.Factory.getInstance().newEditable(spokenText.toString())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No speech results found",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Log.e("onActivityResult", "Position is not available in the intent")
                    }
                    speechRecognitionPosition = -1
                }
            }

            REQUEST_CAMERA_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data!!
                    recognizeText()
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            REQUEST_CODE_SPEECH_INPUT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechRecognition()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun recognizeText() {
        if (uri !== null) {
            val inputImage = InputImage.fromFilePath(requireContext(), uri!!)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(inputImage).addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        val lineText = line.text

                    }
                }
            }.addOnFailureListener { e ->
                Log.e("recognizeText", "Error recognizing text: ${e.message}")
            }
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error RecognizerIntent: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestSpeechRecognitionPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE_SPEECH_INPUT
        )
    }

}