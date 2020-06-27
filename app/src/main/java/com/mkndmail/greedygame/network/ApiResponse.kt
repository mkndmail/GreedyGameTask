package com.mkndmail.greedygame.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class ApiResponse(val kind: String, val data: ApiData)

data class ApiData(
    val modhash: String?,
    val dist: Int,
    val children: List<Children>,
    val after: String?,
    val before: String?
)

data class Children(val kind: String, @Json(name = "data") val imageData: ImageData)

@Parcelize
data class ImageData(
    @Json(name = "thumbnail_height") val thumbnailHeight: Double,
    val thumbnail: String, @Json(name = "author") val author: String?
) : Parcelable