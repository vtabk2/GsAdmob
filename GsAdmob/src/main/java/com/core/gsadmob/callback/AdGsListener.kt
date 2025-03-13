package com.core.gsadmob.callback

interface AdGsListener {
    fun onAdClose(isFailed: Boolean = false) {}
    fun onAdSuccess() {}
    fun onAdClicked() {}
    fun onShowFinishSuccess() {} // dùng cho quảng cáo trả thưởng
}