package com.core.gsadmob.utils.extensions

import android.util.Log
import com.core.gsadmob.utils.AdGsManager

fun log(message: String, value: Any, logType: LogType = LogType.DEBUG) {
    if (AdGsManager.instance.isShowLog()) {
        when (logType) {
            LogType.DEBUG -> Log.d("GsAdmobLib", "$message = $value")
            LogType.ERROR -> Log.e("GsAdmobLib", "$message = $value")
            LogType.INFO -> Log.i("GsAdmobLib", "$message = $value")
            LogType.VERBOSE -> Log.v("GsAdmobLib", "$message = $value")
            LogType.WARN -> Log.w("GsAdmobLib", "$message = $value")
        }
    }
}

enum class LogType {
    DEBUG,
    ERROR,
    INFO,
    VERBOSE,
    WARN
}