package com.core.gsadmob.callback

interface AdGsListener {
    fun onAdClose() {}
    fun onAdFailed() {}
    fun onAdSuccess() {}
    fun onAdClicked() {}
    fun onAdCloseIfFailed() {}
    fun onShowFinishSuccess() {}
}