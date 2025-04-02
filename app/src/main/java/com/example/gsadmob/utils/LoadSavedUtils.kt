package com.example.gsadmob.utils

import android.content.Context

object LoadSavedUtils {
    fun listAssetFiles(context: Context, path: String): List<String> {
        val result = ArrayList<String>()
        context.assets.list(path)?.forEach { file ->
            val innerFiles = listAssetFiles(context, "$path/$file")
            if (innerFiles.isNotEmpty()) {
                result.addAll(innerFiles)
            } else {
                // it can be an empty folder or file you don't like, you can check it here
                result.add("$path/$file")
            }
        }
        return result
    }
}