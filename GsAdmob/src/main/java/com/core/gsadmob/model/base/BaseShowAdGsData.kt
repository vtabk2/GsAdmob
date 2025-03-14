package com.core.gsadmob.model.base

open class BaseShowAdGsData(
    var isShowing: Boolean = false,
    var isCancel: Boolean = false
) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        super.clearData(isResetReload)
        isShowing = false
    }
}