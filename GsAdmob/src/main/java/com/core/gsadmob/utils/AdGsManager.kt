package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.model.appopen.AppOpenAdGsData
import com.core.gsadmob.model.banner.BannerAdGsData
import com.core.gsadmob.model.base.BaseActiveAdGsData
import com.core.gsadmob.model.base.BaseAdGsData
import com.core.gsadmob.model.base.BaseShowAdGsData
import com.core.gsadmob.model.interstitial.InterstitialAdGsData
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.model.rewarded.RewardedAdGsData
import com.core.gsadmob.model.rewarded.RewardedInterstitialAdGsData
import com.core.gsadmob.utils.extensions.isWebViewEnabled
import com.core.gscore.utils.network.LiveDataNetworkStatus
import com.core.gscore.utils.network.NetworkUtils
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
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
    val adGsDataMapMutableStateFlow = MutableStateFlow(HashMap<AdPlaceName, BaseActiveAdGsData>())

    private val shimmerMap = HashMap<AdPlaceName, Boolean>()
    val startShimmerLiveData = MutableLiveData<HashMap<AdPlaceName, Boolean>>()

    private val backupDelayTimeMap = HashMap<AdPlaceName, Long>()

    private val isVipMutableStateFlow = MutableStateFlow(false)
    var isVipFlow = isVipMutableStateFlow.asStateFlow()

    private var defaultScope: CoroutineScope? = null
    private var currentActivity: Activity? = null

    private var isWebViewEnabled = true
    private var application: Application? = null

    private var isPause = false

    /**
     * Bắt buộc phải khởi tạo ở Application nếu không thì sẽ không thể tải được quảng cáo nào cả
     */
    fun registerCoroutineScope(
        application: Application, coroutineScope: CoroutineScope,
        callbackStartLifecycle: ((activity: AppCompatActivity) -> Unit)? = null,
        callbackPauseLifecycle: ((activity: AppCompatActivity) -> Unit)? = null,
        callbackNothingLifecycle: (() -> Unit)? = null
    ) {
        this.application = application

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

        val resumeLifecycleObserver = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                (currentActivity as? AppCompatActivity)?.let { activity ->
                    if (!isPause) return
                    if (isVipFlow.value) return
                    isPause = false
                    callbackStartLifecycle?.invoke(activity)
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                (currentActivity as? AppCompatActivity)?.let { activity ->
                    callbackPauseLifecycle?.invoke(activity)
                }

                isPause = true

                callbackNothingLifecycle?.invoke()
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(resumeLifecycleObserver)

        val liveDataNetworkStatus = LiveDataNetworkStatus(application)
        liveDataNetworkStatus.observeForever { connect ->
            if (connect) {
                tryReloadAd(isChangeNetwork = true)
            }
        }

        defaultScope = coroutineScope

        defaultScope?.launch {
            isVipFlow.collect { isVip ->
                if (isVip) {
                    clearAll(clearFull = false)
                } else {
                    tryReloadAd(isChangeNetwork = false)
                }
            }
        }
    }

    /**
     *  Thử tải lại những quảng cáo được dùng trong activity hiện tại mà có cấu hình tự động tải lại
     *  @param isChangeNetwork mục đích là xác định việc tải lại là do thay đổi mạng hay thay đổi vip
     *  Dựa vào isActive = true tức là nó được đăng ký dùng (thường là native và banner vì chúng cần cập nhật trên UI luôn)
     */
    private fun tryReloadAd(isChangeNetwork: Boolean) {
        adGsDataMap.forEach {
            val adGsData = it.value
            if (adGsData is BaseActiveAdGsData) {
                if (adGsData.isActive) {
                    loadAd(adPlaceName = it.key, requiredLoadNewAds = false) // không bắt buộc tải mới
                }
            }
        }
    }

    /**
     * Tải quảng cáo
     * @param requiredLoadNewAds = true sẽ yêu cầu tải quảng cáo mới không quan tâm đã có quảng cáo cũ rồi
     * Chú ý : Hàm này ít được dùng , thay vào dó hãy dùng hàm registerAds vì nó có hỗ trợ đăng ký báo lỗi và kích hoạt quảng cáo tự tải lại
     * Hàm này được dùng trực tiếp cho app open ở màn splash
     */
    private fun loadAd(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL, requiredLoadNewAds: Boolean) {
        application?.let {

            if (!isWebViewEnabled) {
                clearAll(clearFull = false)
                return
            }
            if (isVipFlow.value) {
                clearAll(clearFull = false)
                return
            }
            val adGsData = getAdGsData(adPlaceName = adPlaceName)
            adGsDataMap[adPlaceName] = adGsData

            if (adGsData.isLoading) {
                return
            }

            val ads: Any? = if (requiredLoadNewAds) { // requiredLoadNewAds = true tức là sẽ cho phép load ads mới mặc dù đã load đc ad cũ rồi
                null
            } else {
                when (adPlaceName.adGsType) {
                    AdGsType.APP_OPEN_AD -> (adGsData as? AppOpenAdGsData)?.appOpenAd
                    AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> (adGsData as? BannerAdGsData)?.bannerAdView
                    AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd
                    AdGsType.NATIVE -> (adGsData as? NativeAdGsData)?.nativeAd
                    AdGsType.REWARDED -> (adGsData as? RewardedAdGsData)?.rewardedAd
                    AdGsType.REWARDED_INTERSTITIAL -> (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd
                }
            }

            if (ads != null) return

            // isReload = false tức là load lần đầu mới cần check delay time
            // isReload = true tức là load lại 1 lần khi tải lỗi -> bỏ qua delay time
            if (adGsData.delayTime > 0L && !adGsData.isReload) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - adGsData.lastTime < adGsData.delayTime * 1000) return
            }

            if (NetworkUtils.isInternetAvailable(it)) {
                adGsData.isLoading = true

                when (adPlaceName.adGsType) {
                    AdGsType.APP_OPEN_AD -> loadAppOpenAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as AppOpenAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds
                    )

                    AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> {
                        shimmerMap[adPlaceName] = true
                        startShimmerLiveData.postValue(shimmerMap)

                        loadBannerAd(
                            app = it,
                            adPlaceName = adPlaceName,
                            adGsData = adGsData as BannerAdGsData
                        )
                    }

                    AdGsType.INTERSTITIAL -> loadInterstitialAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as InterstitialAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds
                    )

                    AdGsType.NATIVE -> {
                        shimmerMap[adPlaceName] = true
                        startShimmerLiveData.postValue(shimmerMap)

                        loadNativeAd(
                            app = it,
                            adPlaceName = adPlaceName,
                            adGsData = adGsData as NativeAdGsData
                        )
                    }

                    AdGsType.REWARDED -> loadRewardedAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as RewardedAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds
                    )

                    AdGsType.REWARDED_INTERSTITIAL -> loadRewardedInterstitialAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as RewardedInterstitialAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds
                    )
                }
            } else {
                adGsData.listener?.onAdClose(isFailed = true)
            }
        }
    }

    private fun loadAppOpenAd(app: Application, adPlaceName: AdPlaceName, adGsData: AppOpenAdGsData, requiredLoadNewAds: Boolean) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
            }

            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                adGsData.lastTime = System.currentTimeMillis() // khi tải được quảng cáo mới lưu lastTime

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.appOpenAd = appOpenAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        it.onAdSuccess()

                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds)
                    }
                    adGsData.appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.listener?.onAdShowing()
                        }

                        override fun onAdClicked() {
                            adGsData.listener?.onAdClicked()
                        }
                    }
                }
            }
        })
    }

    private fun loadBannerAd(app: Application, adPlaceName: AdPlaceName, adGsData: BannerAdGsData) {
        val bannerAdView = AdView(app)
        bannerAdView.adUnitId = app.getString(adPlaceName.adUnitId)
        bannerAdView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        val adSize = getAdSize(app)
        bannerAdView.setAdSize(adSize)

        // Create an extra parameter that aligns the bottom of the expanded ad to
        // the bottom of the bannerView.
        val extras = Bundle()
        if (adGsData.isCollapsible) {
            extras.putString("collapsible", "bottom")
        }

        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

        bannerAdView.loadAd(adRequest)
        bannerAdView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                shimmerMap[adPlaceName] = false
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                notifyAds()
            }

            override fun onAdLoaded() {
                shimmerMap[adPlaceName] = false
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.bannerAdView = bannerAdView
                    adGsData.isLoading = false
                    notifyAds()

                    adGsData.listener?.onAdSuccess()
                }
            }

            override fun onAdClicked() {
                adGsData.listener?.onAdClicked()
            }
        }
    }

    private fun getAdSize(context: Context): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (context is Activity) {
                    context.windowManager.currentWindowMetrics.bounds.width()
                } else {
                    currentActivity?.windowManager?.currentWindowMetrics?.bounds?.width() ?: displayMetrics.widthPixels
                }
            } else {
                displayMetrics.widthPixels
            }
        } catch (e: Exception) {
            e.printStackTrace()
            displayMetrics.widthPixels
        }
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    /**
     * Tải quảng cáo xen kẽ
     */
    private fun loadInterstitialAd(app: Application, adPlaceName: AdPlaceName, adGsData: InterstitialAdGsData, requiredLoadNewAds: Boolean) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.clearData(isResetReload = false)

                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.interstitialAd = interstitialAd
                    adGsData.isLoading = false

                    adGsData.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose(isFailed = true)
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.listener?.onAdShowing()
                        }

                        override fun onAdClicked() {
                            adGsData.listener?.onAdClicked()
                        }
                    }
                }
            }
        })
    }

    private fun loadNativeAd(app: Application, adPlaceName: AdPlaceName, adGsData: NativeAdGsData) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        val adLoader = AdLoader.Builder(app, app.getString(adPlaceName.adUnitId))
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    shimmerMap[adPlaceName] = false
                    adGsData.listener?.onAdClose(isFailed = true)
                    adGsData.clearData(isResetReload = false)
                    notifyAds()
                }

                override fun onAdClicked() {
                    adGsData.listener?.onAdClicked()
                }
            })
            .forNativeAd { nativeAd ->
                shimmerMap[adPlaceName] = false
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.nativeAd = nativeAd
                    adGsData.isLoading = false
                    notifyAds()
                }
            }.build()
        adLoader.loadAd(adRequest)
    }

    private fun loadRewardedAd(app: Application, adPlaceName: AdPlaceName, adGsData: RewardedAdGsData, requiredLoadNewAds: Boolean) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                //
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.rewardedAd = rewardedAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds)
                    }
                    adGsData.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.listener?.onAdShowing()
                        }

                        override fun onAdClicked() {
                            adGsData.listener?.onAdClicked()
                        }
                    }
                }
            }
        })
    }

    private fun loadRewardedInterstitialAd(
        app: Application,
        adPlaceName: AdPlaceName,
        adGsData: RewardedInterstitialAdGsData,
        requiredLoadNewAds: Boolean
    ) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedInterstitialAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                //
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }

            override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName)
                } else {
                    adGsData.rewardedInterstitialAd = rewardedInterstitialAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds)
                    }
                    adGsData.rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.listener?.onAdShowing()
                        }

                        override fun onAdClicked() {
                            adGsData.listener?.onAdClicked()
                        }
                    }
                }
            }
        })
    }

    /**
     * Kiểm tra thời gian tải quảng cáo có trong vòng N giờ hay không
     */
    private fun wasLoadTimeLessThanNHoursAgo(adGsData: BaseAdGsData, numHours: Long): Boolean {
        val dateDifference = System.currentTimeMillis() - adGsData.lastTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Hiển thị quảng cáo nếu được
     * Nếu không có quảng cáo sẽ tự động tải
     */
    fun showAd(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, callbackShow: ((adShowStatus: AdShowStatus) -> Unit)? = null) {
        when {
            !isWebViewEnabled -> callbackShow?.invoke(AdShowStatus.ERROR_WEB_VIEW)
            isVipFlow.value -> callbackShow?.invoke(AdShowStatus.ERROR_VIP)
            else -> {
                var isShowing = false
                adGsDataMap.forEach {
                    val adData = it.value
                    if (adData is BaseShowAdGsData) {
                        if (adData.isShowing) {
                            isShowing = true
                        }
                    }
                }
                if (isShowing) {
                    callbackShow?.invoke(AdShowStatus.SHOWING)
                    return
                }
                (adGsDataMap[adPlaceName] as? BaseShowAdGsData)?.let { adGsData ->
                    if (adGsData.isCancel) {
                        callbackShow?.invoke(AdShowStatus.CANCEL)
                        return
                    }
                    if (adGsData.isShowing) {
                        callbackShow?.invoke(AdShowStatus.SHOWING)
                        return
                    }
                    val canShow = when (adPlaceName.adGsType) {
                        AdGsType.APP_OPEN_AD -> (adGsData as? AppOpenAdGsData)?.appOpenAd != null && wasLoadTimeLessThanNHoursAgo(adGsData, 4)
                        AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd != null
                        AdGsType.REWARDED -> (adGsData as? RewardedAdGsData)?.rewardedAd != null
                        AdGsType.REWARDED_INTERSTITIAL -> (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd != null
                        else -> false
                    }
                    if (canShow) {
                        callbackShow?.invoke(AdShowStatus.CAN_SHOW)
                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds)
                    } else {
                        callbackShow?.invoke(AdShowStatus.REQUIRE_LOAD)
                        // chưa có thì load
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                } ?: run {
                    callbackShow?.invoke(AdShowStatus.REQUIRE_LOAD)
                    // chưa có thì load
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }
        }
    }

    /**
     * Hiển thị quảng cáo có kiểm tra điều kiện có hủy hay không
     */
    private fun showOrCancelAd(adPlaceName: AdPlaceName, adGsData: BaseShowAdGsData, requiredLoadNewAds: Boolean) {
        currentActivity?.let {
            if (adGsData.isCancel) {
                adGsData.listener = null
                return
            }
            if (adGsData.isShowing) return
            adGsData.isShowing = true

            when (adGsData) {
                is AppOpenAdGsData -> {
                    if (adPlaceName.fragmentTagAppOpenResumeResId == 0) {
                        adGsData.appOpenAd?.show(it)
                    } else {
                        (it as? AppCompatActivity)?.supportFragmentManager?.let { fragmentManager ->
                            val bottomDialogFragment = fragmentManager.findFragmentByTag(it.getString(adPlaceName.fragmentTagAppOpenResumeResId))
                            if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                                // ResumeDialogFragment đang hiển thị
                                adGsData.appOpenAd?.show(it)
                            } else {
                                // ResumeDialogFragment không hiển thị
                                adGsData.listener?.onAdClose()
                                adGsData.clearData(isResetReload = true)
                                //
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }
                    }
                }

                is InterstitialAdGsData -> {
                    adGsData.interstitialAd?.show(it)
                }

                is RewardedAdGsData -> {
                    adGsData.rewardedAd?.show(it) {
                        adGsData.listener?.onShowFinishSuccess()
                    }
                }

                is RewardedInterstitialAdGsData -> {
                    adGsData.rewardedInterstitialAd?.show(it) {
                        adGsData.listener?.onShowFinishSuccess()
                    }
                }

                else -> {}
            }
        }
    }

    /**
     * Đăng ký sự kiện khi tải quảng cáo
     * @param adPlaceName : Cấu hình cho quảng cáo
     * @param adGsListener : Các sự kiện được trả về
     */
    fun registerAdsListener(adPlaceName: AdPlaceName, adGsListener: AdGsListener? = null) {
        val adGsData = getAdGsData(adPlaceName = adPlaceName)
        // update listener
        adGsData.listener = adGsListener
        adGsDataMap[adPlaceName] = adGsData
    }

    /**
     * Đăng kí sự kiện và tải quảng cáo
     */
    fun registerActiveAndLoadAds(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, adGsListener: AdGsListener? = null) {
        registerAdsListener(adPlaceName = adPlaceName, adGsListener = adGsListener)
        activeAd(adPlaceName = adPlaceName)
        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
    }

    /**
     * Đăng kí sự kiện và hiển thị quảng cáo
     */
    fun registerAndShowAds(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, adGsListener: AdGsListener? = null, callbackShow: ((AdShowStatus) -> Unit)? = null) {
        registerAdsListener(adPlaceName = adPlaceName, adGsListener = adGsListener)
        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackShow = callbackShow)
    }

    /**
     * Xóa đăng ký sự kiện khi tải quảng cáo
     */
    fun removeAdsListener(adPlaceName: AdPlaceName) {
        adGsDataMap[adPlaceName]?.listener = null
    }

    /**
     * Lấy 1 quảng cáo theo adPlaceName hoặc tạo mới nếu cần
     */
    private fun getAdGsData(adPlaceName: AdPlaceName): BaseAdGsData {
        return adGsDataMap[adPlaceName] ?: run {
            when (adPlaceName.adGsType) {
                AdGsType.APP_OPEN_AD -> AppOpenAdGsData()
                AdGsType.BANNER -> BannerAdGsData()
                AdGsType.BANNER_COLLAPSIBLE -> BannerAdGsData(isCollapsible = true)
                AdGsType.INTERSTITIAL -> InterstitialAdGsData()
                AdGsType.NATIVE -> NativeAdGsData()
                AdGsType.REWARDED -> RewardedAdGsData()
                AdGsType.REWARDED_INTERSTITIAL -> RewardedInterstitialAdGsData()
            }.apply {
                delayTime = backupDelayTimeMap[adPlaceName] ?: adPlaceName.delayTime

                if (this is BaseActiveAdGsData) {
                    isActive = true
                }
            }
        }
    }

    /**
     * Đăng ký thời gian tối thiểu giữa các lần tải ad
     */
    fun registerDelayTime(delayTime: Long, adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        // lưu lại cho trường hơp chưa tạo adPlaceName
        backupDelayTimeMap[adPlaceName] = delayTime
        // thử set delayTime cho adPlaceName
        adGsDataMap[adPlaceName]?.delayTime = delayTime
    }

    /**
     * Xóa 1 quảng cáo cụ thể
     */
    fun clearWithAdPlaceName(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        adGsDataMap[adPlaceName]?.clearData(isResetReload = true)
        notifyAds()
    }

    /**
     * Xóa hết quảng cáo đi (thường dùng cho trường hợp đã mua vip)
     * @param clearFull = true -> reset về ban đầu
     */
    fun clearAll(clearFull: Boolean = true) {
        adGsDataMap.forEach {
            val adGsData = it.value
            adGsData.clearData(isResetReload = true)

            if (clearFull) {
                if (adGsData is BaseActiveAdGsData) {
                    adGsData.isActive = false
                } else if (adGsData is BaseShowAdGsData) {
                    adGsData.isCancel = false
                }
            }
        }
        notifyAds()
    }

    /**
     * Gửi các thay đổi các quảng cáo đã kích hoạt
     */
    private fun notifyAds() {
        defaultScope?.launch {
            val newData = HashMap<AdPlaceName, BaseActiveAdGsData>()
            adGsDataMap.forEach {
                when (val adGsData = it.value) {
                    is BannerAdGsData -> if (adGsData.isActive) newData[it.key] = adGsData.copy()
                    is NativeAdGsData -> if (adGsData.isActive) newData[it.key] = adGsData.copy()
                }
            }

            adGsDataMapMutableStateFlow.emit(newData)
        }
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
     * Hủy quảng cáo trả thưởng không cho hiển thị nữa (rewardAd khi đang tải thì tắt -> không cho show nữa)
     * @param adPlaceName ad trả thưởng cần cancel
     * @param isCancel = true -> cancel ads và hủy listener đi
     */
    fun cancelRewardAd(adPlaceName: AdPlaceName, isCancel: Boolean = true) {
        when (adPlaceName.adGsType) {
            AdGsType.REWARDED, AdGsType.REWARDED_INTERSTITIAL -> {
                if (isCancel) {
                    adGsDataMap[adPlaceName]?.listener = null
                }
                (adGsDataMap[adPlaceName] as? BaseShowAdGsData)?.isCancel = isCancel
            }

            else -> {

            }
        }
    }

    /**
     * Hủy tất cả quảng cáo trả thưởng không cho hiển thị nữa (rewardAd khi đang tải thì tắt -> không cho show nữa)
     */
    fun cancelAllRewardAd() {
        adGsDataMap.forEach {
            cancelRewardAd(adPlaceName = it.key, isCancel = true)
        }
    }

    /**
     * Kích hoạt tự động tải lại cho các quảng cáo active
     */
    fun activeAd(adPlaceName: AdPlaceName) {
        (adGsDataMap[adPlaceName] as? BaseActiveAdGsData)?.isActive = true
    }

    /**
     * Xóa danh sách quảng cáo và xóa kích hoạt tự động tải lại quảng cáo nếu có
     */
    fun clearAndRemoveActive(adPlaceNameList: MutableList<AdPlaceName>) {
        adPlaceNameList.forEach { adPlaceName ->
            clearAndRemoveActive(adPlaceName = adPlaceName)
        }
    }

    /**
     * Xóa quảng cáo và xóa kích hoạt tự động tải lại quảng cáo nếu có
     */
    fun clearAndRemoveActive(adPlaceName: AdPlaceName) {
        clearWithAdPlaceName(adPlaceName = adPlaceName)

        (adGsDataMap[adPlaceName] as? BaseActiveAdGsData)?.isActive = false
    }

    /**
     * Xóa danh sách quảng cáo sử dụng trong activity hiện tại
     * Xóa các listener đăng ký trong activity hiện tại
     */
    fun destroyActivity() {
        adGsDataMap.forEach {
            removeAdsListener(it.key)
        }
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