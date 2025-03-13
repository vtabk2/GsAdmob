package com.example.gsadmob.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.core.gsadmob.callback.AdGsListener
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.visible
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.TestApplication
import com.example.gsadmob.ui.activity.TestAdsActivity
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.gs.core.ui.view.hourglass.Hourglass
import com.gs.core.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class SplashActivity : AppCompatActivity() {
    private var shouldGoToMain = false
    private var isLoaded: Boolean = false
    private var isAppPaused: Boolean = false
    private var timerDelay: Hourglass? = null

    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null

    private val timerVirus = object : Hourglass(3000, 10) {
        override fun onTimerTick(timeRemaining: Long) {
        }

        override fun onTimerFinish() {
            if (isLoaded) return
            goToHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<AppCompatTextView>(R.id.tvMessageSplash).text = String.format("%s %s", getString(R.string.app_name), getString(R.string.text_is_running))
        val clBlur = findViewById<ConstraintLayout>(R.id.clBlur)

        cmpUtils.isCheckGDPR = false
        // phải check mạng trước nếu không timeout mặc định quá lâu
        NetworkUtils.hasInternetAccessCheck(doTask = {
            googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                initializeMobileAdsSdk()
                if (TestApplication.applicationContext().checkHasAds()) {
                    delayShowAds(clBlur)
                } else {
                    TestApplication.applicationContext().setAdOpenListener(object : AdGsListener {
                        override fun onAdSuccess() {
                            isLoaded = true
                            delayShowAds(clBlur)
                        }

                        override fun onAdClose(isFailed: Boolean) {
                            if (isFailed) {
                                isLoaded = true
                                goToHome()
                            }
                        }
                    })
                    TestApplication.applicationContext().initOpenAds()
                    timerVirus.startTimer()
                }
            }, onDisableAds = {
                goToHome()
            }, isDebug = BuildConfig.DEBUG)
            if (googleMobileAdsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
            }
        }, doException = {
            initializeMobileAdsSdk()
            goToHome()
        }, context = this)
    }

    fun delayShowAds(clBlur: ConstraintLayout) {
        clBlur.visible()
        timerDelay = object : Hourglass(1000, 500) {
            override fun onTimerTick(timeRemaining: Long) {
                // nothing
            }

            override fun onTimerFinish() {
                TestApplication.applicationContext().showAdIfAvailable(activity = this@SplashActivity, isVip = false, callbackSuccess = {
                    isAppPaused = false
                }, callbackFailed = {
                    clBlur.gone()
                    goToHome()
                })
            }
        }
        timerDelay?.startTimer()
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        TestApplication.applicationContext().initMobileAds()
        TestApplication.applicationContext().initOpenAds()
    }

    private fun startNewActivityHome() {
        val intent = Intent(this@SplashActivity, TestAdsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun goToHome() {
        TestApplication.applicationContext().setAdOpenListener(null)
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
        if (timerDelay?.isRunning == true) timerDelay?.pauseTimer()
    }

    override fun onResume() {
        super.onResume()
        if (timerVirus.isPaused) timerVirus.resumeTimer()
        if (timerDelay?.isPaused == true) timerDelay?.resumeTimer()
    }

    override fun onStart() {
        super.onStart()
        if (shouldGoToMain) {
            startNewActivityHome()
        }
    }
}