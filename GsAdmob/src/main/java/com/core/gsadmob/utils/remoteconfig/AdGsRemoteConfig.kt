package com.core.gsadmob.utils.remoteconfig

import android.app.Application
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

abstract class AdGsRemoteConfig {
    fun initRemoteConfig(application: Application, remoteConfigDefaultsId: Int) {
        AdPlaceNameDefaultConfig.instance.initAdPlaceNameDefaultConfig(application)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
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