package com.core.gsadmob.utils

import com.core.gsadmob.R
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

object AdPlaceNameConfig {
    val AD_PLACE_NAME_APP_OPEN = AdPlaceName(name = "App open", adUnitId = R.string.app_open_id, adGsType = AdGsType.APP_OPEN_AD)
    val AD_PLACE_NAME_APP_OPEN_RESUME = AdPlaceName(
        name = "App open resume",
        adUnitId = R.string.app_open_id_resume,
        autoReloadWhenDismiss = true,
        adGsType = AdGsType.APP_OPEN_AD
    )

    val AD_PLACE_NAME_BANNER = AdPlaceName(name = "Banner", adUnitId = R.string.banner_id, adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_HOME = AdPlaceName(name = "Banner home", adUnitId = R.string.banner_id_home, adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_COLLAPSIBLE = AdPlaceName(name = "Banner collapsible", adUnitId = R.string.banner_id_collapsible, adGsType = AdGsType.BANNER_COLLAPSIBLE)

    val AD_PLACE_NAME_FULL = AdPlaceName(name = "Full", adUnitId = R.string.full_id, autoReloadWhenDismiss = true, adGsType = AdGsType.INTERSTITIAL)
    val AD_PLACE_NAME_FULL_WITHOUT_VIDEO = AdPlaceName(name = "Full without video", adUnitId = R.string.full_id_without_video, autoReloadWhenDismiss = true, adGsType = AdGsType.INTERSTITIAL)

    val AD_PLACE_NAME_NATIVE = AdPlaceName(name = "Native", adUnitId = R.string.native_id, adGsType = AdGsType.NATIVE)
    val AD_PLACE_NAME_NATIVE_LANGUAGE = AdPlaceName(name = "Native language", adUnitId = R.string.native_id_language, adGsType = AdGsType.NATIVE)

    val AD_PLACE_NAME_REWARDED = AdPlaceName(name = "Rewarded", adUnitId = R.string.rewarded_id, adGsType = AdGsType.REWARDED)
    val AD_PLACE_NAME_REWARDED_INTERSTITIAL = AdPlaceName(name = "Rewarded Interstitial", adUnitId = R.string.rewarded_interstitial_id, adGsType = AdGsType.REWARDED_INTERSTITIAL)
}