package com.example.appquizlet.model.newfeature

import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

data class ParticipantModel(
    val userId: String, // ID của user
    var videoTrack: VideoTrack?, // Video của user
    var audioTrack: AudioTrack? // Audio của user
)
