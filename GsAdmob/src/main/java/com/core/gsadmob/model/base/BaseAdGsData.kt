package com.core.gsadmob.model.base

import com.core.gsadmob.callback.AdGsListener

open class BaseAdGsData(
    var listener: AdGsListener? = null,
    var isReload: Boolean = false,
    var isLoading: Boolean = false,
    var delayTime: Long = 0L,
    var lastTime: Long = 0L
) {
    open fun clearData(isResetReload: Boolean) {
        listener = null
        if (isResetReload) {
            isReload = false
        }
        isLoading = false
        lastTime = 0L
    }

    fun applyBaseAdGsData(baseAdGsData: BaseAdGsData) {
        baseAdGsData.listener = listener
        baseAdGsData.isReload = isReload
        baseAdGsData.isLoading = isLoading
        baseAdGsData.delayTime = delayTime
        baseAdGsData.lastTime = lastTime
    }
}