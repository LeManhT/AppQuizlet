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
import com.example.appquizlet.adapter.newfeature.GroupChatAdapter
import com.example.appquizlet.databinding.FragmentGroupChatBinding
import com.example.appquizlet.model.newfeature.GroupMember
import com.example.appquizlet.services.SignalRService
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.GroupChatViewModel
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
class FragmentGroupChat : Fragment() {
    private val viewModel: GroupChatViewModel by viewModels()
    private lateinit var adapter: GroupChatAdapter
    private lateinit var binding: FragmentGroupChatBinding
    private val navArgs by navArgs<FragmentGroupChatArgs>()
    private val memberNameMap = mutableMapOf<String, String>()

    @Inject
    lateinit var signalRService: SignalRService

    private val REQUEST_CODE_SPEECH_INPUT = 150
    private val REQUEST_CAMERA_CODE = 2404
    private val STORAGE_CODE = 1001
    private var uri: Uri? = null

    private val storageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupGroupInfo()
        setupObservers()
        setUpClickListeners()
        requestPermissions()

        val conversation = navArgs.conversation

        Log.d("FragmentGroupChat", "Conversation ID: ${conversation.conversationId}")

        viewModel.initWithConversation(conversation)

        viewModel.joinGroup(requireContext(), conversation)

        setupSignalRConnection()
    }

    private fun setupRecyclerView() {
        adapter = GroupChatAdapter(emptyList(), memberNameMap)
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true // To show newest messages at the bottom
        }
    }

    private fun setupGroupInfo() {
        binding.textGroupName.text = navArgs.conversation.name

        // Cập nhật hiển thị số thành viên một cách an toàn
        val memberCount = navArgs.conversation.members.size
        binding.textParticipantCount.text = if (memberCount > 0) {
            "$memberCount participants"
        } else {
            "Loading participants..."
        }
    }

    private fun setupSignalRConnection() {
        val groupId = navArgs.conversation.conversationId
        val userId = Helper.getDataUserId(requireContext())

        Log.d("FragmentGroupChat", "Setting up SignalR connection for group: $groupId")

        if (groupId.isNotEmpty()) {
            signalRService.joinGroup(userId, groupId)

            signalRService.onMessageReceived { receivedUserId, message ->
                viewModel.sendMessage(requireContext(), message.content.toString(), groupId)
            }
        } else {
            Log.e("FragmentGroupChat", "Invalid group ID: Empty string")
            Toast.makeText(requireContext(), "Invalid group ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // Observe active group information
        lifecycleScope.launch {
            viewModel.activeGroup.collectLatest { group ->
                group?.let {
                    binding.textGroupName.text = it.name

                    // Cập nhật số thành viên nếu có
                    val memberCount = it.members.size
                    binding.textParticipantCount.text = "$memberCount participants"
                }
            }
        }

        // Observe messages
        lifecycleScope.launch {
            viewModel.groupMessages.collectLatest { messages ->
                Log.d("FragmentGroupChat", "Messages: ${messages.size}")
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

        // Observe group members
        lifecycleScope.launch {
            viewModel.groupMembers.collectLatest { members ->
                updateMemberMap(members)
            }
        }

        // Observe message status
        viewModel.messageStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is GroupChatViewModel.MessageStatus.Sending -> {
                    // You could show a sending indicator if you want
                }

                is GroupChatViewModel.MessageStatus.Sent -> {
                    // Maybe clear the message input or show a success indicator
                    binding.messageInput.text?.clear()
                }

                is GroupChatViewModel.MessageStatus.Error -> {
                    Toast.makeText(requireContext(), "Error: ${status.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateMemberMap(members: List<GroupMember>) {
        memberNameMap.clear()
        members.forEach { member ->
            memberNameMap[member.userId] = member.userName ?: "Unknown User"
        }
        adapter.updateMemberMap(memberNameMap)
    }

    private fun setUpClickListeners() {
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

        binding.iconGroupInfo.setOnClickListener {
            val action =
                FragmentGroupChatDirections.actionFragmentGroupChatToFragmentConversationDetail(
                    navArgs.conversation
                )
            findNavController().navigate(action)
        }

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.iconHome.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.add_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.option_files -> {
                        Toast.makeText(requireContext(), "Files clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.option_location -> {
                        Toast.makeText(requireContext(), "Location clicked", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.iconCamera.setOnClickListener {
            checkAndRequestCameraPermission()
        }

        binding.sendMessageButton.setOnClickListener {
            Log.d("FragmentGroupChat", "Sending message button clicked : ${navArgs.conversation.conversationId}")
            sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText = binding.messageInput.text.toString().trim()
        val groupId = navArgs.conversation.conversationId
        Log.d("FragmentGroupChat", "Sending message to group: $groupId")
        if (messageText.isNotBlank()) {
            if (groupId.isNotEmpty()) {
                viewModel.sendMessage(requireContext(), messageText, groupId)
            } else {
                Toast.makeText(requireContext(), "Invalid group ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Rest of the code remains the same...
    private fun checkAndRequestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val permission = Manifest.permission.READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startImagePicker()
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
                startImagePicker()
            }
        }
    }

    private fun startImagePicker() {
        ImagePicker.with(this)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    private fun requestPermissions() {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && data != null) {
                    val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (!speechResults.isNullOrEmpty()) {
                        val spokenText = speechResults[0]
                        binding.messageInput.text =
                            Editable.Factory.getInstance().newEditable(spokenText)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No speech results found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            ImagePicker.REQUEST_CODE -> {
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

            REQUEST_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImagePicker()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun recognizeText() {
        uri?.let { imageUri ->
            try {
                val inputImage = InputImage.fromFilePath(requireContext(), imageUri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val recognizedText = StringBuilder()
                        for (block in visionText.textBlocks) {
                            for (line in block.lines) {
                                recognizedText.append(line.text).append(" ")
                            }
                        }

                        // Add recognized text to input field
                        val currentText = binding.messageInput.text.toString()
                        val newText = if (currentText.isEmpty()) recognizedText.toString()
                        else "$currentText ${recognizedText.toString()}"
                        binding.messageInput.text =
                            Editable.Factory.getInstance().newEditable(newText)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FragmentGroupChat", "Text recognition failed: ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "Failed to recognize text",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Log.e("FragmentGroupChat", "Error processing image: ${e.message}")
                Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Speech recognition not available: ${e.message}",
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

    override fun onDestroy() {
        signalRService.leaveGroup(
            Helper.getDataUserId(requireContext()),
            navArgs.conversation.conversationId
        )
        super.onDestroy()
    }
}