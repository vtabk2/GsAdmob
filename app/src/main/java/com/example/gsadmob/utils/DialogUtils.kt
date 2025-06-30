package com.example.gsadmob.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.gsadmob.databinding.DialogDeniedPermissionsBinding

object DialogUtils {
    fun initDeniedPermissionsDialog(context: Context, callback: (granted: Boolean) -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = DialogDeniedPermissionsBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val deniedPermissionsDialog = builder.create()
        deniedPermissionsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvDeniedPermissionsClose.setOnClickListener {
            deniedPermissionsDialog.dismiss()
            callback(false)
        }
        binding.tvDeniedPermissionsSettings.setOnClickListener {
            deniedPermissionsDialog.dismiss()
            callback(true)
        }
        return deniedPermissionsDialog
    }
}