package com.example.gsadmob

import android.annotation.SuppressLint
import android.app.Activity
import com.core.gsadmob.appopen.AppOpenAdManager
import com.core.gsadmob.appopen.AppResumeAdManager
import com.core.gsadmob.utils.AdGsManager
import com.gs.core.GsApplication
import kotlinx.coroutines.MainScope

class TestApplication : GsApplication() {
    private var appOpenAdManager: AppOpenAdManager? = null
    private var appResumeAdManager: AppResumeAdManager? = null

    init {
        instance = this
    }

    override fun fixWebView(packageName: String) {
        super.fixWebView(getPackageName())
    }

    override fun setupAdMob(isDebug: Boolean) {
        super.setupAdMob(BuildConfig.DEBUG)
    }

    override fun initConfig() {
        AdGsManager.instance.registerCoroutineScope(application = this, coroutineScope = MainScope())
    }

    fun initOpenAds() {
        if (appOpenAdManager == null) {
            appOpenAdManager = AppOpenAdManager().apply {
                loadAd(this@TestApplication)
            }
        } else {
            if (appOpenAdManager?.checkHasAds() == false) {
                appOpenAdManager?.loadAd(this)
            }
        }
    }

    fun setAdOpenListener(appOpenListener: AppOpenAdManager.AdOpenListener?) {
        appOpenAdManager?.adOpenListener = appOpenListener
    }

    fun checkHasAds(): Boolean {
        return appOpenAdManager?.checkHasAds() ?: false
    }

    fun showAdIfAvailable(activity: Activity, isVip: Boolean, callbackSuccess: () -> Unit, callbackFailed: () -> Unit) {
        appOpenAdManager?.showAdIfAvailable(activity, isVip, callbackSuccess, callbackFailed)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TestApplication? = null

        fun applicationContext(): TestApplication {
            return instance as TestApplication
        }
    }
}