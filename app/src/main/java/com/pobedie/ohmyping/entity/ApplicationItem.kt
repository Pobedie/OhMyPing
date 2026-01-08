package com.pobedie.ohmyping.entity

import android.graphics.Bitmap

data class ApplicationItem(
    val id: String,
    val name: String,
    val icon: Bitmap,
    val isEnabled: Boolean,
    val namedChannels: List<ApplicationChannel.NamedChannel>,
    val allChannels: ApplicationChannel.AllChannels
)
