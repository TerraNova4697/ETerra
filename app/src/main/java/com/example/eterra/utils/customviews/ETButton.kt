package com.example.eterra.utils.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.example.eterra.R

class ETButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {
    init {
        typeface = ResourcesCompat.getFont(context, R.font.montserratregular)
    }
}