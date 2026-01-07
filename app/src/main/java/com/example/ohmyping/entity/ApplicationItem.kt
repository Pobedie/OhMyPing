package com.example.ohmyping.entity

import android.graphics.Bitmap

data class ApplicationItem(
    val id: String,
    val name: String,
    val icon: Bitmap,
    val isEnabled: Boolean,
    val namedChannels: List<ApplicationChannel.Channel>,
    val allChannels: ApplicationChannel.AllChannels
)
