package com.example.eterra.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.eterra.R
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class GlideLoader (private val context: Context) {

    fun loadUserPicture(imageUri: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadProductPicture(imageUri: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(imageUri)
                .centerCrop()
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}