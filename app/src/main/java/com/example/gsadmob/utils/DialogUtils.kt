package com.example.gsadmob.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.gsadmob.databinding.DialogGdprPermissionBinding

object DialogUtils {
    fun initGdprPermissionDialog(context: Context, callback: (granted: Boolean) -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = DialogGdprPermissionBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val gdprPermissionDialog = builder.create()
        gdprPermissionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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