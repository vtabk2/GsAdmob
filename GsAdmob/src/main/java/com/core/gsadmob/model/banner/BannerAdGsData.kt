package com.core.gsadmob.model.banner

import com.core.gsadmob.model.base.BaseActiveAdGsData
import com.google.android.gms.ads.AdView

class BannerAdGsData(var bannerAdView: AdView? = null, var isCollapsible: Boolean = false) : BaseActiveAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        bannerAdView?.destroy()
        bannerAdView = null
        super.clearData(isResetReload)
    }

    fun copy(): BannerAdGsData {
        val bannerAdGsData = BannerAdGsData(bannerAdView = bannerAdView, isCollapsible = isCollapsible)
        bannerAdGsData.isActive = isActive
        applyBaseAdGsData(bannerAdGsData)
        return bannerAdGsData
    }
}