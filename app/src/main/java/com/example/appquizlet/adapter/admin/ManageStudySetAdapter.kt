package com.example.appquizlet.adapter.admin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.StudySetModel


class ManageStudySetAdapter(
    private val onAdminStudySetClick: IAdminStudySetClick,
) : PagingDataAdapter<StudySetModel, ManageStudySetAdapter.StudySetViewHolder>(STUDY_SET_COMPARATOR) {

    interface IAdminStudySetClick {
        fun handleEditClick(studySet: StudySetModel)
        fun handleDeleteClick(studySet: StudySetModel)
    }

    class StudySetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studySetName: TextView = itemView.findViewById(R.id.txtStudySetName)
        val studySetDescription: TextView = itemView.findViewById(R.id.txtStudySetDescription)
        val studySetNameOwner: TextView = itemView.findViewById(R.id.txtStudySetNameOwner)
        val btnEditStudySet: Button = itemView.findViewById(R.id.btnEditStudySet)
        val btnDeleteStudySet: Button = itemView.findViewById(R.id.btnDeleteStudySet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_studyset, parent, false)
        return StudySetViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudySetViewHolder, position: Int) {
        val studySet = getItem(position) // Use getItem() to retrieve the current study set

        if (studySet != null) {
            holder.studySetName.text = studySet.name
            holder.studySetDescription.text = studySet.description
            holder.studySetNameOwner.text = studySet.nameOwner

            holder.btnEditStudySet.setOnClickListener {
                onAdminStudySetClick.handleEditClick(studySet)
            }

            holder.btnDeleteStudySet.setOnClickListener {
                onAdminStudySetClick.handleDeleteClick(studySet)
            }
        }
    }

    companion object {
        private val STUDY_SET_COMPARATOR = object : DiffUtil.ItemCallback<StudySetModel>() {
            override fun areItemsTheSame(oldItem: StudySetModel, newItem: StudySetModel): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: StudySetModel,
                newItem: StudySetModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
