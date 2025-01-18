package com.example.apod_fetch.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Entity(tableName = "apod")
data class ApodPhoto(
    val copyright: String? = null,
    @PrimaryKey(autoGenerate = false)
    val date: String,
    val explanation: String? = null,
    val hdurl: String? = null,
    val media_type: String? = null,
    val service_version: String? = null,
    val title: String? = null,
    val url: String? = null,
    val thumbnail_url: String? = null,
    var favourite: Boolean = false
) : Parcelable