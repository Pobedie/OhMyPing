package com.pobedie.ohmyping.entity

sealed class ApplicationChannel {
    data class AllChannels(
//        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibrationPattern
    ) : ApplicationChannel()

    data class NamedChannel(
        val id: Long,
        val name: String,
        val isEnabled: Boolean,
        val triggerText: List<String>,
        val vibrationPattern: VibrationPattern,
        val creationTime: Long
    ) : ApplicationChannel()
}
