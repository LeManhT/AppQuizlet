package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.databinding.ItemAchievementBinding
import com.example.appquizlet.model.AchievementModel

class AchievementAdapter(private val listAchievements: List<AchievementModel>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementHolder>() {
    inner class AchievementHolder(val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementHolder {
        return AchievementHolder(
            ItemAchievementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        )
    }

    override fun onBindViewHolder(holder: AchievementHolder, position: Int) {
        val imgAchievement = holder.binding.imgAchievement
        val txtAchievementName = holder.binding.txtNameAchievement
        val txtAchievementStatus = holder.binding.txtStatus
    }

    override fun getItemCount(): Int {
        return listAchievements.size
    }
}