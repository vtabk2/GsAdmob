package com.core.gsadmob.utils.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

abstract class AdGsRemoteConfig {
    fun initRemoteConfig(remoteConfigDefaultsId: Int) {
        // [START get_remote_config_instance]
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        // [END get_remote_config_instance]

        // [START enable_dev_mode]
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        // [END enable_dev_mode]

        // [START set_default_values]
        remoteConfig.setDefaultsAsync(remoteConfigDefaultsId)
        // [END set_default_values]

        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            updateRemoteConfig(remoteConfig)
        }
        // [END fetch_config_with_callback]
    }

    open fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig) {}
}