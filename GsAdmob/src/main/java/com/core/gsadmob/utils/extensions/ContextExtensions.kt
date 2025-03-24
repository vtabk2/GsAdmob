package com.core.gsadmob.utils.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.webkit.WebViewCompat
import com.core.gsadmob.utils.preferences.CMPUtils

val Context.cmpUtils: CMPUtils get() = CMPUtils.newInstance(applicationContext)

/**
 * Kiểm tra xem thiết bị có bật ứng dụng web view hay không
 * @return true có bật web view -> có thể hiển thị quảng cáo
 * @return false không bật web view -> không thể hiển thị quảng cáo
 */
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