package com.pobedie.ohmyping.entity

import android.graphics.Bitmap

data class ApplicationItem(
    val name: String,
    val packageName: String,
    val icon: Bitmap,
    val isEnabled: Boolean,
    val namedChannels: List<ApplicationChannel.NamedChannel>,
    val allChannels: ApplicationChannel.AllChannels,
    val creationTime: Long
)
