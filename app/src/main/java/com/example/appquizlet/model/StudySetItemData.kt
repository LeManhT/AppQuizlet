package com.example.appquizlet.model

class StudySetItemData(
    val title: String, val countTerms: Int ?= 15, val avatar: Int, val username: String, var isSelected : Boolean ?= false
) : java.io.Serializable {
}