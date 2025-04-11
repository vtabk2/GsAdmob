package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Application
import com.core.gsadmob.R
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

class AdPlaceNameDefaultConfig {
    private var isCreated: Boolean = false

    val AD_PLACE_NAME_APP_OPEN = AdPlaceName(adGsType = AdGsType.APP_OPEN)
    val AD_PLACE_NAME_APP_OPEN_RESUME = AdPlaceName(adGsType = AdGsType.APP_OPEN)

    //
    val AD_PLACE_NAME_BANNER = AdPlaceName(adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_HOME = AdPlaceName(adGsType = AdGsType.BANNER)
    val AD_PLACE_NAME_BANNER_COLLAPSIBLE = AdPlaceName(adGsType = AdGsType.BANNER_COLLAPSIBLE)

    //
    val AD_PLACE_NAME_INTERSTITIAL = AdPlaceName(adGsType = AdGsType.INTERSTITIAL)
    val AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO = AdPlaceName(adGsType = AdGsType.INTERSTITIAL)

    //
    val AD_PLACE_NAME_NATIVE = AdPlaceName(adGsType = AdGsType.NATIVE)
    val AD_PLACE_NAME_NATIVE_LANGUAGE = AdPlaceName(adGsType = AdGsType.NATIVE)

    //
    val AD_PLACE_NAME_REWARDED = AdPlaceName(adGsType = AdGsType.REWARDED)
    val AD_PLACE_NAME_REWARDED_INTERSTITIAL = AdPlaceName(adGsType = AdGsType.REWARDED_INTERSTITIAL)

    fun initAdPlaceNameDefaultConfig(application: Application) {
        if (isCreated) {
            return
        }
        isCreated = true

        AD_PLACE_NAME_APP_OPEN.update(name = "app_open", adUnitId = application.getString(R.string.app_open_id))

        AD_PLACE_NAME_APP_OPEN_RESUME.apply {
            name = "app_open_resume"
            adUnitId = application.getString(R.string.app_open_id_resume)
            autoReloadWhenDismiss = true
        }

        AD_PLACE_NAME_BANNER.update(name = "banner", adUnitId = application.getString(R.string.banner_id))

        AD_PLACE_NAME_BANNER_HOME.update(name = "banner_home", adUnitId = application.getString(R.string.banner_id_home))

        AD_PLACE_NAME_BANNER_COLLAPSIBLE.update(name = "banner_collapsible", adUnitId = application.getString(R.string.banner_id_collapsible))

        AD_PLACE_NAME_INTERSTITIAL.apply {
            name = "interstitial"
            adUnitId = application.getString(R.string.interstitial_id)
            autoReloadWhenDismiss = true
        }

        AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO.apply {
            name = "interstitial_without_video"
            adUnitId = application.getString(R.string.interstitial_id_without_video)
            autoReloadWhenDismiss = true
        }

        AD_PLACE_NAME_NATIVE.update(name = "native", adUnitId = application.getString(R.string.native_id))

        AD_PLACE_NAME_NATIVE_LANGUAGE.update(name = "native_language", adUnitId = application.getString(R.string.native_id_language))

        AD_PLACE_NAME_REWARDED.update(name = "rewarded", adUnitId = application.getString(R.string.rewarded_id))

        AD_PLACE_NAME_REWARDED_INTERSTITIAL.update(name = "rewarded_interstitial", adUnitId = application.getString(R.string.rewarded_interstitial_id))
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var singleton: AdPlaceNameDefaultConfig? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: AdPlaceNameDefaultConfig
            get() {
                if (singleton == null) {
                    singleton = AdPlaceNameDefaultConfig()
                }
                return singleton as AdPlaceNameDefaultConfig
            }
    }
}