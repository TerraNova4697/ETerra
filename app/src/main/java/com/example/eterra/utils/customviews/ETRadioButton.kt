package com.example.eterra.utils.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.res.ResourcesCompat
import com.example.eterra.R

class ETRadioButton(context: Context, attrs: AttributeSet): AppCompatRadioButton(context, attrs) {

    init {
        typeface = ResourcesCompat.getFont(context, R.font.montserratregular)
    }

}