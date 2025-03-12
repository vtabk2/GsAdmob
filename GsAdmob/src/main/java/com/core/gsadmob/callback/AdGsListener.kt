package com.core.gsadmob.callback

interface AdGsListener {
    fun onAdClose(isFailed: Boolean = false) {}
    fun onAdFailed() {}
    fun onAdSuccess() {}
    fun onAdClicked() {}
    fun onShowFinishSuccess() {}
}