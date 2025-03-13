package com.core.gsadmob.model

data class AdPlaceName(
    var name: String = "",
    var adUnitId: Int = 0,
    var isReloadIfNeed: Boolean = false,
    var fragmentTagAppOpenResumeResId: Int = 0,
    var adGsType: AdGsType = AdGsType.INTERSTITIAL
)