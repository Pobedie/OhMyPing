package com.pobedie.ohmyping.entity

import android.graphics.Bitmap


data class UserApplication(
    val name: String,
    val packageName: String,
    val icon: Bitmap
)
