package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Application
import com.core.gsadmob.R
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

class AdPlaceNameConfig {
    val AD_PLACE_NAME_APP_OPEN = AdPlaceName()
    val AD_PLACE_NAME_APP_OPEN_RESUME = AdPlaceName()

    //
    val AD_PLACE_NAME_BANNER = AdPlaceName()
    val AD_PLACE_NAME_BANNER_HOME = AdPlaceName()
    val AD_PLACE_NAME_BANNER_COLLAPSIBLE = AdPlaceName()

    //
    val AD_PLACE_NAME_FULL = AdPlaceName()
    val AD_PLACE_NAME_FULL_WITHOUT_VIDEO = AdPlaceName()

    //
    val AD_PLACE_NAME_NATIVE = AdPlaceName()
    val AD_PLACE_NAME_NATIVE_LANGUAGE = AdPlaceName()

    //
    val AD_PLACE_NAME_REWARDED = AdPlaceName()
    val AD_PLACE_NAME_REWARDED_INTERSTITIAL = AdPlaceName()

    fun initAdPlaceNameConfig(application: Application) {
        AD_PLACE_NAME_APP_OPEN.apply {
            name = "App open"
            adUnitId = application.getString(R.string.app_open_id)
            adGsType = AdGsType.APP_OPEN_AD
        }

        AD_PLACE_NAME_APP_OPEN_RESUME.apply {
            name = "App open resume"
            adUnitId = application.getString(R.string.app_open_id_resume)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.APP_OPEN_AD
        }

        AD_PLACE_NAME_BANNER.apply {
            name = "Banner"
            adUnitId = application.getString(R.string.banner_id)
            adGsType = AdGsType.BANNER
        }

        AD_PLACE_NAME_BANNER_HOME.apply {
            name = "Banner home"
            adUnitId = application.getString(R.string.banner_id_home)
            adGsType = AdGsType.BANNER
        }

        AD_PLACE_NAME_BANNER_COLLAPSIBLE.apply {
            name = "Banner collapsible"
            adUnitId = application.getString(R.string.banner_id_collapsible)
            adGsType = AdGsType.BANNER_COLLAPSIBLE
        }

        AD_PLACE_NAME_FULL.apply {
            name = "Full"
            adUnitId = application.getString(R.string.full_id)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.INTERSTITIAL
        }

        AD_PLACE_NAME_FULL_WITHOUT_VIDEO.apply {
            name = "Full without video"
            adUnitId = application.getString(R.string.full_id_without_video)
            autoReloadWhenDismiss = true
            adGsType = AdGsType.INTERSTITIAL
        }

        AD_PLACE_NAME_NATIVE.apply {
            name = "Native"
            adUnitId = application.getString(R.string.native_id)
            adGsType = AdGsType.NATIVE
        }

        AD_PLACE_NAME_NATIVE_LANGUAGE.apply {
            name = "Native language"
            adUnitId = application.getString(R.string.native_id_language)
            adGsType = AdGsType.NATIVE
        }

        AD_PLACE_NAME_REWARDED.apply {
            name = "Rewarded"
            adUnitId = application.getString(R.string.rewarded_id)
            adGsType = AdGsType.REWARDED
        }

        AD_PLACE_NAME_REWARDED_INTERSTITIAL.apply {
            name = "Rewarded Interstitial"
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