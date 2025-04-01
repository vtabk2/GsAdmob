package com.example.gsadmob

import android.annotation.SuppressLint
import android.text.TextUtils
import com.core.gsadmob.model.AdGsType
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

        // cấu hình quảng cáo màn hình home
        setupAdHomeConfig(remoteConfig)

        // cấu hình quảng cáo language
        setupAdLanguageConfig(remoteConfig)
    }


    private fun setupAdSplashConfig(remoteConfig: FirebaseRemoteConfig) {
        val adSplashConfigJson = remoteConfig.getString(AD_SPLASH_CONFIG)
        if (adSplashConfigJson.isNotEmpty()) {
            try {
                val adSplashConfigList = GsonUtils.parseAdPlaceNameList(adSplashConfigJson)
                adSplashConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN.adUnitId
                                }
                            }

                            AdGsType.INTERSTITIAL -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_INTERSTITIAL.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // không có quảng cáo ở màn hình splash
                    AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(AdPlaceName())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN)
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
                adAppOpenResumeConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // không có quảng cáo ở màn hình splash
                    AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(AdPlaceName())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
        }
        log("setupAdAppOpenResume", AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume)
    }

    private fun setupAdLanguageConfig(remoteConfig: FirebaseRemoteConfig) {
        val adLanguageConfigJson = remoteConfig.getString(AD_LANGUAGE_CONFIG)
        if (adLanguageConfigJson.isNotEmpty()) {
            try {
                val adLanguageConfigList = GsonUtils.parseAdPlaceNameList(adLanguageConfigJson)
                adLanguageConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.BANNER -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER.adUnitId
                                }
                            }

                            AdGsType.BANNER_COLLAPSIBLE -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER_COLLAPSIBLE.adUnitId
                                }
                            }

                            AdGsType.NATIVE -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // không có quảng cáo ở màn hình splash
                    AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(AdPlaceName())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
        log("setupAdLanguageConfig", AdGsRemoteExtraConfig.instance.adPlaceNameLanguage)
    }

    private fun setupAdHomeConfig(remoteConfig: FirebaseRemoteConfig) {
        val adHomeConfigJson = remoteConfig.getString(AD_HOME_CONFIG)
        if (adHomeConfigJson.isNotEmpty()) {
            try {
                val adHomeConfigList = GsonUtils.parseAdPlaceNameList(adHomeConfigJson)
                adHomeConfigList.filter { it.isEnable }.forEach {
                    when (it.adGsType) {
                        AdGsType.BANNER -> {
                            if (it.isValidate()) {
                                AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply(it)
                            } else {
                                AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER.adUnitId
                                }
                            }
                        }

                        AdGsType.NATIVE -> {
                            if (it.isValidate()) {
                                AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.apply(it)
                            } else {
                                AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.apply(it).apply {
                                    adUnitId = AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE.adUnitId
                                }
                            }
                        }

                        else -> {
                            // nothing
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER)
                AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER)
            AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.apply(AdPlaceNameConfig.instance.AD_PLACE_NAME_NATIVE)
        }
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome)
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome)
    }

    private fun AdPlaceName.isValidate(): Boolean {
        return !TextUtils.isEmpty(adUnitId)
    }

    companion object {
        const val AD_SPLASH_CONFIG = "ad_splash_config"
        const val AD_APP_OPEN_RESUME_CONFIG = "ad_app_open_resume_config"
        const val AD_LANGUAGE_CONFIG = "ad_language_config"
        const val AD_HOME_CONFIG = "ad_home_config"

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