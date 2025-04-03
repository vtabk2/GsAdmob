package com.example.gsadmob.utils.remoteconfig

import android.annotation.SuppressLint
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName

class AdGsRemoteExtraConfig {
    val adPlaceNameSplash = AdPlaceName(adGsType = AdGsType.APP_OPEN)
    val adPlaceNameAppOpenResume = AdPlaceName(adGsType = AdGsType.APP_OPEN)
    val adPlaceNameBannerHome = AdPlaceName(adGsType = AdGsType.BANNER)
    val adPlaceNameNativeHome = AdPlaceName(adGsType = AdGsType.NATIVE)
    val adPlaceNameLanguage = AdPlaceName(adGsType = AdGsType.NATIVE)

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