package com.pobedie.ohmyping.entity

data class ApplicationItem(
    val name: String,
    val packageName: String,
    val icon: ByteArray?,
    val isEnabled: Boolean,
    val namedChannels: List<ApplicationChannel.NamedChannel>,
    val allChannels: ApplicationChannel.AllChannels,
    val creationTime: Long
)
