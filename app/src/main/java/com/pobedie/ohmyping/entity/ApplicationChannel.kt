package com.pobedie.ohmyping.entity

sealed class ApplicationChannel {
    data class AllChannels(
        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibationPattern
    ) : ApplicationChannel()

    data class NamedChannel(
        val id: String,
        val name: String,
        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibationPattern
    ) : ApplicationChannel()
}
