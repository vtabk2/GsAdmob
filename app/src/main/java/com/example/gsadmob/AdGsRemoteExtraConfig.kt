package com.example.gsadmob

import android.annotation.SuppressLint
import com.core.gsadmob.model.AdPlaceName

class AdGsRemoteExtraConfig {
    val adPlaceNameSplash = AdPlaceName()
    val adPlaceNameAppOpenResume = AdPlaceName()
    val adPlaceNameBannerHome = AdPlaceName()
    val adPlaceNameNativeHome = AdPlaceName()
    val adPlaceNameLanguage = AdPlaceName()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var singleton: AdGsRemoteExtraConfig? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: AdGsRemoteExtraConfig
            get() {
                if (singleton == null) {
                    singleton = AdGsRemoteExtraConfig()
                }
                return singleton as AdGsRemoteExtraConfig
            }
    }
}