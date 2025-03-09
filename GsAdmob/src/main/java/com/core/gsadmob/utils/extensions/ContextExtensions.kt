package com.core.gsadmob.utils.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.webkit.WebViewCompat

fun Context.isWebViewEnabled(): Boolean {
    val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this)
    val isWebViewEnabled = webViewPackageInfo != null
            && packageManager?.getApplicationEnabledSetting(webViewPackageInfo.packageName).let { setting ->
        setting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                && setting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                && setting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
    }
    return isWebViewEnabled
}