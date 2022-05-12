package com.example.eterra.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val quantity: String = "",
    val image: String = "",
    val id: String = ""
): Parcelable