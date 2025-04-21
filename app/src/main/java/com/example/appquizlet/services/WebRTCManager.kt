package com.example.appquizlet.services

import android.content.Context
import android.media.projection.MediaProjection
import com.example.appquizlet.util.Helper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import javax.inject.Inject

class WebRTCManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signalRService: SignalRService
) {
    private val peerConnectionFactory: PeerConnectionFactory
    val eglBase: EglBase = EglBase.create()
    private val peers = mutableMapOf<String, PeerConnection>()
    private val peerVideoViews = mutableMapOf<String, SurfaceViewRenderer>()

    private var localPeerConnection: PeerConnection? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var screenTrack: VideoTrack? = null
    private var screenCapturer: ScreenCapturerAndroid? = null
    private var videoCapturer: CameraVideoCapturer? = null

    private val surfaceTextureHelper: SurfaceTextureHelper by lazy {
        SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
    }

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()

        signalRService.receiveSignal { type, sender, data ->
            handleSignal(type, sender, data)
        }
    }

    private fun createPeerConnection(
        peerId: String,
        remoteVideoView: SurfaceViewRenderer
    ): PeerConnection {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

        val peer =
            peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    signalRService.sendSignal(
                        "ICE",
                        Helper.getDataUserId(context),
                        peerId,
                        iceCandidate.toString()
                    )
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    mediaStream.videoTracks.firstOrNull()?.addSink(remoteVideoView)
                }

                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
                override fun onIceConnectionReceivingChange(p0: Boolean) {}
                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
                override fun onRemoveStream(p0: MediaStream?) {}
                override fun onDataChannel(p0: DataChannel?) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
            })!!

        peers[peerId] = peer
        return peer
    }

    fun startLocalStream(localVideoView: SurfaceViewRenderer) {
        localVideoView.init(eglBase.eglBaseContext, null)

        videoCapturer = createCameraCapturer()
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer!!.isScreencast)
        videoCapturer!!.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer!!.startCapture(1280, 720, 30)

        localVideoTrack = peerConnectionFactory.createVideoTrack("LOCAL_VIDEO", videoSource)
        localVideoTrack!!.addSink(localVideoView)

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory.createAudioTrack("LOCAL_AUDIO", audioSource)

        val mediaStream = peerConnectionFactory.createLocalMediaStream("localStream")
        mediaStream.addTrack(localVideoTrack)
        mediaStream.addTrack(localAudioTrack)

        localPeerConnection = createPeerConnection(Helper.getDataUserId(context), localVideoView)
        localPeerConnection?.addStream(mediaStream)
    }


    fun toggleMic() {
        localAudioTrack?.setEnabled(localAudioTrack?.enabled()?.not() ?: true)
    }

    fun toggleCamera() {
        localVideoTrack?.setEnabled(localVideoTrack?.enabled()?.not() ?: true)
    }

    fun startScreenShare(mediaProjection: MediaProjection) {
//        val videoSource = peerConnectionFactory.createVideoSource(false)
//        screenCapturer = ScreenCapturerAndroid(
//            mediaProjection,
//            object : MediaProjection.Callback() {
//                override fun onStop() {
//                    stopScreenShare()
//                }
//            }
//        )
//
//        screenTrack = peerConnectionFactory.createVideoTrack("screenTrack", videoSource)
//        screenTrack?.let {
//            val screenStream = peerConnectionFactory.createLocalMediaStream("screenStream")
//            screenStream.addTrack(it)
//            localPeerConnection?.addStream(screenStream)
//        }
    }

    fun stopScreenShare() {
        screenTrack?.setEnabled(false)
        screenTrack = null
        screenCapturer = null
    }

    private fun closeAllConnections() {
        peers.values.forEach { it.close() }
        peers.clear()
        localPeerConnection?.close()
        localPeerConnection = null
    }

    fun release() {
        closeAllConnections()
        peerConnectionFactory.dispose()
        eglBase.release()
    }

    fun createLocalVideoTrack(surfaceViewRenderer: SurfaceViewRenderer): VideoTrack {
        surfaceViewRenderer.init(eglBase.eglBaseContext, null)

        val videoCapturer = createCameraCapturer()
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(
            surfaceTextureHelper, context, videoSource.capturerObserver
        )
        videoCapturer.startCapture(1280, 720, 30)

        val localVideoTrack = peerConnectionFactory.createVideoTrack("LOCAL_VIDEO", videoSource)
        localVideoTrack.addSink(surfaceViewRenderer)

        return localVideoTrack
    }

    private fun createCameraCapturer(): CameraVideoCapturer {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        throw IllegalStateException("No front-facing camera found")
    }

    private fun handleSignal(type: String, sender: String, data: String) {
        when (type) {
            "OFFER" -> receiveOffer(sender, data)
            "ANSWER" -> receiveAnswer(sender, data)
            "ICE" -> receiveIceCandidate(sender, data)
        }
    }

    fun createOffer(peerId: String, remoteVideoView: SurfaceViewRenderer) {
        val peerConnection = createPeerConnection(peerId, remoteVideoView)
        peerVideoViews[peerId] = remoteVideoView
        val mediaConstraints = MediaConstraints()

        peerConnection.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(p0: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), p0)
                if (p0 != null) {
                    signalRService.sendSignal(
                        "OFFER",
                        Helper.getDataUserId(context),
                        peerId,
                        p0.description
                    )
                }
            }
        }, mediaConstraints)
    }

    private fun receiveOffer(peerId: String, sdp: String) {
        val remoteVideoView = peerVideoViews[peerId] ?: return
        val peerConnection = createPeerConnection(peerId, remoteVideoView)
        val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, sdp)

        peerConnection.setRemoteDescription(SdpObserverAdapter(), sessionDescription)

        peerConnection.createAnswer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(p0: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), sessionDescription)
                signalRService.sendSignal(
                    "ANSWER",
                    Helper.getDataUserId(context),
                    peerId,
                    sessionDescription.description
                )
            }
        }, MediaConstraints())
    }

    private fun receiveAnswer(peerId: String, sdp: String) {
        val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        peers[peerId]?.setRemoteDescription(SdpObserverAdapter(), sessionDescription)
    }

    private fun receiveIceCandidate(peerId: String, iceData: String) {
        val parts = iceData.split(",")
        val iceCandidate = IceCandidate(parts[0], parts[1].toInt(), parts[2])
        peers[peerId]?.addIceCandidate(iceCandidate)
    }

    fun endCall() {
        peers.values.forEach { it.close() }
        peers.clear()
        localPeerConnection?.close()
        localPeerConnection = null
    }
}

open class SdpObserverAdapter : SdpObserver {
    override fun onCreateSuccess(p0: SessionDescription?) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(p0: String?) {}
    override fun onSetFailure(p0: String?) {}
}
