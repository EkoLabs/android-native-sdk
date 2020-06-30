package com.eko.sdk

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar

/**
 * The default cover to be shown if not replaced in the EkoOptions.
 * This is a simple black view with a white spinner in the middle.
 */
class EkoDefaultCover(context: Context) : FrameLayout(context) {
    init {
        setBackgroundColor(Color.BLACK)
        val spinnerSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics)
                .toInt()
        val spinner = ProgressBar(context)
        spinner.layoutParams = LayoutParams(spinnerSize, spinnerSize, Gravity.CENTER)
        spinner.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        addView(spinner)
    }
}