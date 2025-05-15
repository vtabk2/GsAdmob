package com.core.gsadmob.utils

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.hourglass.Hourglass
import com.core.gscore.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class AdGsSplashManager(
    private val activity: AppCompatActivity,
    private var adPlaceName: AdPlaceName? = null,
    private val onRetryAdPlaceNameListener: OnRetryAdPlaceNameListener? = null,
    private val goToHomeCallback: (() -> Unit),
    private val initMobileAds: (() -> Unit),
    private val adsLoading: ((Boolean) -> Unit)? = null,
    private val delayTime: Long = 3500L,
    private val delayRetry: Long = 500,
    private var isDebug: Boolean = false
) {

    private var shouldGoToMain = false
    private var isAppPaused = false
    private var isAdLoaded: Boolean = false
    private var isRetry: Boolean = false
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
            adPlaceName?.let {
                AdGsManager.instance.clearAndRemoveListener(adPlaceName = it)
            }
        }
    }

    init {
        activity.lifecycle.addObserver(lifecycleObserver)
        initAdsForApp()
    }

    private fun initAdsForApp() {
        adPlaceName?.let {
            if (!it.isValidate() && !isRetry) {
                isRetry = true
                Handler(Looper.getMainLooper()).postDelayed({
                    onRetryAdPlaceNameListener?.getAdPlaceName()?.let {
                        adPlaceName = it
                        initAdsForApp()
                    }
                }, delayRetry)
                return
            }
        }
        activity.cmpUtils.isCheckGDPR = false

        val timeout = if (isRetry) {
            delayTime - delayRetry
        } else {
            delayTime
        }.toInt()

        // phải check mạng trước nếu không timeout mặc định quá lâu
        NetworkUtils.hasInternetAccessCheck(
            doTask = {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity)
                googleMobileAdsConsentManager?.gatherConsent(activity, onCanShowAds = {
                    initializeMobileAdsSdk()
                    showOpenAdIfNeed()
                }, onDisableAds = {
                    callBackGoHome()
                }, isDebug = isDebug)

                if (googleMobileAdsConsentManager?.canRequestAds == true) {
                    initializeMobileAdsSdk()
                }
            },
            doException = {
                initializeMobileAdsSdk()
                callBackGoHome()
            }, context = activity, timeout = timeout
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
                        isAdLoaded = false
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
                log("AdGsSplashManager_adShowStatus", adShowStatus)
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

    interface OnRetryAdPlaceNameListener {
        fun getAdPlaceName(): AdPlaceName
    }
}