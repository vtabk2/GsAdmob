package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.BaseAdGsData
import com.core.gsadmob.model.BaseRewardedAdGsData
import com.core.gsadmob.model.InterstitialAdGsData
import com.core.gsadmob.model.NativeAdGsData
import com.core.gsadmob.model.RewardedAdGsData
import com.core.gsadmob.model.RewardedInterstitialAdGsData
import com.core.gsadmob.utils.extensions.isWebViewEnabled
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
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
    private val adGsDataMap = HashMap<AdPlaceName, BaseAdGsData>()
    val adGsDataMapMutableStateFlow = MutableStateFlow(HashMap<AdPlaceName, BaseAdGsData>())

    private val backupDelayTimeMap = HashMap<AdPlaceName, Long>()

    private val isVipMutableStateFlow = MutableStateFlow(false)
    var isVipFlow = isVipMutableStateFlow.asStateFlow()

    private val isAutoReloadMutableStateFlow = MutableStateFlow(false)
    private var isAutoReloadFlow = isAutoReloadMutableStateFlow.asStateFlow()

    private val activeAdList = mutableListOf<AdPlaceName>()

    private var defaultScope: CoroutineScope? = null
    private var currentActivity: Activity? = null

    private var isWebViewEnabled = true

    fun registerCoroutineScope(application: Application, coroutineScope: CoroutineScope) {
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

        defaultScope = coroutineScope

        defaultScope?.launch {
            isVipFlow.collect { isVip ->
                if (isVip) {
                    clearAll()
                } else {
                    if (isAutoReloadFlow.value) {
                        // reload active ad list
                        activeAdList.forEach { activeAd ->
                            loadAd(adPlaceName = activeAd, requiredLoadNewAds = true)
                        }
                    }
                }
            }
        }
    }

    fun loadAd(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL, requiredLoadNewAds: Boolean = false, callbackStart: (() -> Unit)? = null) {
        if (!isWebViewEnabled) {
            clearAll()
            return
        }
        if (isVipFlow.value) {
            clearAll()
            return
        }
        val adGsData = adGsDataMap[adPlaceName] ?: BaseAdGsData(delayTime = backupDelayTimeMap[adPlaceName] ?: 0L)
        adGsDataMap[adPlaceName] = adGsData
        if (adGsData.isLoading) {
            return
        }
        var ads: Any? = when (adPlaceName.adGsType) {
            AdGsType.INTERSTITIAL -> {
                (adGsData as? InterstitialAdGsData)?.interstitialAd
            }

            AdGsType.NATIVE -> {
                (adGsData as? NativeAdGsData)?.nativeAd
            }

            AdGsType.REWARDED -> {
                (adGsData as? RewardedAdGsData)?.rewardedAd
            }

            AdGsType.REWARDED_INTERSTITIAL -> {
                (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd
            }
        }
        if (requiredLoadNewAds) { // requiredLoadNewAds = true tức là sẽ cho phép load ads mới mặc dù đã load đc ad cũ rồi
            ads = null
        }

        if (ads != null) return

        // isReload = false tức là load lần đầu mới cần check delay time
        // isReload = true tức là load lại 1 lần khi tải lỗi -> bỏ qua delay time
        if (adGsData.delayTime > 0L && !adGsData.isReload) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - adGsData.lastTime < adGsData.delayTime * 1000) return
            adGsData.lastTime = currentTime
        }

        adGsData.isLoading = true

        when (adPlaceName.adGsType) {
            AdGsType.INTERSTITIAL -> {
                loadInterstitialAd(adPlaceName = adPlaceName, adGsData = adGsData as InterstitialAdGsData, requiredLoadNewAds = requiredLoadNewAds)
            }

            AdGsType.NATIVE -> {
                loadNativeAd(adPlaceName = adPlaceName, adGsData = adGsData as NativeAdGsData, requiredLoadNewAds = requiredLoadNewAds, callbackStart)
            }

            AdGsType.REWARDED -> {
                loadRewardedAd(adPlaceName = adPlaceName, adGsData = adGsData as RewardedAdGsData, requiredLoadNewAds = requiredLoadNewAds)
            }

            AdGsType.REWARDED_INTERSTITIAL -> {
                loadRewardedInterstitialAd(adPlaceName = adPlaceName, adGsData = adGsData as RewardedInterstitialAdGsData, requiredLoadNewAds = requiredLoadNewAds)
            }
        }
    }

    private fun loadInterstitialAd(adPlaceName: AdPlaceName, adGsData: InterstitialAdGsData, requiredLoadNewAds: Boolean) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            InterstitialAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adGsData.clearData(isResetReload = false)
                    notifyAds()

                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    if (isVipFlow.value) {
                        clearWithAdPlaceName(adPlaceName = adPlaceName)
                    } else {
                        adGsData.interstitialAd = interstitialAd
                        adGsData.isLoading = false
                        notifyAds()

                        adGsData.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdClose()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdCloseIfFailed()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun loadNativeAd(adPlaceName: AdPlaceName, adGsData: NativeAdGsData, requiredLoadNewAds: Boolean, callbackStart: (() -> Unit)? = null) {
        currentActivity?.let {
            callbackStart?.invoke()
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            val adLoader = AdLoader.Builder(it, it.getString(adPlaceName.adUnitId)).forNativeAd { nativeAd ->
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (it.isDestroyed) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.nativeAd = nativeAd
                    adGsData.isLoading = false
                    notifyAds()
                }
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    adGsData.clearData(isResetReload = false)
                    notifyAds()

                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                }
            }).build()
            adLoader.loadAd(adRequest)
        }
    }

    private fun loadRewardedAd(adPlaceName: AdPlaceName, adGsData: RewardedAdGsData, requiredLoadNewAds: Boolean) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            RewardedAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adGsData.clearData(isResetReload = false)
                    notifyAds()

                    adGsData.listener?.onAdCloseIfFailed()
                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    if (isVipFlow.value) {
                        clearWithAdPlaceName(adPlaceName = adPlaceName)
                    } else {
                        adGsData.rewardedAd = rewardedAd
                        adGsData.isLoading = false
                        notifyAds()

                        adGsData.listener?.let {
                            showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData)
                        }
                        adGsData.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdClose()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdClose()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun loadRewardedInterstitialAd(adPlaceName: AdPlaceName, adGsData: RewardedInterstitialAdGsData, requiredLoadNewAds: Boolean) {
        currentActivity?.let {
            val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
            RewardedInterstitialAd.load(it, it.getString(adPlaceName.adUnitId), adRequest, object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adGsData.clearData(isResetReload = false)
                    notifyAds()

                    adGsData.listener?.onAdCloseIfFailed()
                    if (!adGsData.isReload) {
                        adGsData.isReload = true
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    if (isVipFlow.value) {
                        clearWithAdPlaceName(adPlaceName = adPlaceName)
                    } else {
                        adGsData.rewardedInterstitialAd = rewardedInterstitialAd
                        adGsData.isLoading = false
                        notifyAds()

                        adGsData.listener?.let {
                            showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData)
                        }
                        adGsData.rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdClose()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                adGsData.clearData(isResetReload = true)
                                notifyAds()

                                adGsData.listener?.onAdClose()
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }
                    }
                }
            })
        }
    }

    fun showAd(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, callbackShow: (() -> Unit)? = null) {
        adGsDataMap[adPlaceName]?.let { adGsData ->
            val canShow = when (adPlaceName.adGsType) {
                AdGsType.INTERSTITIAL -> {
                    (adGsData as? InterstitialAdGsData)?.interstitialAd != null
                }

                AdGsType.REWARDED -> {
                    (adGsData as? RewardedAdGsData)?.rewardedAd != null
                }

                AdGsType.REWARDED_INTERSTITIAL -> {
                    (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd != null
                }

                else -> {
                    false
                }
            }
            if (canShow) {
                showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData)
                callbackShow?.invoke()
            } else {
                // chưa có thì load
                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
            }
        } ?: run {
            // chưa có thì load
            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
        }
    }

    private fun showOrCancelAd(adPlaceName: AdPlaceName, adGsData: BaseAdGsData) {
        currentActivity?.let {
            when (adGsData) {
                is InterstitialAdGsData -> {
                    adGsData.interstitialAd?.show(it)
                }

                is BaseRewardedAdGsData -> {
                    if (adGsData.isCancel) return
                    if (adGsData.isShowing) return
                    adGsData.isShowing = true
                    when (adPlaceName.adGsType) {
                        AdGsType.REWARDED -> {
                            (adGsData as? RewardedAdGsData)?.rewardedAd?.show(it) {
                                adGsData.listener?.onShowFinishSuccess()
                            }
                        }

                        AdGsType.REWARDED_INTERSTITIAL -> {
                            (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd?.show(it) {
                                adGsData.listener?.onShowFinishSuccess()
                            }
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun registerAdsListener(adPlaceName: AdPlaceName, adGsListener: AdGsListener) {
        val adGsData = adGsDataMap[adPlaceName] ?: BaseAdGsData(delayTime = backupDelayTimeMap[adPlaceName] ?: 0L)
        // update listener
        adGsData.listener = adGsListener
        adGsDataMap[adPlaceName] = adGsData
    }

    fun removeAdsListener(adPlaceName: AdPlaceName) {
        adGsDataMap[adPlaceName]?.listener = null
    }

    /**
     * Đăng ký thời gian tối thiểu giữa các lần tải ad
     */
    fun registerDelayTime(delayTime: Long, adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        backupDelayTimeMap[adPlaceName] = delayTime
    }

    /**
     * Xóa 1 ad cụ thể
     */
    fun clearWithAdPlaceName(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        adGsDataMap[adPlaceName]?.clearData(isResetReload = true)
        notifyAds()
    }

    /**
     * Xóa hết ads đi thường dùng cho trường hợp đã mua vip
     */
    fun clearAll() {
        adGsDataMap.forEach { data ->
            data.value.clearData(isResetReload = true)
        }
        notifyAds()
    }

    /**
     * Cập nhật trạng thái các ads được active ở activity này
     */
    private fun notifyAds() {
        val newData = HashMap<AdPlaceName, BaseAdGsData>()
        activeAdList.forEach {
            adGsDataMap[it]?.let { adGsData ->
                newData[it] = adGsData
            }
        }
        adGsDataMapMutableStateFlow.value = newData
    }

    /**
     * Cập nhật trạng thái vip
     * @param isVip = true đã có vip và ngược lại
     */
    fun notifyVip(isVip: Boolean) {
        defaultScope?.launch {
            isVipMutableStateFlow.emit(isVip)
        }
    }

    /**
     * Cho phép tự động tải ad không
     * @param isAutoReload = true cho phép tự động tải laại ad khi mất vip
     */
    fun notifyAutoReload(isAutoReload: Boolean) {
        defaultScope?.launch {
            isVipMutableStateFlow.emit(isAutoReload)
        }
    }

    /**
     * Hủy ads không cho show nữa (đa phần là rewardAd khi đang tải thì tắt -> không cho show nữa)
     */
    fun cancelAd(adPlaceName: AdPlaceName, isCancel: Boolean) {
        (adGsDataMap[adPlaceName] as? BaseRewardedAdGsData)?.isCancel = isCancel
    }

    /**
     * Đăng ký danh sách ads đươc sử dụng trong activity -> mục đích là khi thay đổi vip sẽ tự động load lại ads
     */
    fun activeAd(adPlaceName: AdPlaceName) {
        if (!activeAdList.contains(adPlaceName)) {
            activeAdList.add(adPlaceName)
        }
    }

    /**
     * Xóa danh sách ads sử dụng trong activity hiện tại
     */
    fun destroyActivity() {
        activeAdList.clear()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
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