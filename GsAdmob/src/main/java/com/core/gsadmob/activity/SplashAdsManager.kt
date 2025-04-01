package com.core.gsadmob.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.BuildConfig
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.hourglass.Hourglass
import com.core.gscore.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class SplashAdsManager(
    private val activity: AppCompatActivity,
    private val adPlaceName: AdPlaceName? = null,
    private val goToHomeCallback: (() -> Unit),
    private val initMobileAds: (() -> Unit),
    private val adsLoading: ((Boolean) -> Unit)? = null,
    private var delayTime: Long = 3500L
) {

    private var shouldGoToMain = false
    private var isAppPaused = false
    private var isAdLoaded: Boolean = false
    private val alreadyGoHome = AtomicBoolean(false)
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null

    private val timerVirus = object : Hourglass(delayTime, 500) {
        override fun onTimerTick(timeRemaining: Long) {}

        override fun onTimerFinish() {
            if (isAdLoaded) return
            goToHomeCallback()
        }
    }

    private val callBackGoHome = {
        if (timerVirus.isRunning) {
            timerVirus.stopTimer()
        }
        if (isAppPaused) {
            shouldGoToMain = true
        } else {
            if (!alreadyGoHome.getAndSet(true)) {
                if (timerVirus.isRunning) {
                    timerVirus.stopTimer()
                }
                goToHomeCallback.invoke()
            }
        }
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            if (shouldGoToMain) {
                goToHomeCallback.invoke()
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            if (timerVirus.isPaused) timerVirus.resumeTimer()
        }

        override fun onPause(owner: LifecycleOwner) {
            isAppPaused = true
            if (timerVirus.isRunning) timerVirus.pauseTimer()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            AdGsManager.instance.destroyActivity()
            adPlaceName?.let {
                AdGsManager.instance.clearAndRemoveActive(adPlaceName = it)
            }
        }
    }

    init {
        activity.lifecycle.addObserver(lifecycleObserver)
        initAdsForApp()
    }

    private fun initAdsForApp() {
        activity.cmpUtils.isCheckGDPR = false
        // phải check mạng trước nếu không timeout mặc định quá lâu
        NetworkUtils.hasInternetAccessCheck(
            doTask = {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity)
                googleMobileAdsConsentManager?.gatherConsent(activity, onCanShowAds = {
                    initializeMobileAdsSdk()
                    showOpenAdIfNeed()
                }, onDisableAds = {
                    callBackGoHome()
                }, isDebug = BuildConfig.DEBUG)

                if (googleMobileAdsConsentManager?.canRequestAds == true) {
                    initializeMobileAdsSdk()
                }
            },
            doException = {
                initializeMobileAdsSdk()
                callBackGoHome()
            }, context = activity
        )
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        initMobileAds.invoke()
    }

    private fun showOpenAdIfNeed() {
        adPlaceName?.let {
            AdGsManager.instance.registerAndShowAds(adPlaceName = it, adGsListener = object : AdGsListener {
                override fun onAdClose(isFailed: Boolean) {
                    if (isFailed) {
                        isAdLoaded = true
                    } else {
                        callBackGoHome()
                    }
                }

                override fun onAdSuccess() {
                    isAdLoaded = true
                }

                override fun onAdShowing() {
                    isAppPaused = false
                    adsLoading?.invoke(false)
                }
            }, callbackShow = { adShowStatus ->
                when (adShowStatus) {
                    AdShowStatus.CAN_SHOW, AdShowStatus.REQUIRE_LOAD -> {
                        adsLoading?.invoke(true)
                        timerVirus.startTimer()
                    }

                    else -> {
                        isAdLoaded = true
                        callBackGoHome()
                    }
                }
            })
        } ?: {
            callBackGoHome()
        }
    }
}