package com.example.appquizlet.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

class Story(
    val title: String,
    val content: String,
    val newWords: List<NewWord>,
    val imagePath: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        title = parcel.readString() ?: "",
        content = parcel.readString() ?: "",
        newWords = parcel.createTypedArrayList(NewWord.CREATOR) ?: emptyList(),
        imagePath = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeTypedList(newWords)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity(tableName = "favourite_words")
data class NewWord(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val word: String,
    var meaning: String,
    var isFavourite: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        word = parcel.readString() ?: "",
        meaning = parcel.readString() ?: "",
        isFavourite = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(word)
        parcel.writeString(meaning)
        parcel.writeByte(if (isFavourite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewWord> {
        override fun createFromParcel(parcel: Parcel): NewWord {
            return NewWord(parcel)
        }

        override fun newArray(size: Int): Array<NewWord?> {
            return arrayOfNulls(size)
        }
    }
}
