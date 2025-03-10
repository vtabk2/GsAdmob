package com.core.gsadmob.callback

interface AdGsListener {
    fun onAdClose() {}
    fun onAdCloseIfFailed() {}
    fun onShowFinishSuccess() {}
}