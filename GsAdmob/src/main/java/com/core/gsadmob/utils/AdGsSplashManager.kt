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
    private val timeout: Long = 3500L,
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

    private val timerVirus = object : Hourglass(timeout, 500) {
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
            destroy(fromAutoDestroy = true)
        }
    }

    init {
        activity.lifecycle.addObserver(lifecycleObserver)
        initAdsForApp()
    }

    private fun initAdsForApp() {
        adPlaceName?.let {
            if (!it.isValidateEnable() && !isRetry) {
                isRetry = true
                Handler(Looper.getMainLooper()).postDelayed({
                    onRetryAdPlaceNameListener?.getAdPlaceName()?.let { name ->
                        adPlaceName = name
                        initAdsForApp()
                    }
                }, delayRetry)
                return
            }
        }
        activity.cmpUtils.isCheckGDPR = false

        // thời gian còn lại
        val subTimeout = if (isRetry) {
            timeout - delayRetry
        } else {
            timeout
        }

        log("AdGsSplashManager_initAdsForApp_subTimeout", subTimeout)

        if (NetworkUtils.isInternetAvailable(context = activity)) {
            googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity)
            googleMobileAdsConsentManager?.gatherConsent(activity, onCanShowAds = {
                initializeMobileAdsSdk()
                showOpenAdIfNeed()
            }, onDisableAds = {
                callBackGoHome()
            }, isDebug = isDebug, timeout = subTimeout)

            if (googleMobileAdsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
            }
        } else {
            initializeMobileAdsSdk()
            callBackGoHome()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        initMobileAds.invoke()
    }

    private fun showOpenAdIfNeed() {
        adPlaceName?.let {
            if (!it.isValidateEnable()) {
                log("AdGsSplashManager_showOpenAdIfNeed_isValidateEnable", false)
                isAdLoaded = true
                callBackGoHome()
                return@let
            }
            log("AdGsSplashManager_showOpenAdIfNeed_registerAndShowAds", "")
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
            log("AdGsSplashManager_showOpenAdIfNeed_adPlaceName", "null")
            isAdLoaded = true
            callBackGoHome()
        }
    }

    /**
     * Gọi để hủy quảng cáo
     */
    fun destroy(fromAutoDestroy: Boolean = false) {
        adPlaceName?.let {
            AdGsManager.instance.clearAndRemoveListener(adPlaceName = it, fromAutoDestroy = fromAutoDestroy)
        }
    }

    interface OnRetryAdPlaceNameListener {
        fun getAdPlaceName(): AdPlaceName
    }
}