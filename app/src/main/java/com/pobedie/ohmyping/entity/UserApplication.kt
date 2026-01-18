package com.pobedie.ohmyping.entity

import android.content.pm.ApplicationInfo


data class UserApplication(
    val name: String,
    val packageName: String,
    val icon: ByteArray?,
    val appInfo: ApplicationInfo
)
