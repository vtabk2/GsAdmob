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

    /**
     * Khởi tạo VipPreferences
     */
    fun initVipPreferences(context: Context, applicationId: String) {
        prefs = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE)
    }

    /**
     * Bắt sự kiện thay đổi vip dựa vào keyVipList truyền vào, nếu không thì sẽ dùng danh sách mặc định
     */
    fun getVipChangeFlow(keyVipList: MutableList<String> = defaultKeyVipList) = callbackFlow<Boolean> {
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

    /**
     * Khi dùng key mới thì dùng hàm này để tải
     */
    fun load(key: String, valueDefault: Boolean = false): Boolean {
        return prefs?.getBoolean(key, valueDefault) ?: valueDefault
    }

    /**
     * Khi dùng key mới thì dùng hàm này để lưu
     */
    fun save(key: String, value: Boolean) {
        prefs?.edit()?.putBoolean(key, value)?.apply()
    }

    /**
     * Đây là biến mặc định lưu vip mua 1 lần trọn đời (kiểu thanh toán INAPP)
     */
    var isPro: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO, value) }
        }

    /**
     * Đây là biến mặc định lưu vip mua vip theo năm (kiểu thanh toán SUB)
     */
    var isProByYear: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO_BY_YEAR, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO_BY_YEAR, value) }
        }

    /**
     * Đây là biến mặc định lưu vip mua theo tháng (kiểu thanh toán SUB)
     */
    var isProByMonth: Boolean
        get() = prefs?.getBoolean(KEY_IS_PRO_BY_MONTH, false) == true
        set(value) {
            prefs?.edit { putBoolean(KEY_IS_PRO_BY_MONTH, value) }
        }

    /**
     * Kiểm tra xem trong danh sách truyền vào có vip nào được kích hoạt không
     */
    fun isFullVersion(keyVipList: MutableList<String> = defaultKeyVipList): Boolean {
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

        /**
         * Danh sách keyVipList mặc định sẽ gồm 3 loại
         */
        val defaultKeyVipList = mutableListOf(KEY_IS_PRO, KEY_IS_PRO_BY_YEAR, KEY_IS_PRO_BY_MONTH)

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