package com.example.ohmyping.entity

data class ApplicationItem(
    val icon: Int,
    val name: String,
    val isEnabled: Boolean,
    val applicationChannels: List<ApplicationChannel>
)
