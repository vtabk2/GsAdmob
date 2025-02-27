package com.core.gsadmob.utils.extensions

import android.view.View
import android.view.ViewGroup

fun View.setMarginExtensionFunction(left: Int, top: Int, right: Int, bottom: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left, top, right, bottom)
    layoutParams = params
}