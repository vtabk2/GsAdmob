package com.core.gsadmob.model

import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

class RewardedInterstitialAdGsData(var rewardedInterstitialAd: RewardedInterstitialAd? = null) : BaseRewardedAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        rewardedInterstitialAd = null
        super.clearData(isResetReload)
    }

    fun copy(): RewardedInterstitialAdGsData {
        val rewardedInterstitialAdGsData = RewardedInterstitialAdGsData(rewardedInterstitialAd = rewardedInterstitialAd)
        applyBaseAdGsData(rewardedInterstitialAdGsData)
        return rewardedInterstitialAdGsData
    }
}