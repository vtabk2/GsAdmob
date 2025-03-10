package com.core.gsadmob.utils

import android.app.Activity
import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdGsManager {
    private val adGsDataMap = HashMap<Int, AdGsData>()
    private var backupDelayTimeMap = HashMap<Int, Long>()

    private var isVipMutableStateFlow = MutableStateFlow(false)
    private var isVipFlow = isVipMutableStateFlow.asStateFlow()

    private var defaultScope: CoroutineScope? = null

    fun registerCoroutineScope(coroutineScope: CoroutineScope?) {
        defaultScope = coroutineScope

        defaultScope?.launch {
            isVipFlow.collect { isVip ->
                Log.d("TAG5", "registerCoroutineScope: isVip = $isVip")
            }
        }
    }

    fun loadAd(context: Context, adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        val isWebViewEnabled = context.isWebViewEnabled()
        Log.d("TAG5", "loadAd: isWebViewEnabled = $isWebViewEnabled")
        Log.d("TAG5", "loadAd: isVip = " + isVipFlow.value)
        if (!isWebViewEnabled) {
            Log.d("TAG5", "loadAd: 1")
            return
        }
        Log.d("TAG5", "loadAd: 2")
        if (isVipFlow.value) {
            Log.d("TAG5", "loadAd: 3")
            return
        }
        Log.d("TAG5", "loadAd: 4")
        val adGsData = adGsDataMap[adPlaceName.adUnitId] ?: AdGsData(delayTime = backupDelayTimeMap[adPlaceName.adUnitId] ?: 0L)
        Log.d("TAG5", "loadAd: adGsData = $adGsData")
        adGsDataMap[adPlaceName.adUnitId] = adGsData
        if (adGsData.isLoading) {
            Log.d("TAG5", "loadAd: 5")
            return
        }
        Log.d("TAG5", "loadAd: 6")
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
        Log.d("TAG5", "loadAd: ads = $ads")
        if (ads != null) return

        if (adGsData.delayTime > 0L) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - adGsData.lastTime < adGsData.delayTime * 1000) return
            adGsData.lastTime = currentTime
        }

        Log.d("TAG5", "loadAd: 7")
        adGsData.isLoading = true

        when (adPlaceName.adGsType) {
            AdGsType.INTERSTITIAL -> {
                loadInterstitialAd(context = context, adPlaceName = adPlaceName, adGsData = adGsData)
            }

            AdGsType.REWARDED -> {
                loadRewardedAd(context = context, adPlaceName = adPlaceName, adGsData = adGsData)
            }

            AdGsType.REWARDED_INTERSTITIAL -> {
                loadRewardedInterstitialAd(context = context, adPlaceName = adPlaceName, adGsData = adGsData)
            }

            else -> {

            }
        }
    }

    private fun loadInterstitialAd(context: Context, adPlaceName: AdPlaceName, adGsData: AdGsData) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(context, context.getString(adPlaceName.adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adGsData.interstitialAd = null
                adGsData.isLoading = false
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(context = context, adPlaceName = adPlaceName)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adGsData.interstitialAd = interstitialAd
                adGsData.isLoading = false
                adGsData.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        adGsData.interstitialAd = null
                        adGsData.listener?.onAdClose("onAdDismissedFullScreenContent")
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        adGsData.interstitialAd = null
                        adGsData.listener?.onAdCloseIfFailed()
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }
                }
            }
        })
    }

    private fun loadRewardedAd(context: Context, adPlaceName: AdPlaceName, adGsData: AdGsData) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedAd.load(context, context.getString(adPlaceName.adUnitId), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.rewardedAd = null
                adGsData.isLoading = false
                adGsData.isShowing = false
                adGsData.listener?.onAdCloseIfFailed()
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(context = context, adPlaceName = adPlaceName)
                }
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                adGsData.rewardedAd = rewardedAd
                adGsData.isLoading = false
                adGsData.listener?.let {
                    if (context is Activity) {
                        showRewarded(activity = context, adPlaceName = adPlaceName, adGsData = adGsData)
                    }
                }
                adGsData.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        adGsData.rewardedAd = null
                        adGsData.isShowing = false
                        adGsData.listener?.onAdClose("onAdDismissedFullScreenContent")
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        adGsData.rewardedAd = null
                        adGsData.isShowing = false
                        adGsData.listener?.onAdClose("onAdFailedToShowFullScreenContent")
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }
                }
            }
        })
    }

    private fun loadRewardedInterstitialAd(context: Context, adPlaceName: AdPlaceName, adGsData: AdGsData) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedInterstitialAd.load(context, context.getString(adPlaceName.adUnitId), adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.rewardedInterstitialAd = null
                adGsData.isLoading = false
                adGsData.isShowing = false
                adGsData.listener?.onAdCloseIfFailed()
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(context = context, adPlaceName = adPlaceName)
                }
            }

            override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                adGsData.rewardedInterstitialAd = rewardedInterstitialAd
                adGsData.isLoading = false
                adGsData.listener?.let {
                    if (context is Activity) {
                        showRewarded(activity = context, adPlaceName = adPlaceName, adGsData = adGsData)
                    }
                }
                adGsData.rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        adGsData.rewardedInterstitialAd = null
                        adGsData.isShowing = false
                        adGsData.listener?.onAdClose("onAdDismissedFullScreenContent")
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        adGsData.rewardedInterstitialAd = null
                        adGsData.isShowing = false
                        adGsData.listener?.onAdClose("onAdFailedToShowFullScreenContent")
                        loadAd(context = context, adPlaceName = adPlaceName)
                    }
                }
            }
        })
    }

    fun show(activity: Activity, adPlaceName: AdPlaceName, callbackShow: () -> Unit) {
        val adGsData: AdGsData? = adGsDataMap[adPlaceName.adUnitId]

        if (adGsData?.rewardedInterstitialAd != null || adGsData?.rewardedAd != null) {
            adGsData.isReload = false
            showRewarded(activity = activity, adPlaceName = adPlaceName, adGsData = adGsData)
            callbackShow()
        } else {
            loadAd(context = activity, adPlaceName = adPlaceName)
        }
    }

    private fun showRewarded(activity: Activity, adPlaceName: AdPlaceName, adGsData: AdGsData) {
        if (adGsData.isCancel) {
            adGsData.clearData()
            return
        }
        if (!adGsData.isShowing) {
            adGsData.isShowing = true
            when (adPlaceName.adGsType) {
                AdGsType.REWARDED -> {
                    adGsData.rewardedAd?.show(activity) {
                        adGsData.listener?.onShowFinishSuccess()
                    }
                }

                AdGsType.REWARDED_INTERSTITIAL -> {
                    adGsData.rewardedInterstitialAd?.show(activity) {
                        adGsData.listener?.onShowFinishSuccess()
                    }
                }

                else -> {

                }
            }
        }
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