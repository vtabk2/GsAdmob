package com.example.gsadmob.utils.remoteconfig

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
                        AdGsRemoteExtraConfig.instance.adPlaceNameSplash.update(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.update(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN.adUnitId
                                }
                            }

                            AdGsType.INTERSTITIAL -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.update(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // nếu isEnable = false hết thì cho adPlaceNameSplash isEnable = false
                    AdGsRemoteExtraConfig.instance.adPlaceNameSplash.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameSplash.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameSplash.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN)
        }
        log("setupAdSplashConfig", AdGsRemoteExtraConfig.instance.adPlaceNameSplash)
    }

    private fun setupAdAppOpenResume(remoteConfig: FirebaseRemoteConfig) {
        val adAppOpenResumeConfigJson = remoteConfig.getString(AD_APP_OPEN_RESUME_CONFIG)
        if (adAppOpenResumeConfigJson.isNotEmpty()) {
            try {
                val adAppOpenResume = GsonUtils.parseAdPlaceName(adAppOpenResumeConfigJson)
                if (adAppOpenResume.isEnable) {
                    if (adAppOpenResume.isValidate()) {
                        AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.update(adAppOpenResume)
                    } else {
                        when (adAppOpenResume.adGsType) {
                            AdGsType.APP_OPEN -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.update(adAppOpenResume).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } else {
                    // nếu isEnable = false hết thì cho adPlaceNameLanguage isEnable = false
                    AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
        }
        log("setupAdAppOpenResume", AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume)
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
                                    AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(it)
                                } else {
                                    AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(it).apply {
                                        adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER_HOME.adUnitId
                                    }
                                }
                            } else {
                                AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply {
                                    isEnable = false
                                }
                            }
                        }

                        AdGsType.BANNER_COLLAPSIBLE -> {
                            // ưu tiên banner thường trước
                            if (!AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.isEnable) {
                                if (it.isEnable) {
                                    if (it.isValidate()) {
                                        AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(it)
                                    } else {
                                        AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(it).apply {
                                            adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER_COLLAPSIBLE.adUnitId
                                        }
                                    }
                                } else {
                                    AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.apply {
                                        isEnable = false
                                    }
                                }
                            }
                        }

                        AdGsType.NATIVE -> {
                            if (it.isEnable) {
                                if (it.isValidate()) {
                                    AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.update(it)
                                } else {
                                    AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.update(it).apply {
                                        adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE.adUnitId
                                    }
                                }
                            } else {
                                AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.apply {
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
                AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER_HOME)
                AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER_HOME)
            AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE)
        }
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome)
        log("setupAdHomeConfig", AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome)
    }

    private fun setupAdLanguageConfig(remoteConfig: FirebaseRemoteConfig) {
        val adLanguageConfigJson = remoteConfig.getString(AD_LANGUAGE_CONFIG)
        if (adLanguageConfigJson.isNotEmpty()) {
            try {
                val adLanguageConfigList = GsonUtils.parseAdPlaceNameList(adLanguageConfigJson)
                // tìm 1 quảng cáo isEnable = true để dùng
                adLanguageConfigList.find { it.isEnable }?.let {
                    if (it.isValidate()) {
                        AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(it)
                    } else {
                        when (it.adGsType) {
                            AdGsType.BANNER -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER.adUnitId
                                }
                            }

                            AdGsType.BANNER_COLLAPSIBLE -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_BANNER_COLLAPSIBLE.adUnitId
                                }
                            }

                            AdGsType.NATIVE -> {
                                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(it).apply {
                                    adUnitId = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE.adUnitId
                                }
                            }

                            else -> {
                                // nothing
                            }
                        }
                    }
                } ?: run {
                    // nếu isEnable = false hết thì cho adPlaceNameLanguage isEnable = false
                    AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.apply {
                        isEnable = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // nếu json lỗi thì dùng mặc định
                AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
            }
        } else {
            AdGsRemoteExtraConfig.instance.adPlaceNameLanguage.update(AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
        log("setupAdLanguageConfig", AdGsRemoteExtraConfig.instance.adPlaceNameLanguage)
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