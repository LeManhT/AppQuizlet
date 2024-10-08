package com.example.appquizlet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class DocumentModel(
    val folders: @RawValue List<FolderModel>,
    val studySets: @RawValue List<StudySetModel>,
    val flashCards: @RawValue List<FlashCardModel>
) : Parcelable