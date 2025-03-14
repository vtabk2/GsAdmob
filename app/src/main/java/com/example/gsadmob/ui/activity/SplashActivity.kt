package com.example.gsadmob.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.core.gscore.utils.network.NetworkUtils
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SplashActivity : AppCompatActivity() {
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    override fun onCreate(savedInstanceState: Bundle?) {

        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        cmpUtils.isCheckGDPR = false
        // phải check mạng trước nếu không timeout mặc định quá lâu
        NetworkUtils.hasInternetAccessCheck(doTask = {
            googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            googleMobileAdsConsentManager.gatherConsent(this, onCanShowAds = {
                initializeMobileAdsSdk()
                goToHome()
            }, onDisableAds = {
                goToHome()
            }, isDebug = BuildConfig.DEBUG)
            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }, doException = {
            when (it) {
                NetworkUtils.NetworkError.TURN_OFF -> {
                    initializeMobileAdsSdk()
                    goToHome()
                }

                else -> {
                    initializeMobileAdsSdk()
                    goToHome()
                }
            }
        }, context = this)
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        initMobileAds()
    }

    private fun initMobileAds() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@SplashActivity) {}
        }
    }

    private fun goToHome() {
        if (isTaskRoot) {
            startNewActivityHome()
        } else {
            finish()
        }
    }

    private fun startNewActivityHome() {
        val intent = Intent(this@SplashActivity, TestAdsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}