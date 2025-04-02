package com.example.gsadmob.utils.remoteConfig

import android.annotation.SuppressLint
import android.text.TextUtils
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.gson.GsonUtils
import com.core.gsadmob.utils.remoteconfig.AdGsRemoteConfig
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

        log("", "-----------------------")
    }


    private fun setupAdSplashConfig(remoteConfig: FirebaseRemoteConfig) {
        val adSplashConfigJson = remoteConfig.getString(AD_SPLASH_CONFIG)
        if (adSplashConfigJson.isNotEmpty()) {
            try {
                val adSplashConfigList = GsonUtils.parseAdPlaceNameList(adSplashConfigJson)
                // tìm 1 quảng cáo isEnable = true để dùng
                adSplashConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN.adUnitId
                                }
                            }

                            AdGsType.INTERSTITIAL -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_INTERSTITIAL.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // nếu isEnable = false hết thì cho adPlaceNameSplash isEnable = false
                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN)
            }
        } else {
            AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN)
        }
        log("setupAdSplashConfig", AdGsRemoteExtraConfig.Companion.instance.adPlaceNameSplash)
    }

    private fun setupAdAppOpenResume(remoteConfig: FirebaseRemoteConfig) {
        val adAppOpenResumeConfigJson = remoteConfig.getString(AD_APP_OPEN_RESUME_CONFIG)
        if (adAppOpenResumeConfigJson.isNotEmpty()) {
            try {
                val adAppOpenResume = GsonUtils.parseAdPlaceName(adAppOpenResumeConfigJson)
                if (adAppOpenResume.isEnable) {
                    if (adAppOpenResume.isValidate()) {
                        AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume.apply(adAppOpenResume)
                    } else {
                        when (adAppOpenResume.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume.apply(adAppOpenResume).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN_RESUME.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } else {
                    // nếu isEnable = false hết thì cho adPlaceNameLanguage isEnable = false
                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
            }
        } else {
            AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
        }
        log("setupAdAppOpenResume", AdGsRemoteExtraConfig.Companion.instance.adPlaceNameAppOpenResume)
    }

    private fun setupAdHomeConfig(remoteConfig: FirebaseRemoteConfig) {
        val adHomeConfigJson = remoteConfig.getString(AD_HOME_CONFIG)
        if (adHomeConfigJson.isNotEmpty()) {
            try {
                val adHomeConfigList = GsonUtils.parseAdPlaceNameList(adHomeConfigJson)
                // do home dùng nhiều quảng cáo
                adHomeConfigList.forEach {
                    when (it.adGsType) {
                        AdGsType.BANNER -> {
                            if (it.isEnable) {
                                if (it.isValidate()) {
                                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(it)
                                } else {
                                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(it).apply {
                                        adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER.adUnitId
                                    }
                                }
                            } else {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply {
                                    isEnable = false
                                }
                            }
                        }

                        AdGsType.BANNER_COLLAPSIBLE -> {
                            // ưu tiên banner thường trước
                            if (!AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.isEnable) {
                                if (it.isEnable) {
                                    if (it.isValidate()) {
                                        AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(it)
                                    } else {
                                        AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(it).apply {
                                            adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER_COLLAPSIBLE.adUnitId
                                        }
                                    }
                                } else {
                                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply {
                                        isEnable = false
                                    }
                                }
                            }
                        }

                        AdGsType.NATIVE -> {
                            if (it.isEnable) {
                                if (it.isValidate()) {
                                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome.apply(it)
                                } else {
                                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome.apply(it).apply {
                                        adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE.adUnitId
                                    }
                                }
                            } else {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome.apply {
                                    isEnable = false
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
                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER)
                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE)
            }
        } else {
            AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER)
            AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE)
        }
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.Companion.instance.adPlaceNameBannerHome)
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.Companion.instance.adPlaceNameNativeHome)
    }

    private fun setupAdLanguageConfig(remoteConfig: FirebaseRemoteConfig) {
        val adLanguageConfigJson = remoteConfig.getString(AD_LANGUAGE_CONFIG)
        if (adLanguageConfigJson.isNotEmpty()) {
            try {
                val adLanguageConfigList = GsonUtils.parseAdPlaceNameList(adLanguageConfigJson)
                // tìm 1 quảng cáo isEnable = true để dùng
                adLanguageConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.BANNER -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER.adUnitId
                                }
                            }

                            AdGsType.BANNER_COLLAPSIBLE -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_BANNER_COLLAPSIBLE.adUnitId
                                }
                            }

                            AdGsType.NATIVE -> {
                                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE_LANGUAGE.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // nếu isEnable = false hết thì cho adPlaceNameLanguage isEnable = false
                    AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
            }
        } else {
            AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage.apply(AdPlaceNameDefaultConfig.Companion.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
        log("setupAdLanguageConfig", AdGsRemoteExtraConfig.Companion.instance.adPlaceNameLanguage)
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