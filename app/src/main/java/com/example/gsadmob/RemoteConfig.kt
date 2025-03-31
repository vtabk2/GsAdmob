package com.example.gsadmob

import android.annotation.SuppressLint
import com.core.gsadmob.utils.AdGsRemoteConfig
import com.core.gsadmob.utils.GsonUtils
import com.core.gsadmob.utils.extensions.log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class RemoteConfig : AdGsRemoteConfig() {
    override fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        super.updateRemoteConfig(remoteConfig)
        val adSplashConfigJson = remoteConfig.getString(AD_SPLASH_CONFIG)
        if (adSplashConfigJson.isNotEmpty()) {
            // update AdPlaceNameConfig
            try {
                val adSplashConfigList = GsonUtils.parseAdPlaceNameList(adSplashConfigJson)
                adSplashConfigList.forEach {
                    log("RemoteConfig_updateRemoteConfig_adPlaceName", it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                log("RemoteConfig_updateRemoteConfig.error", e)
            }
        } else {
            // nothing
        }
    }

    companion object {
        const val AD_SPLASH_CONFIG = "ad_splash_config"

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