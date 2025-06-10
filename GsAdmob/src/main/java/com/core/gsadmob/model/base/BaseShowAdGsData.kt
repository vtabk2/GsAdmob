package com.core.gsadmob.model.base

open class BaseShowAdGsData(
    var isCancel: Boolean = false,
    var isShowing: Boolean = false,
    var delayShowTime: Long = 0L,
    var lastShowTime: Long = 0L,
) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        super.clearData(isResetReload)
        isShowing = false
    }
}