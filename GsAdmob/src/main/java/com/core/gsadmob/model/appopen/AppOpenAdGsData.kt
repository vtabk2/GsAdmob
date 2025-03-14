package com.core.gsadmob.model.appopen

import com.core.gsadmob.model.base.BaseShowAdGsData
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenAdGsData(var appOpenAd: AppOpenAd? = null) : BaseShowAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        appOpenAd = null
        super.clearData(isResetReload)
    }

    fun copy(): AppOpenAdGsData {
        val appOpenAdGsData = AppOpenAdGsData(appOpenAd = appOpenAd)
        applyBaseAdGsData(appOpenAdGsData)
        return appOpenAdGsData
    }
}