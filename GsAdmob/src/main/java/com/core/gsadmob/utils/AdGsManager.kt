package com.core.gsadmob.utils

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdGsData
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.extensions.isWebViewEnabled
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdGsManager {
    private val adGsDataMap = HashMap<Int, AdGsData>()
    private var backupDelayTimeMap = HashMap<Int, Long>()

    private var isVipMutableStateFlow = MutableStateFlow(false)
    private var isVipFlow = isVipMutableStateFlow.asStateFlow()

    private var activeAdList = mutableListOf<AdPlaceName>()

    private var defaultScope: CoroutineScope? = null
    private var currentActivity: Activity? = null

    private var isWebViewEnabled = true

    fun registerCoroutineScope(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
                currentActivity?.let {
                    isWebViewEnabled = it.isWebViewEnabled()
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })

        defaultScope = CoroutineScope(Dispatchers.IO)

        defaultScope?.launch {
            isVipFlow.collect { isVip ->
                Log.d("TAG5", "registerCoroutineScope: isVip = $isVip")
                if (isVip) {
                    clearAll()
                } else {
                    // reload active ad list
                    activeAdList.forEach { activeAd ->
                        loadAd(adPlaceName = activeAd)
                    }
                }
                // push
            }
        }
    }

    fun loadAd(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        Log.d("TAG5", "loadAd: isWebViewEnabled = $isWebViewEnabled")
        Log.d("TAG5", "loadAd: isVip = " + isVipFlow.value)
        if (!isWebViewEnabled) {
            return
        }
        if (isVipFlow.value) {
            return
        }
        val adGsData = adGsDataMap[adPlaceName.adUnitId] ?: AdGsData(delayTime = backupDelayTimeMap[adPlaceName.adUnitId] ?: 0L)
        adGsDataMap[adPlaceName.adUnitId] = adGsData
        if (adGsData.isLoading) {
            return
        }
        val ads: Any? = when (adPlaceName.adGsType) {
            AdGsType.INTERSTITIAL -> {
                adGsData.interstitialAd
            }

            AdGsType.REWARDED -> {
                adGsData.rewardedAd
            }

            AdGsType.REWARDED_INTERSTITIAL -> {
                adGsData.rewardedInterstitialAd
            }

            else -> {
                null
            }
        }
        if (ads != null) return
        if (adGsData.delayTime > 0L) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - adGsData.lastTime < adGsData.delayTime * 1000) return
            adGsData.lastTime = currentTime
        }

        adGsData.isLoading = true

        when (adPlaceName.adGsType) {
            AdGsType.INTERSTITIAL -> {
                loadInterstitialAd(adPlaceName = adPlaceName, adGsData = adGsData)
            }

            AdGsType.REWARDED -> {
                loadRewardedAd(adPlaceName = adPlaceName, adGsData = adGsData)
            }

            AdGsType.REWARDED_INTERSTITIAL -> {
                loadRewardedInterstitialAd(adPlaceName = adPlaceName, adGsData = adGsData)
            }

            else -> {

            }
        }
    }

    private fun loadInterstitialAd(adPlaceName: AdPlaceName, adGsData: AdGsData) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            InterstitialAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adGsData.interstitialAd = null
                    adGsData.isLoading = false
                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName)
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adGsData.interstitialAd = interstitialAd
                    adGsData.isLoading = false
                    adGsData.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.interstitialAd = null
                            adGsData.listener?.onAdClose()
                            loadAd(adPlaceName = adPlaceName)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.interstitialAd = null
                            adGsData.listener?.onAdCloseIfFailed()
                            loadAd(adPlaceName = adPlaceName)
                        }
                    }
                }
            })
        }
    }

    private fun loadRewardedAd(adPlaceName: AdPlaceName, adGsData: AdGsData) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            RewardedAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adGsData.rewardedAd = null
                    adGsData.isLoading = false
                    adGsData.isShowing = false
                    adGsData.listener?.onAdCloseIfFailed()
                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName)
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    adGsData.rewardedAd = rewardedAd
                    adGsData.isLoading = false
                    adGsData.listener?.let {
                        showRewarded(adPlaceName = adPlaceName, adGsData = adGsData)
                    }
                    adGsData.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.rewardedAd = null
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            loadAd(adPlaceName = adPlaceName)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.rewardedAd = null
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            loadAd(adPlaceName = adPlaceName)
                        }
                    }
                }
            })
        }
    }

    private fun loadRewardedInterstitialAd(adPlaceName: AdPlaceName, adGsData: AdGsData) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            RewardedInterstitialAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adGsData.rewardedInterstitialAd = null
                    adGsData.isLoading = false
                    adGsData.isShowing = false
                    adGsData.listener?.onAdCloseIfFailed()
                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName)
                    }
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    adGsData.rewardedInterstitialAd = rewardedInterstitialAd
                    adGsData.isLoading = false
                    adGsData.listener?.let {
                        showRewarded(adPlaceName = adPlaceName, adGsData = adGsData)
                    }
                    adGsData.rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.rewardedInterstitialAd = null
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            loadAd(adPlaceName = adPlaceName)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.rewardedInterstitialAd = null
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            loadAd(adPlaceName = adPlaceName)
                        }
                    }
                }
            })
        }
    }

    fun showRewardedAd(adPlaceName: AdPlaceName, callbackShow: () -> Unit) {
        val adGsData: AdGsData? = adGsDataMap[adPlaceName.adUnitId]

        if (adGsData?.rewardedInterstitialAd != null || adGsData?.rewardedAd != null) {
            adGsData.isReload = false
            showRewarded(adPlaceName = adPlaceName, adGsData = adGsData)
            callbackShow()
        } else {
            loadAd(adPlaceName = adPlaceName)
        }
    }

    private fun showRewarded(adPlaceName: AdPlaceName, adGsData: AdGsData) {
        if (adGsData.isCancel) {
            adGsData.clearData()
            return
        }
        if (!adGsData.isShowing) {
            adGsData.isShowing = true
            when (adPlaceName.adGsType) {
                AdGsType.REWARDED -> {
                    currentActivity?.let {
                        adGsData.rewardedAd?.show(it) {
                            adGsData.listener?.onShowFinishSuccess()
                        }
                    }
                }

                AdGsType.REWARDED_INTERSTITIAL -> {
                    currentActivity?.let {
                        adGsData.rewardedInterstitialAd?.show(it) {
                            adGsData.listener?.onShowFinishSuccess()
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

    fun registerAdsListener(adPlaceName: AdPlaceName, adGsListener: AdGsListener) {
        val adGsData = adGsDataMap[adPlaceName.adUnitId] ?: AdGsData(delayTime = backupDelayTimeMap[adPlaceName.adUnitId] ?: 0L)
        // update listener
        adGsData.listener = adGsListener
        adGsDataMap[adPlaceName.adUnitId] = adGsData
    }

    fun removeAdsListener(adPlaceName: AdPlaceName) {
        adGsDataMap[adPlaceName.adUnitId]?.listener = null
    }

    fun registerDelayTime(delayTime: Long, adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        backupDelayTimeMap[adPlaceName.adUnitId] = delayTime
    }

    fun clearWithAdPlaceName(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        val adGsData = adGsDataMap.remove(adPlaceName.adUnitId)
        adGsData?.clearData()
    }

    fun clearAll() {
        adGsDataMap.forEach { data ->
            data.value.clearData()
        }
        adGsDataMap.clear()
    }

    fun activeAd(adPlaceName: AdPlaceName) {
        activeAdList.add(adPlaceName)
    }

    fun destroy() {
        activeAdList.clear()
    }

    companion object {
        private var singleton: AdGsManager? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: AdGsManager
            get() {
                if (singleton == null) {
                    singleton = AdGsManager()
                }
                return singleton as AdGsManager
            }
    }
}