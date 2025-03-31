package com.example.gsadmob

import android.annotation.SuppressLint
import android.text.TextUtils
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.AdGsRemoteConfig
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.GsonUtils
import com.core.gsadmob.utils.extensions.log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class RemoteConfig : AdGsRemoteConfig() {
    override fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        super.updateRemoteConfig(remoteConfig)

        // cấu hình quảng cáo màn hình splash
        setupAdSplashConfig(remoteConfig)

        // cấu hình quảng cáo app open resume
        setupAdAppOpenResume(remoteConfig)
    }


    private fun setupAdSplashConfig(remoteConfig: FirebaseRemoteConfig) {
        val adSplashConfigJson = remoteConfig.getString(AD_SPLASH_CONFIG)
        if (adSplashConfigJson.isNotEmpty()) {
            try {
                val adSplashConfigList = GsonUtils.parseAdPlaceNameList(adSplashConfigJson)
                adSplashConfigList.find { it.isEnable && it.isValidate() }?.let {
                    AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(it)
                } ?: run {
                    // không có quảng cáo ở màn hình splash
                    AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(AdPlaceName())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                log("setupAdSplashConfig.error", e)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN)
        }
        log("setupAdSplashConfig", AdGsRemoteExtraConfig.instance.adPlaceNameSplash)
    }

    private fun setupAdAppOpenResume(remoteConfig: FirebaseRemoteConfig) {
        val adAppOpenResumeConfigJson = remoteConfig.getString(AD_APP_OPEN_RESUME_CONFIG)
        if (adAppOpenResumeConfigJson.isNotEmpty()) {
            try {
                val adAppOpenResumeConfigList = GsonUtils.parseAdPlaceNameList(adAppOpenResumeConfigJson)
                adAppOpenResumeConfigList.find { it.isEnable && it.isValidate() }?.let {
                    AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(it)
                } ?: run {
                    // không có quảng cáo ở màn hình splash
                    AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(AdPlaceName())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                log("setupAdSplashConfig.error", e)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
        }
        log("setupAdAppOpenResume", AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume)
    }

    fun AdPlaceName.isValidate(): Boolean {
        return TextUtils.isEmpty(name) && TextUtils.isEmpty(adUnitId)
    }

    companion object {
        const val AD_SPLASH_CONFIG = "ad_splash_config"
        const val AD_APP_OPEN_RESUME_CONFIG = "ad_app_open_resume_config"

        @SuppressLint("StaticFieldLeak")
        private var singleton: RemoteConfig? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: RemoteConfig
            get() {
                if (singleton == null) {
                    singleton = RemoteConfig()
                }
                return singleton as RemoteConfig
            }
    }
}