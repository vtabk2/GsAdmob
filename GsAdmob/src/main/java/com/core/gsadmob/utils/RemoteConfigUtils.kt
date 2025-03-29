package com.core.gsadmob.utils

import android.app.Activity
import com.core.gsadmob.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

class RemoteConfigUtils {

    fun initRemoteConfig(activity: Activity) {
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
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        // [END set_default_values]

        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate().addOnCompleteListener(activity) {
            updateRemoteConfig()
        }
        // [END fetch_config_with_callback]

        // [START add_config_update_listener]
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                updateRemoteConfig()
            }

            override fun onError(error: FirebaseRemoteConfigException) {}
        })
    }

    private fun updateRemoteConfig() {
    }
}