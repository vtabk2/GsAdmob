package com.core.gsadmob.model.base

open class BaseActiveAdGsData(var isActive: Boolean = false) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        super.clearData(isResetReload)
        isActive = false
    }
}