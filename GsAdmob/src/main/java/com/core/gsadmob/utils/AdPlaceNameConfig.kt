package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Application
import com.core.gsadmob.R
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

class AdPlaceNameConfig {
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

    fun initAdPlaceNameConfig(application: Application) {
        AD_PLACE_NAME_APP_OPEN.apply {
            name = "app_open"
            adUnitId = application.getString(R.string.app_open_id)
            adGsType = AdGsType.APP_OPEN
        }

        AD_PLACE_NAME_APP_OPEN_RESUME.apply {
            name = "app_open_resume"
            adUnitId = application.getString(R.string.app_open_id_resume)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.APP_OPEN
        }

        AD_PLACE_NAME_BANNER.apply {
            name = "banner"
            adUnitId = application.getString(R.string.banner_id)
            adGsType = AdGsType.BANNER
        }

        AD_PLACE_NAME_BANNER_HOME.apply {
            name = "banner_home"
            adUnitId = application.getString(R.string.banner_id_home)
            adGsType = AdGsType.BANNER
        }

        AD_PLACE_NAME_BANNER_COLLAPSIBLE.apply {
            name = "banner_collapsible"
            adUnitId = application.getString(R.string.banner_id_collapsible)
            adGsType = AdGsType.BANNER_COLLAPSIBLE
        }

        AD_PLACE_NAME_INTERSTITIAL.apply {
            name = "interstitial"
            adUnitId = application.getString(R.string.full_id)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.INTERSTITIAL
        }

        AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO.apply {
            name = "interstitial_without_video"
            adUnitId = application.getString(R.string.full_id_without_video)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.INTERSTITIAL
        }

        AD_PLACE_NAME_NATIVE.apply {
            name = "native"
            adUnitId = application.getString(R.string.native_id)
            adGsType = AdGsType.NATIVE
        }

        AD_PLACE_NAME_NATIVE_LANGUAGE.apply {
            name = "native_language"
            adUnitId = application.getString(R.string.native_id_language)
            adGsType = AdGsType.NATIVE
        }

        AD_PLACE_NAME_REWARDED.apply {
            name = "rewarded"
            adUnitId = application.getString(R.string.rewarded_id)
            adGsType = AdGsType.REWARDED
        }

        AD_PLACE_NAME_REWARDED_INTERSTITIAL.apply {
            name = "rewarded_interstitial"
            adUnitId = application.getString(R.string.rewarded_interstitial_id)
            adGsType = AdGsType.REWARDED_INTERSTITIAL
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var singleton: AdPlaceNameConfig? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: AdPlaceNameConfig
            get() {
                if (singleton == null) {
                    singleton = AdPlaceNameConfig()
                }
                return singleton as AdPlaceNameConfig
            }
    }
}