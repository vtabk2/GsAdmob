package com.core.gsadmob.model

import com.core.gsadmob.callback.AdGsListener

open class BaseAdGsData(
    var listener: AdGsListener? = null,
    var isReload: Boolean = false,
    var isLoading: Boolean = false,
    var delayTime: Long = 0L,
    var lastTime: Long = 0L
) : BaseData() {
    override fun clearData(isResetReload: Boolean) {
        listener = null
        if (isResetReload) {
            isReload = false
        }
        isLoading = false
        lastTime = 0L
    }
}