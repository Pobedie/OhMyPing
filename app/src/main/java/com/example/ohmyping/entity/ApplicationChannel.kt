package com.example.ohmyping.entity

sealed class ApplicationChannel {
    data class AllChannels(
        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibationPattern
    )

    data class Channel(
        val name: String,
        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibationPattern
    )
}
