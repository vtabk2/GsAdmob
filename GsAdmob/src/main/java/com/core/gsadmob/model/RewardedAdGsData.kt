package com.core.gsadmob.model

import com.google.android.gms.ads.rewarded.RewardedAd

class RewardedAdGsData(var rewardedAd: RewardedAd? = null) : BaseRewardedAdGsData() {
    override fun clearData(isResetReload: Boolean) {
        rewardedAd = null
        super.clearData(isResetReload)
    }

    fun copy(): RewardedAdGsData {
        val rewardedAdGsData = RewardedAdGsData(rewardedAd = rewardedAd)
        applyBaseAdGsData(rewardedAdGsData)
        return rewardedAdGsData
    }
}