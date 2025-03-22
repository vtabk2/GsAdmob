package com.core.gsadmob.utils.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class VipPreferences {
    private var prefs: SharedPreferences? = null

    fun initVipPreferences(context: Context, applicationId: String) {
        prefs = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE)
    }

    fun getVipChangeFlow(keyVipList: MutableList<String>) = callbackFlow<Boolean> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (keyVipList.contains(key)) {
                trySend(isFullVersion(keyVipList))
            }
        }
        prefs?.registerOnSharedPreferenceChangeListener(listener)
        for (keyVip in keyVipList) {
            if (prefs?.contains(keyVip) == true) {
                send(isFullVersion(keyVipList)) // if you want to emit an initial pre-existing value
                break
            }
        }
        awaitClose { prefs?.unregisterOnSharedPreferenceChangeListener(listener) }
    }.buffer(Channel.UNLIMITED) // so trySend never fails

    fun load(key: String, valueDefault: Boolean = false): Boolean {
        return prefs?.getBoolean(key, valueDefault) ?: valueDefault
    }

    fun save(key: String, value: Boolean) {
        prefs?.edit()?.putBoolean(key, value)?.apply()
    }

    var isPro: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO, value) }
        }

    var isProByYear: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO_BY_YEAR, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO_BY_YEAR, value) }
        }

    var isProByMonth: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO_BY_MONTH, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO_BY_MONTH, value) }
        }

    fun isFullVersion(keyVipList: MutableList<String>): Boolean {
        var isVip = false
        for (keyVip in keyVipList) {
            if (load(keyVip, false)) {
                isVip = true
            }
        }
        return isVip
    }

    companion object {
        const val KEY_IS_PRO = "KEY_IS_PRO"

        const val KEY_IS_PRO_BY_YEAR = "KEY_IS_PRO_BY_YEAR"

        const val KEY_IS_PRO_BY_MONTH = "KEY_IS_PRO_BY_MONTH"

        @SuppressLint("StaticFieldLeak")
        private var singleton: VipPreferences? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: VipPreferences
            get() {
                if (singleton == null) {
                    singleton = VipPreferences()
                }
                return singleton as VipPreferences
            }
    }
}