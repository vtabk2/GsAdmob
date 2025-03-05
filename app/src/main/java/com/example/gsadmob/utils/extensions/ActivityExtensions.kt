package com.example.gsadmob.utils.extensions

import android.app.Activity
import android.graphics.Rect
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog

fun Activity.dialogLayout(dialog: AlertDialog?, percent: Float = 0.85f) {
    // retrieve display dimensions
    val displayRectangle = Rect()
    window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
    dialog?.window?.setLayout((displayRectangle.width() * percent).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
}