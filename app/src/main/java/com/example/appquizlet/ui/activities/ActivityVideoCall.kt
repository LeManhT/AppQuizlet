package com.example.appquizlet.ui.activities

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appquizlet.adapter.newfeature.VideoCallAdapter
import com.example.appquizlet.databinding.ActivityVideoCallBinding
import com.example.appquizlet.model.newfeature.ParticipantModel
import com.example.appquizlet.services.SignalRService
import com.example.appquizlet.services.WebRTCManager
import com.example.appquizlet.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.AudioTrack
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import javax.inject.Inject

@AndroidEntryPoint
class ActivityVideoCall : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCallBinding

    @Inject
    lateinit var webRTCManager: WebRTCManager

    @Inject
    lateinit var signalRService: SignalRService
    private lateinit var adapter: VideoCallAdapter
    private val participants = mutableListOf<ParticipantModel>()

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null

    private val screenCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            mediaProjection =
                mediaProjectionManager.getMediaProjection(result.resultCode, result.data!!)
            startScreenCapture()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = VideoCallAdapter(participants)
        binding.rvVideoCallParticipants.layoutManager = GridLayoutManager(this, 2)
        binding.rvVideoCallParticipants.adapter = adapter

        setupLocalVideo()

        binding.btnToggleMic.setOnClickListener {
            webRTCManager.toggleMic()
        }

        binding.btnToggleCamera.setOnClickListener {
            webRTCManager.toggleCamera()
        }
        binding.btnShareScreen.setOnClickListener {
            startScreenSharing()
        }
    }

    private fun setupLocalVideo() {
        val localVideoView = SurfaceViewRenderer(this)
//        localVideoView.init(webRTCManager.eglBase.eglBaseContext, null)

        val localVideoTrack = webRTCManager.createLocalVideoTrack(localVideoView)
        addParticipant(Helper.getDataUserId(this), localVideoTrack, null)
    }

    private fun startScreenSharing() {
        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    private fun startScreenCapture() {

    }

    private fun addParticipant(userId: String, videoTrack: VideoTrack?, audioTrack: AudioTrack?) {
        participants.add(ParticipantModel(userId, videoTrack, audioTrack))
        adapter.notifyItemInserted(participants.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaProjection?.stop()
        webRTCManager.release()
    }
}