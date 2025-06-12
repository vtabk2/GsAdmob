package com.example.gsadmob

import android.annotation.SuppressLint
import com.core.gsadmob.GsAdmobApplication
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.ui.activity.splash.SplashActivity
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
import com.example.gsadmob.utils.remoteconfig.RemoteConfig
import kotlinx.coroutines.MainScope

class TestApplication : GsAdmobApplication() {

    private val mainScope = MainScope()

    init {
        instance = this
    }

    override fun fixWebView(packageName: String) {
        super.fixWebView(getPackageName())
    }

    override fun setupDeviceTest(isDebug: Boolean) {
        super.setupDeviceTest(BuildConfig.DEBUG)
    }

    override fun registerAdGsManager() {
        super.registerAdGsManager()

        RemoteConfig.instance.initRemoteConfig(
            application = this,
            remoteConfigDefaultsId = R.xml.remote_config_defaults,
            isDebug = BuildConfig.DEBUG
        )

        AdGsManager.instance.registerCoroutineScope(
            application = this,
            coroutineScope = mainScope,
            applicationId = BuildConfig.APPLICATION_ID,
            keyVipList = VipPreferences.defaultKeyVipList,
            adPlaceNameAppOpenResume = AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume,
            canShowAppOpenResume = { currentActivity ->
                canShowAppOpenResume && currentActivity !is SplashActivity
            },
            requireScreenAdLoading = true,
            showLog = BuildConfig.DEBUG
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TestApplication? = null

        fun applicationContext(): TestApplication {
            return instance as TestApplication
        }
    }
}