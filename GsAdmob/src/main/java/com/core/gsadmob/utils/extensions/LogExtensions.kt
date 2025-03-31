package com.core.gsadmob.utils.extensions

import android.util.Log

fun log(message: String, value: Any, showLog: Boolean = true, logType: LogType = LogType.DEBUG) {
    if (showLog) {
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