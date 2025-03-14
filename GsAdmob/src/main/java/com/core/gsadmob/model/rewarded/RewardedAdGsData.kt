package com.core.gsadmob.model.rewarded

import com.core.gsadmob.model.base.BaseShowAdGsData
import com.google.android.gms.ads.rewarded.RewardedAd

class RewardedAdGsData(var rewardedAd: RewardedAd? = null) : BaseShowAdGsData() {
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