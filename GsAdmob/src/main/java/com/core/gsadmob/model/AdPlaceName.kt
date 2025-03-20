package com.core.gsadmob.model

data class AdPlaceName(
    var name: String = "",
    var adUnitId: Int = 0,
    var fragmentTagAppOpenResumeResId: Int = 0,
    var autoReloadWhenDismiss: Boolean = false,
    var adGsType: AdGsType = AdGsType.INTERSTITIAL
)