package com.core.gsadmob.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.core.gsadmob.databinding.AdDialogGdprPermissionBinding
import androidx.core.graphics.drawable.toDrawable

object DialogUtils {
    fun initGdprPermissionDialog(context: Context, callback: (granted: Boolean) -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = AdDialogGdprPermissionBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val gdprPermissionDialog = builder.create()
        gdprPermissionDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        binding.imageGdprClose.setOnClickListener {
            gdprPermissionDialog.dismiss()
            callback.invoke(false)
        }
        binding.tvGdprGrant.setOnClickListener {
            gdprPermissionDialog.dismiss()
            callback.invoke(true)
        }
        return gdprPermissionDialog
    }
}