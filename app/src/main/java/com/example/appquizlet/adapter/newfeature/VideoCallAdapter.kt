package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.databinding.VideoItemBinding

import com.example.appquizlet.model.newfeature.ParticipantModel

class VideoCallAdapter(
    private val participants: List<ParticipantModel>
) : RecyclerView.Adapter<VideoCallAdapter.ParticipantViewHolder>() {

    inner class ParticipantViewHolder(private val binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(participant: ParticipantModel) {
            binding.txtUserName.text = participant.userId

            participant.videoTrack?.addSink(binding.videoView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding =
            VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size
}
