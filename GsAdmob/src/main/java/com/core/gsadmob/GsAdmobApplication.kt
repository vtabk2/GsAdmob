package com.core.gsadmob

import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.core.gsadmob.utils.extensions.getAndroidId
import com.core.gsadmob.utils.extensions.md5
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class GsAdmobApplication : MultiDexApplication() {
    private val deviceTestList = mutableListOf<String>()

    override fun onCreate() {
        super.onCreate()

        fixWebView("")

        setupAdMob(isDebug = false)

        setupLingver()

        initConfig()
    }

    open fun fixWebView(packageName: String) {
        if (TextUtils.isEmpty(packageName)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val process = getProcessName()
            if (packageName != process) WebView.setDataDirectorySuffix(process)
        }
    }

    open fun setupAdMob(isDebug: Boolean) {
        if (isDebug) {
            deviceTestList.add(md5(getAndroidId(this)).uppercase())

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(deviceTestList)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }

    open fun setupLingver() {}

    open fun initConfig() {}

    fun initMobileAds() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@GsAdmobApplication) {}
        }
    }
}