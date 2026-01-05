package com.example.ohmyping.entity

data class ApplicationChannel(
    val name: String,
    val isEnabled: Boolean,
    val triggerText: List<String>,
    val vibrationPattern: VibationPattern
)
