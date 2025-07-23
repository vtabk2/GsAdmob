package com.example.gsadmob.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdGsSplashManager
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.TestApplication
import com.example.gsadmob.databinding.ActivitySplashBinding
import com.example.gsadmob.ui.activity.home.HomeActivity
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig

class SplashActivity : AppCompatActivity() {
    private var bindingView: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        bindingView = ActivitySplashBinding.inflate(layoutInflater)
        bindingView?.apply {
            setContentView(root)
            tvMessageSplash.text = String.format("%s %s", getString(R.string.app_name), getString(R.string.text_is_running))
        }

        AdGsSplashManager(
            activity = this@SplashActivity,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameSplash,
            onRetryAdPlaceNameListener = object : AdGsSplashManager.OnRetryAdPlaceNameListener {
                override fun getAdPlaceName(): AdPlaceName {
                    return AdGsRemoteExtraConfig.instance.adPlaceNameSplash
                }
            },
            goToHomeCallback = {
                goToHome()
            }, initMobileAds = {
                TestApplication.applicationContext().initMobileAds()
            }, adsLoading = {
                bindingView?.clBlur?.isVisible = it
            }, isDebug = BuildConfig.DEBUG
        )
    }

    private fun goToHome() {
        if (isTaskRoot) {
            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        AdGsManager.instance.clearAndRemoveListener(adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameSplash, fromAutoDestroy = false)
        finish()
    }
}