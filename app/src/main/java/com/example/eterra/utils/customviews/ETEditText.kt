package com.example.eterra.utils.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.example.eterra.R

class ETEditText(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    init {
        typeface = ResourcesCompat.getFont(context, R.font.montserratregular)
    }
}