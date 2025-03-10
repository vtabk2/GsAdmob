package com.core.gsadmob.utils

import com.core.gsadmob.R
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

object AdPlaceNameConfig {

    val AD_PLACE_NAME_APP_OPEN_ADS = AdPlaceName(name = "Open ads", adUnitId = R.string.app_open_id, adGsType = AdGsType.OPEN_ADS)
    val AD_PLACE_NAME_APP_OPEN_ADS_RESUME = AdPlaceName(name = "Open ads resume", adUnitId = R.string.app_open_id_resume, adGsType = AdGsType.OPEN_ADS)

    val AD_PLACE_NAME_BANNER = AdPlaceName(name = "Banner", adUnitId = R.string.banner_id, adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_HOME = AdPlaceName(name = "Banner home", adUnitId = R.string.banner_id_home, adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_COLLAPSIBLE = AdPlaceName(name = "Banner collapsible", adUnitId = R.string.banner_id_collapsible, adGsType = AdGsType.BANNER)

    val AD_PLACE_NAME_FULL = AdPlaceName(name = "Full", adUnitId = R.string.full_id, adGsType = AdGsType.INTERSTITIAL)
    val AD_PLACE_NAME_FULL_WITHOUT_VIDEO = AdPlaceName(name = "Full without video", adUnitId = R.string.full_without_video, adGsType = AdGsType.INTERSTITIAL)

    val AD_PLACE_NAME_NATIVE = AdPlaceName(name = "Native", adUnitId = R.string.native_id, adGsType = AdGsType.NATIVE)
    val AD_PLACE_NAME_NATIVE_LANGUAGE = AdPlaceName(name = "Native language", adUnitId = R.string.native_language, adGsType = AdGsType.NATIVE)

    val AD_PLACE_NAME_REWARDED = AdPlaceName(name = "Rewarded", adUnitId = R.string.rewarded_id, adGsType = AdGsType.REWARDED)
    val AD_PLACE_NAME_REWARDED_INTERSTITIAL = AdPlaceName(name = "Rewarded Interstitial", adUnitId = R.string.rewarded_interstitial_id, adGsType = AdGsType.REWARDED_INTERSTITIAL)
}