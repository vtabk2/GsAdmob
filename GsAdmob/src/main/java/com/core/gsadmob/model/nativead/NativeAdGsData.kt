package com.core.gsadmob.model.nativead

import com.core.gsadmob.model.base.BaseActiveAdGsData
import com.google.android.gms.ads.nativead.NativeAd

class NativeAdGsData(var nativeAd: NativeAd? = null) : BaseActiveAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        nativeAd = null
        super.clearData(isResetReload)
    }

    fun copy(): NativeAdGsData {
        val nativeAdGsData = NativeAdGsData(nativeAd = nativeAd)
        nativeAdGsData.isActive = isActive
        applyBaseAdGsData(nativeAdGsData)
        return nativeAdGsData
    }
}