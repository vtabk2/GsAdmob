package com.example.gsadmob.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.hourglass.Hourglass
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.visible
import com.core.gscore.utils.network.NetworkUtils
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.TestApplication
import com.example.gsadmob.databinding.ActivitySplashBinding
import com.example.gsadmob.ui.activity.TestAdsActivity
import java.util.concurrent.atomic.AtomicBoolean

class SplashActivity : AppCompatActivity() {
    private var shouldGoToMain = false
    private var isAdLoaded: Boolean = false
    private var isAppPaused: Boolean = false
    private var adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_APP_OPEN

    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null

    private var bindingView: ActivitySplashBinding? = null

    private val timerVirus = object : Hourglass(3000, 10) {
        override fun onTimerTick(timeRemaining: Long) {
        }

        override fun onTimerFinish() {
            if (isAdLoaded) return
            goToHome()
        }
    }

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
                    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this@SplashActivity)
                    googleMobileAdsConsentManager?.gatherConsent(this@SplashActivity, onCanShowAds = {
                        initializeMobileAdsSdk()

                        AdGsManager.instance.registerAndShowAds(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
                            override fun onAdClose(isFailed: Boolean) {
                                if (isFailed) {
                                    isAdLoaded = true
                                } else {
                                    goToHome()
                                }
                            }

                            override fun onAdSuccess() {
                                isAdLoaded = true
                            }

                            override fun onAdShowing() {
                                isAppPaused = false
                                bindingView?.apply {
                                    clBlur.gone()
                                }
                            }
                        }, callbackShow = { adShowStatus ->
                            when (adShowStatus) {
                                AdShowStatus.CAN_SHOW, AdShowStatus.REQUIRE_LOAD -> {
                                    bindingView?.apply {
                                        clBlur.visible()
                                    }
                                    timerVirus.startTimer()
                                }

                                else -> {
                                    isAdLoaded = true
                                    goToHome()
                                }
                            }
                        })
                    }, onDisableAds = {
                        goToHome()
                    }, isDebug = BuildConfig.DEBUG)

                    if (googleMobileAdsConsentManager?.canRequestAds == true) {
                        initializeMobileAdsSdk()
                    }
                },
                doException = {
                    initializeMobileAdsSdk()
                    goToHome()
                }, context = this@SplashActivity
            )
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        TestApplication.applicationContext().initMobileAds()
    }

    private fun startNewActivityHome() {
        val intent = Intent(this@SplashActivity, TestAdsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun goToHome() {
        if (timerVirus.isRunning) {
            timerVirus.stopTimer()
        }
        if (isAppPaused) {
            shouldGoToMain = true
            return
        }
        if (isTaskRoot) {
            startNewActivityHome()
        } else {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        isAppPaused = true
        if (timerVirus.isRunning) timerVirus.pauseTimer()
    }

    override fun onResume() {
        super.onResume()
        if (timerVirus.isPaused) timerVirus.resumeTimer()
    }

    override fun onStart() {
        super.onStart()
        if (shouldGoToMain) {
            startNewActivityHome()
        }
    }

    override fun onDestroy() {
        AdGsManager.instance.destroyActivity()
        AdGsManager.instance.clearAndRemoveActive(adPlaceName = adPlaceName)
        super.onDestroy()
    }
}