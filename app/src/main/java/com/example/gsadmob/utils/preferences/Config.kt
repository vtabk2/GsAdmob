package com.example.gsadmob.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.gsadmob.BuildConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class Config(val context: Context) {
    private val prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    private val keyVipList = mutableListOf(KEY_IS_PRO, KEY_IS_PRO_BY_YEAR, KEY_IS_PRO_BY_MONTH)

    fun getVipChangeFlow() = callbackFlow<Boolean> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (keyVipList.contains(key)) {
                trySend(isFullVersion())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        for (keyVip in keyVipList) {
            if (prefs.contains(keyVip)) {
                send(isFullVersion()) // if you want to emit an initial pre-existing value
                break
            }
        }
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.buffer(Channel.UNLIMITED) // so trySend never fails

    var isPro: Boolean
        get() = prefs.getBoolean(KEY_IS_PRO, false)
        set(value) {
            prefs.edit { putBoolean(KEY_IS_PRO, value) }
        }

    var isProByYear: Boolean
        get() = prefs.getBoolean(KEY_IS_PRO_BY_YEAR, false)
        set(value) {
            prefs.edit { putBoolean(KEY_IS_PRO_BY_YEAR, value) }
        }

    var isProByMonth: Boolean
        get() = prefs.getBoolean(KEY_IS_PRO_BY_MONTH, false)
        set(value) {
            prefs.edit { putBoolean(KEY_IS_PRO_BY_MONTH, value) }
        }

    fun isFullVersion(): Boolean {
        return isPro || isProByYear || isProByMonth
    }

    companion object {

        fun newInstance(context: Context) = Config(context)

        private const val KEY_IS_PRO = "KEY_IS_PRO"

        private const val KEY_IS_PRO_BY_YEAR = "KEY_IS_PRO_BY_YEAR"

        private const val KEY_IS_PRO_BY_MONTH = "KEY_IS_PRO_BY_MONTH"
    }
}