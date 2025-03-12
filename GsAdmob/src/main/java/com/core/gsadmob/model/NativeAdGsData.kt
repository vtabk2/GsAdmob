package com.core.gsadmob.model

import com.google.android.gms.ads.nativead.NativeAd

class NativeAdGsData(var nativeAd: NativeAd? = null) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        nativeAd = null
        super.clearData(isResetReload)
    }

    fun copy(): NativeAdGsData {
        val nativeAdGsData = NativeAdGsData(nativeAd = nativeAd)
        applyBaseAdGsData(nativeAdGsData)
        return nativeAdGsData
    }
}