package com.core.gsadmob.model.base

import com.core.gsadmob.callback.AdGsExtendListener
import com.core.gsadmob.callback.AdGsListener

open class BaseAdGsData(
    var listener: AdGsListener? = null,
    var extendListener: AdGsExtendListener? = null,
    var isReload: Boolean = false,
    var isLoading: Boolean = false,
    var delayTime: Long = 0L,
    var lastTime: Long = 0L,
) {
    open fun clearData(isResetReload: Boolean) {
        listener = null
        extendListener = null
        if (isResetReload) {
            isReload = false
        }
        isLoading = false
    }

    fun applyBaseAdGsData(baseAdGsData: BaseAdGsData) {
        baseAdGsData.listener = listener
        baseAdGsData.extendListener = extendListener
        baseAdGsData.isReload = isReload
        baseAdGsData.isLoading = isLoading
        baseAdGsData.delayTime = delayTime
        baseAdGsData.lastTime = lastTime
    }
}