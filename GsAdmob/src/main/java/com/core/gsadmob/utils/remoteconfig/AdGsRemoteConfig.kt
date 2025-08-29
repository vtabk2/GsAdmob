package com.core.gsadmob.utils.remoteconfig

import android.app.Application
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

abstract class AdGsRemoteConfig {
    fun initRemoteConfig(application: Application, remoteConfigDefaultsId: Int, isDebug: Boolean = false) {
        AdPlaceNameDefaultConfig.instance.initAdPlaceNameDefaultConfig(application)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (isDebug) {
                0
            } else {
                3600
            }
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set defaults và sử dụng ngay
        remoteConfig.setDefaultsAsync(remoteConfigDefaultsId)
            .addOnCompleteListener {
                // Gọi update ngay với giá trị mặc định
                updateRemoteConfig(remoteConfig)

                // Sau đó mới fetch giá trị mới từ server
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    // Cập nhật lại nếu fetch thành công
                    updateRemoteConfig(remoteConfig)
                }
            }
    }

    open fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig) {}
}