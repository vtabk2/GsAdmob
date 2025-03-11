package com.example.gsadmob

import com.core.gsadmob.utils.AdGsManager
import com.gs.core.GsApplication
import kotlinx.coroutines.MainScope

class TestApplication : GsApplication() {

    override fun fixWebView(packageName: String) {
        super.fixWebView(getPackageName())
    }

    override fun setupAdMob(isDebug: Boolean) {
        super.setupAdMob(BuildConfig.DEBUG)
    }

    override fun initConfig() {
        AdGsManager.instance.registerCoroutineScope(application = this, coroutineScope = MainScope())
    }
}