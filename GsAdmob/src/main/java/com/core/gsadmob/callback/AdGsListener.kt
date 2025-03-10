package com.core.gsadmob.callback

interface AdGsListener {
    fun onAdClose(from: String) {}
    fun onAdCloseIfFailed() {}
    fun onShowFinishSuccess() {}
}