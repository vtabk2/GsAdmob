package com.core.gsadmob.model

import com.google.android.gms.ads.interstitial.InterstitialAd

class InterstitialAdGsData(var interstitialAd: InterstitialAd? = null) : BaseAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        interstitialAd = null
        super.clearData(isResetReload)
    }
}