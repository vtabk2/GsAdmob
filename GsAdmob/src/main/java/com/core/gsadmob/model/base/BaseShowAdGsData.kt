package com.core.gsadmob.model.base

open class BaseShowAdGsData(var isCancel: Boolean = false, var isShowing: Boolean = false) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        super.clearData(isResetReload)
        isShowing = false
    }
}