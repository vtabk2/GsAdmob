package com.example.gsadmob

import android.annotation.SuppressLint
import android.util.Log
import com.core.gsadmob.utils.AdGsRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class RemoteConfig : AdGsRemoteConfig() {
    override fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        super.updateRemoteConfig(remoteConfig)
        val adSplashConfigJson = remoteConfig.getString(AD_SPLASH_CONFIG)
        Log.d("GsAdmobLib", "RemoteConfig_updateRemoteConfig: adSplashConfigJson = $adSplashConfigJson")
        if (adSplashConfigJson.isNotEmpty()) {
            // update AdPlaceNameConfig
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