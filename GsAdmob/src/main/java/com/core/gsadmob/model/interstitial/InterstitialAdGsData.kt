package com.core.gsadmob.model.interstitial

import com.core.gsadmob.model.base.BaseShowAdGsData
import com.google.android.gms.ads.interstitial.InterstitialAd

class InterstitialAdGsData(var interstitialAd: InterstitialAd? = null) : BaseShowAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        interstitialAd = null
        super.clearData(isResetReload)
    }

    fun copy(): InterstitialAdGsData {
        val interstitialAdGsData = InterstitialAdGsData(interstitialAd = interstitialAd)
        applyBaseAdGsData(interstitialAdGsData)
        return interstitialAdGsData
    }
}