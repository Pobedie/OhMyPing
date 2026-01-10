package com.pobedie.ohmyping.entity

import kotlin.random.Random

sealed class ApplicationChannel {
    data class AllChannels(
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

    companion object {
        fun emptyChannel() = NamedChannel(
            id = Random.nextLong(),
            name = "",
            isEnabled = true,
            triggerText = emptyList(),
            vibrationPattern = VibrationPattern.BeeHive,
            creationTime = System.currentTimeMillis()
        )
    }
}
