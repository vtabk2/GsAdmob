package com.core.gsadmob.model

import com.core.gsadmob.callback.AdGsListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

data class AdGsData(
    var interstitialAd: InterstitialAd? = null,
    var rewardedAd: RewardedAd? = null,
    var rewardedInterstitialAd: RewardedInterstitialAd? = null,
    var listener: AdGsListener? = null,
    var isReload: Boolean = false,
    var isLoading: Boolean = false,
    var isShowing: Boolean = false,
    var isCancel: Boolean = false,
    var delayTime: Long = 0L,
    var lastTime: Long = 0L
) {
    fun clearData() {
        interstitialAd = null
        rewardedAd = null
        rewardedInterstitialAd = null
        listener = null
        isReload = false
        isLoading = false
        lastTime = 0L
    }
}