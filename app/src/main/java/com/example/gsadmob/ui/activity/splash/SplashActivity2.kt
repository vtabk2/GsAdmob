package com.example.gsadmob.ui.activity.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.core.gscore.utils.extensions.visible
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ActivitySplashBinding
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.gs.core.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class SplashActivity2 : AppCompatActivity() {
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null

    private var bindingView: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        bindingView = ActivitySplashBinding.inflate(layoutInflater)

        bindingView?.apply {
            setContentView(root)

            tvMessageSplash.text = String.format("%s %s", getString(R.string.app_name), getString(R.string.text_is_running))

            cmpUtils.isCheckGDPR = false
            // phải check mạng trước nếu không timeout mặc định quá lâu
            NetworkUtils.hasInternetAccessCheck(
                doTask = {
                    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this@SplashActivity2)
                    googleMobileAdsConsentManager?.gatherConsent(this@SplashActivity2, onCanShowAds = {

                    }, onDisableAds = {

                    }, isDebug = BuildConfig.DEBUG)
                },
                doException = {

                }, context = this@SplashActivity2
            )
        }
    }

    private fun delayShowAds() {
        bindingView?.apply {
            clBlur.visible()
        }
    }
}