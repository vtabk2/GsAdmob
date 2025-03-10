package com.example.gsadmob

import android.annotation.SuppressLint
import com.core.gsadmob.utils.AdGsManager
import com.gs.core.GsApplication

class TestApplication : GsApplication() {

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
        AdGsManager.instance.registerCoroutineScope(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TestApplication? = null

        fun applicationContext(): TestApplication {
            return instance as TestApplication
        }
    }
}