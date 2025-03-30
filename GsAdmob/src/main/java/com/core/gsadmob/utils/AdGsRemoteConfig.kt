package com.core.gsadmob.utils

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

abstract class AdGsRemoteConfig {
    fun initRemoteConfig(activity: Activity, remoteConfigDefaultsId: Int) {
        Log.d("GsAdmob", "AdGsRemoteConfig_initRemoteConfig: ")
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
        remoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                updateRemoteConfig(remoteConfig, "fetchAndActivate")
            } else {
                // nothing
            }
        }
        // [END fetch_config_with_callback]

        // [START add_config_update_listener]
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("GsAdmob", "AdGsRemoteConfig_onUpdate: updatedKeys = " + configUpdate.updatedKeys)
                updateRemoteConfig(remoteConfig, "onUpdate")
            }

            override fun onError(error: FirebaseRemoteConfigException) {}
        })
    }

    open fun updateRemoteConfig(remoteConfig: FirebaseRemoteConfig, from: String) {
        Log.d("GsAdmob", "RemoteConfigUtils_updateRemoteConfig: from = $from")
    }
}