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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.core.gsadmob.banner.BannerGsAdView
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
import com.core.gsadmob.natives.view.NativeGsAdView
import com.core.gsadmob.utils.extensions.isWebViewEnabled
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.VipPreferences
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdGsManager {
    private val adGsDataMap = HashMap<AdPlaceName, BaseAdGsData>()
    private val adGsDataMapMutableStateFlow = MutableStateFlow(HashMap<AdPlaceName, BaseActiveAdGsData>())

    private val shimmerMap = HashMap<AdPlaceName, Boolean>()
    private val startShimmerLiveData = MutableLiveData<HashMap<AdPlaceName, Boolean>>()

    private val backupDelayTimeMap = HashMap<AdPlaceName, Long>()

    private val isVipMutableStateFlow = MutableStateFlow(false)
    var isVipFlow = isVipMutableStateFlow.asStateFlow()

    private var defaultScope: CoroutineScope? = null
    private var currentActivity: Activity? = null

    private var isWebViewEnabled = true
    private var application: Application? = null

    private var isPause = false

    private var showLog: Boolean = true

    /**
     * Bắt buộc phải khởi tạo ở Application nếu không thì sẽ không thể tải được quảng cáo nào cả
     * @param applicationId dùng để tạo VipPreferences
     * @param keyVipList đây là danh sách các key lưu giá trị vip của ứng dụng(kiểu có nhiều loại vip như vip tháng, vip năm hay vip toàn bộ)
     * @param callbackStartLifecycle trả kết quả khi quay trở lại ứng dụng(được dùng cho quảng cáo app open resume)
     * @param callbackPauseLifecycle trả kết quả khi ứng dụng vào trạng thái tạm dừng
     * @param callbackNothingLifecycle thường dùng để thiết lập 1 số logic khác(ví dụ retry vip hoặc Lingver)
     * @param callbackChangeVip trả về activity hiện tại và trạng thái vip hiện tại(mục đích là để cập nhật giao diện cho ứng dụng)
     * @param showLog có muốn hiển thị log không?
     */
    fun registerCoroutineScope(
        application: Application,
        coroutineScope: CoroutineScope,
        applicationId: String,
        keyVipList: MutableList<String> = VipPreferences.defaultKeyVipList,
        callbackStartLifecycle: ((activity: AppCompatActivity) -> Unit)? = null,
        callbackPauseLifecycle: ((activity: AppCompatActivity) -> Unit)? = null,
        callbackNothingLifecycle: (() -> Unit)? = null,
        callbackChangeVip: ((currentActivity: Activity?, isVip: Boolean) -> Unit)? = null,
        showLog: Boolean = true
    ) {
        this.application = application
        this.showLog = showLog

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

        val liveDataNetworkStatus = LiveDataNetworkStatus(context = application)
        liveDataNetworkStatus.observeForever { connect ->
            if (connect) {
                tryReloadAd(isChangeNetwork = true)
            }
        }

        defaultScope = coroutineScope

        defaultScope?.launch {

            VipPreferences.instance.initVipPreferences(context = application, applicationId = applicationId)

            AdPlaceNameDefaultConfig.instance.initAdPlaceNameDefaultConfig(application = application)

            async {
                VipPreferences.instance.getVipChangeFlow(keyVipList = keyVipList)
                    .stateIn(this, SharingStarted.Eagerly, VipPreferences.instance.isFullVersion())
                    .collect { isVip ->
                        notifyVip(isVip)
                        callbackChangeVip?.invoke(currentActivity, isVip)
                    }
            }

            async {
                isVipFlow.collect { isVip ->
                    if (isVip) {
                        clearAll(clearFull = false)
                    } else {
                        tryReloadAd(isChangeNetwork = false)
                    }
                }
            }
        }
    }

    /**
     *  Thử tải lại những quảng cáo được dùng trong activity hiện tại mà có cấu hình tự động tải lại
     *  @param isChangeNetwork mục đích là xác định việc tải lại là do thay đổi mạng hay thay đổi vip
     *  Dựa vào isActive = true tức là nó được đăng ký dùng (thường là native và banner vì chúng cần cập nhật trên UI luôn)
     *  Chỉ tải loại những quảng cáo đã active nhưng chưa tải được vì mặc định requiredLoadNewAds = false
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
     * @param adPlaceName đây là cấu hình quảng cáo muốn được tải
     * @param requiredLoadNewAds = true sẽ yêu cầu tải quảng cáo mới không quan tâm đã có quảng cáo cũ rồi
     * Chú ý : Hàm này ít được dùng , thay vào dó hãy dùng hàm registerActiveAndLoadAds hoặc registerAndShowAds vì nó có hỗ trợ đăng ký báo lỗi và kích hoạt quảng cáo tự tải lại
     * Hàm này được dùng trực tiếp cho app open ở màn splash
     */
    private fun loadAd(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean) {
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
                    AdGsType.APP_OPEN -> (adGsData as? AppOpenAdGsData)?.appOpenAd
                    AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> (adGsData as? BannerAdGsData)?.bannerAdView
                    AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd
                    AdGsType.NATIVE -> (adGsData as? NativeAdGsData)?.nativeAd
                    AdGsType.REWARDED -> (adGsData as? RewardedAdGsData)?.rewardedAd
                    AdGsType.REWARDED_INTERSTITIAL -> (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd
                }
            }

            if (!adPlaceName.isEnable) {
                adGsData.listener?.onAdClose(isFailed = true)
                return
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
                    AdGsType.APP_OPEN -> loadAppOpenAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as AppOpenAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds
                    )

                    AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> {
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
                if (adGsData is BaseActiveAdGsData) {
                    shimmerMap[adPlaceName] = false
                    adGsData.clearData(isResetReload = false)
                    notifyAds("loadAd.isInternetAvailable")
                }
            }
        }
    }

    /**
     * Tải quảng cáo app open
     * @param adPlaceName cấu hình quảng cáo muốn được tải
     * @param adGsData nơi chứa quảng cáo
     * @param requiredLoadNewAds = true sẽ yêu cầu tải quảng cáo mới
     */
    private fun loadAppOpenAd(app: Application, adPlaceName: AdPlaceName, adGsData: AppOpenAdGsData, requiredLoadNewAds: Boolean) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(app, adPlaceName.adUnitId, adRequest, object : AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                log("loadAppOpenAd_onAdFailedToLoad: message", loadAdError.message)
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
            }

            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                adGsData.lastTime = System.currentTimeMillis() // khi tải được quảng cáo mới lưu lastTime

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.appOpenAd = appOpenAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        it.onAdSuccess()
                        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, onlyShow = true)
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
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.isShowing = true
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
     * Tải quảng cáo banner
     */
    private fun loadBannerAd(app: Application, adPlaceName: AdPlaceName, adGsData: BannerAdGsData) {
        shimmerMap[adPlaceName] = true
        startShimmerLiveData.postValue(shimmerMap)

        val bannerAdView = AdView(app)
        bannerAdView.adUnitId = adPlaceName.adUnitId
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
                log("loadBannerAd_onAdFailedToLoad: message", loadAdError.message)
                shimmerMap[adPlaceName] = false
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                notifyAds("loadBannerAd.onAdFailedToLoad")
            }

            override fun onAdLoaded() {
                shimmerMap[adPlaceName] = false
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.bannerAdView = bannerAdView
                    adGsData.isLoading = false
                    notifyAds("loadBannerAd.onAdLoaded")

                    adGsData.listener?.onAdSuccess()
                }
            }

            override fun onAdClicked() {
                adGsData.listener?.onAdClicked()
            }
        }
    }

    /**
     * Tính toán kích thước của quảng cáo banner
     */
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
        InterstitialAd.load(app, adPlaceName.adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)

                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.interstitialAd = interstitialAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        it.onAdSuccess()
                        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, onlyShow = true)
                    }
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
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose(isFailed = true)
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.isShowing = true
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
     * Tải quảng cáo native
     */
    private fun loadNativeAd(app: Application, adPlaceName: AdPlaceName, adGsData: NativeAdGsData) {
        shimmerMap[adPlaceName] = true
        startShimmerLiveData.postValue(shimmerMap)

        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        val adLoader = AdLoader.Builder(app, adPlaceName.adUnitId)
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    log("loadNativeAd_onAdFailedToLoad: message", loadAdError.message)
                    shimmerMap[adPlaceName] = false
                    adGsData.listener?.onAdClose(isFailed = true)
                    adGsData.clearData(isResetReload = false)
                    notifyAds("loadNativeAd.onAdFailedToLoad")
                }

                override fun onAdClicked() {
                    adGsData.listener?.onAdClicked()
                }
            })
            .forNativeAd { nativeAd ->
                shimmerMap[adPlaceName] = false
                adGsData.lastTime = System.currentTimeMillis()

                if (isVipFlow.value) {
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.nativeAd = nativeAd
                    adGsData.isLoading = false
                    notifyAds("loadNativeAd.forNativeAd")
                }
            }.build()
        adLoader.loadAd(adRequest)
    }

    /**
     * Tải quảng cáo trả thưởng
     */
    private fun loadRewardedAd(app: Application, adPlaceName: AdPlaceName, adGsData: RewardedAdGsData, requiredLoadNewAds: Boolean) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedAd.load(app, adPlaceName.adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                log("loadRewardedAd_onAdFailedToLoad: message", loadAdError.message)
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
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.rewardedAd = rewardedAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, onlyShow = true)
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
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.isShowing = true
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
     * Tải quảng cáo trả thưởng xen kẽ
     */
    private fun loadRewardedInterstitialAd(
        app: Application,
        adPlaceName: AdPlaceName,
        adGsData: RewardedInterstitialAdGsData,
        requiredLoadNewAds: Boolean
    ) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedInterstitialAd.load(app, adPlaceName.adUnitId, adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                log("loadRewardedInterstitialAd_onAdFailedToLoad: message", loadAdError.message)
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
                    clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = true)
                } else {
                    adGsData.rewardedInterstitialAd = rewardedInterstitialAd
                    adGsData.isLoading = false
                    //
                    adGsData.listener?.let {
                        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, onlyShow = true)
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
                            adGsData.isShowing = false
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            if (adPlaceName.autoReloadWhenDismiss) {
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            adGsData.isShowing = true
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
     * @param requiredLoadNewAds = true sẽ yêu tải quảng cáo mới trong khi đã có quảng cáo sẵn rồi
     * @param onlyShow = true sẽ chỉ hiển thị quảng cáo nếu có thể
     * @param onlyShow = false sẽ tải quảng cáo nếu không có quảng cáo sẵn
     * @param onlyCheckNotShow = true sẽ kiểm tra xem quảng cáo ở trạng thái nào mà không hiển thị nếu đủ điều kiện
     * @param onlyCheckNotShow = false sẽ kiểm tra xem quảng cáo ở trạng thái nào và sẽ hiển thị nếu đủ điều kiện
     * @param callbackShow trả về trạng thái của quảng cáo
     *
     */
    fun showAd(
        adPlaceName: AdPlaceName,
        requiredLoadNewAds: Boolean = false,
        onlyShow: Boolean = false,
        onlyCheckNotShow: Boolean = false,
        callbackShow: ((adShowStatus: AdShowStatus) -> Unit)? = null
    ) {
        when {
            !isWebViewEnabled -> callbackShow?.invoke(AdShowStatus.ERROR_WEB_VIEW)
            isVipFlow.value -> callbackShow?.invoke(AdShowStatus.ERROR_VIP)
            !adPlaceName.isEnable -> callbackShow?.invoke(AdShowStatus.ADS_DISABLE)
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
                        adGsData.listener = null
                        callbackShow?.invoke(AdShowStatus.CANCEL)
                        return
                    }
                    if (adGsData.isShowing) {
                        callbackShow?.invoke(AdShowStatus.SHOWING)
                        return
                    }
                    val canShow = when (adPlaceName.adGsType) {
                        AdGsType.APP_OPEN -> (adGsData as? AppOpenAdGsData)?.appOpenAd != null && wasLoadTimeLessThanNHoursAgo(adGsData, 4)
                        AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd != null
                        AdGsType.REWARDED -> (adGsData as? RewardedAdGsData)?.rewardedAd != null
                        AdGsType.REWARDED_INTERSTITIAL -> (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd != null
                        else -> false
                    }
                    if (canShow) {
                        currentActivity?.let {
                            when (adGsData) {
                                is AppOpenAdGsData -> {
                                    callbackShow?.invoke(AdShowStatus.CAN_SHOW)
                                    if (onlyCheckNotShow) { // chặn hiển thị quảng cáo
                                        return
                                    }
                                    //
                                    adGsData.appOpenAd?.show(it)
                                }

                                is InterstitialAdGsData -> {
                                    callbackShow?.invoke(AdShowStatus.CAN_SHOW)
                                    if (onlyCheckNotShow) {
                                        return
                                    }
                                    //
                                    adGsData.interstitialAd?.show(it)
                                }

                                is RewardedAdGsData -> {
                                    callbackShow?.invoke(AdShowStatus.CAN_SHOW)
                                    if (onlyCheckNotShow) {
                                        return
                                    }
                                    //
                                    adGsData.rewardedAd?.show(it) { rewardItem ->
                                        adGsData.listener?.onShowFinishSuccess(rewardItem)
                                    }
                                }

                                is RewardedInterstitialAdGsData -> {
                                    callbackShow?.invoke(AdShowStatus.CAN_SHOW)
                                    if (onlyCheckNotShow) {
                                        return
                                    }
                                    //
                                    adGsData.rewardedInterstitialAd?.show(it) { rewardItem ->
                                        adGsData.listener?.onShowFinishSuccess(rewardItem)
                                    }
                                }

                                else -> {}
                            }
                        } ?: run {
                            callbackShow?.invoke(AdShowStatus.NO_ACTIVITY)
                        }
                    } else {
                        if (onlyShow) {
                            callbackShow?.invoke(AdShowStatus.ONLY_SHOW)
                            return
                        }
                        callbackShow?.invoke(AdShowStatus.REQUIRE_LOAD)
                        // chưa có thì load
                        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                    }
                } ?: run {
                    if (onlyShow) {
                        callbackShow?.invoke(AdShowStatus.ONLY_SHOW)
                        return
                    }
                    callbackShow?.invoke(AdShowStatus.REQUIRE_LOAD)
                    // chưa có thì load
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
                }
            }
        }
    }

    /**
     * Đăng ký sự kiện khi tải quảng cáo
     * @param adPlaceName : Cấu hình cho quảng cáo
     * @param adGsListener : Các sự kiện được trả về
     */
    private fun registerAdsListener(adPlaceName: AdPlaceName, adGsListener: AdGsListener? = null) {
        val adGsData = getAdGsData(adPlaceName = adPlaceName)
        // update listener
        adGsData.listener = adGsListener
        adGsDataMap[adPlaceName] = adGsData
    }

    /**
     * Đăng kí sự kiện và tải quảng cáo
     */
    private fun registerActiveAndLoadAds(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, adGsListener: AdGsListener? = null) {
        registerAdsListener(adPlaceName = adPlaceName, adGsListener = adGsListener)
        activeAd(adPlaceName = adPlaceName)
        loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds)
    }

    /**
     * Đăng kí sự kiện và hiển thị quảng cáo
     */
    fun registerAndShowAds(
        adPlaceName: AdPlaceName,
        requiredLoadNewAds: Boolean = false,
        adGsListener: AdGsListener? = null,
        onlyShow: Boolean = false,
        callbackShow: ((AdShowStatus) -> Unit)? = null
    ) {
        registerAdsListener(adPlaceName = adPlaceName, adGsListener = adGsListener)
        showAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, onlyShow = onlyShow, callbackShow = callbackShow)
    }

    fun registerNativeOrBanner(
        lifecycleOwner: LifecycleOwner,
        adPlaceName: AdPlaceName,
        bannerGsAdView: BannerGsAdView? = null,
        nativeGsAdView: NativeGsAdView? = null,
        requiredLoadNewAds: Boolean = false,
        adGsListener: AdGsListener? = null,
        callbackSuccess: ((nativeAdGsData: NativeAdGsData?, isStartShimmer: Boolean) -> Unit)? = null,
        callbackFailed: (() -> Unit)? = null
    ) {
        registerNative(
            lifecycleOwner = lifecycleOwner,
            adPlaceName = adPlaceName,
            nativeGsAdView = nativeGsAdView,
            requiredLoadNewAds = requiredLoadNewAds,
            adGsListener = adGsListener,
            callbackSuccess = callbackSuccess,
            callbackFailed = {
                registerBanner(
                    lifecycleOwner = lifecycleOwner,
                    adPlaceName = adPlaceName,
                    bannerGsAdView = bannerGsAdView,
                    requiredLoadNewAds = requiredLoadNewAds,
                    adGsListener = adGsListener,
                    callbackFailed = callbackFailed
                )
            })
    }

    /**
     * Đăng ký quảng cáo native và banner
     */
    private fun registerNativeOrBanner(
        lifecycleOwner: LifecycleOwner,
        adPlaceName: AdPlaceName,
        adGsListener: AdGsListener? = null,
        requiredLoadNewAds: Boolean = false,
        callbackBanner: ((bannerAdGsData: BannerAdGsData?, isStartShimmer: Boolean) -> Unit)? = null,
        callbackNative: ((nativeAdGsData: NativeAdGsData?, isStartShimmer: Boolean) -> Unit)? = null,
        callbackPause: (() -> Unit)? = null,
        callbackDestroy: (() -> Unit)? = null
    ) {
        // 1. Khởi tạo observer ở phạm vi Activity/Fragment
        val pauseResumeObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    callbackPause?.invoke()
                }

                Lifecycle.Event.ON_RESUME -> {

                }

                else -> {}
            }
        }

        // 2. Đăng ký observer trực tiếp với lifecycle (không qua coroutine)
        lifecycleOwner.lifecycle.addObserver(pauseResumeObserver)

        // 3. Xử lý coroutine riêng cho data flow
        lifecycleOwner.lifecycleScope.launch {
            instance.registerActiveAndLoadAds(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, adGsListener = adGsListener)

            // Xử lý shimmer effect
            instance.startShimmerLiveData.observe(lifecycleOwner) { shimmerMap ->
                shimmerMap[adPlaceName]?.takeIf { it }?.let {
                    when (adPlaceName.adGsType) {
                        AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> callbackBanner?.invoke(null, true)
                        AdGsType.NATIVE -> callbackNative?.invoke(null, true)
                        else -> {}
                    }
                }
            }

            // Xử lý data flow khi RESUMED
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                instance.adGsDataMapMutableStateFlow.collect { dataMap ->
                    dataMap[adPlaceName]?.let { adData ->
                        when (adPlaceName.adGsType) {
                            AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> callbackBanner?.invoke(adData as? BannerAdGsData, adData.isLoading)
                            AdGsType.NATIVE -> callbackNative?.invoke(adData as? NativeAdGsData, adData.isLoading)
                            else -> {}
                        }
                    }
                }
            }

            // Xử lý khi DESTROYED
            try {
                awaitCancellation()
            } finally {
                // Gọi callbackDestroy trước khi cleanup
                callbackDestroy?.invoke()

                // Dọn dẹp
                lifecycleOwner.lifecycle.removeObserver(pauseResumeObserver)
                instance.destroyActivity()
                instance.clearAndRemoveActive(adPlaceName)
            }
        }
    }

    /**
     * Đăng kí quảng cáo banner
     */
    fun registerBanner(
        lifecycleOwner: LifecycleOwner,
        adPlaceName: AdPlaceName,
        bannerGsAdView: BannerGsAdView?,
        requiredLoadNewAds: Boolean = false,
        adGsListener: AdGsListener? = null,
        callbackFailed: (() -> Unit)? = null
    ) {
        when (adPlaceName.adGsType) {
            AdGsType.BANNER, AdGsType.BANNER_COLLAPSIBLE -> {
                registerNativeOrBanner(
                    lifecycleOwner = lifecycleOwner,
                    adPlaceName = adPlaceName,
                    requiredLoadNewAds = requiredLoadNewAds,
                    adGsListener = adGsListener,
                    callbackBanner = { bannerAdGsData, isStartShimmer ->
                        bannerGsAdView?.setBannerAdView(adView = bannerAdGsData?.bannerAdView, isStartShimmer = isStartShimmer)
                        try {
                            bannerGsAdView?.resume()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    callbackPause = {
                        try {
                            bannerGsAdView?.pause()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    callbackDestroy = {
                        try {
                            bannerGsAdView?.destroy()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
            }

            else -> {
                callbackFailed?.invoke()
            }
        }
    }

    /**
     * Đăng kí quảng cáo native
     */
    fun registerNative(
        lifecycleOwner: LifecycleOwner,
        adPlaceName: AdPlaceName,
        nativeGsAdView: NativeGsAdView? = null,
        requiredLoadNewAds: Boolean = false,
        adGsListener: AdGsListener? = null,
        callbackSuccess: ((nativeAdGsData: NativeAdGsData?, isStartShimmer: Boolean) -> Unit)? = null,
        callbackFailed: (() -> Unit)? = null
    ) {
        when (adPlaceName.adGsType) {
            AdGsType.NATIVE -> {
                registerNativeOrBanner(
                    lifecycleOwner = lifecycleOwner,
                    adPlaceName = adPlaceName,
                    requiredLoadNewAds = requiredLoadNewAds,
                    adGsListener = adGsListener,
                    callbackNative = { nativeAdGsData, isStartShimmer ->
                        nativeGsAdView?.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                        callbackSuccess?.invoke(nativeAdGsData, isStartShimmer)
                    },
                    callbackDestroy = {
                        try {
                            nativeGsAdView?.destroy()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
            }

            else -> {
                callbackFailed?.invoke()
            }
        }
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
                AdGsType.APP_OPEN -> AppOpenAdGsData()
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
    fun registerDelayTime(delayTime: Long, adPlaceName: AdPlaceName) {
        // lưu lại cho trường hơp chưa tạo adPlaceName
        backupDelayTimeMap[adPlaceName] = delayTime
        // thử set delayTime cho adPlaceName
        adGsDataMap[adPlaceName]?.delayTime = delayTime
    }

    /**
     * Xóa 1 quảng cáo cụ thể
     */
    fun clearWithAdPlaceName(adPlaceName: AdPlaceName, requiredNotify: Boolean = true) {
        adGsDataMap[adPlaceName]?.clearData(isResetReload = true)
        if (requiredNotify) {
            log("clearWithAdPlaceName_adPlaceName", adPlaceName)
            notifyAds("clearWithAdPlaceName")
        }
    }

    /**
     * Xóa hết quảng cáo đi(thường dùng cho trường hợp đã mua vip)
     * @param clearFull = true -> reset về ban đầu(thường được dùng ở activity home khi ấn back thoát ứng dụng hoặc có thể viết ở onDestroy() của home)
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
        notifyAds("clearAll = $clearFull")
    }

    /**
     * Gửi các thay đổi các quảng cáo đã kích hoạt
     */
    private fun notifyAds(from: String) {
        log("AdGsManager_notifyAds_from", from)
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
     * Xóa quảng cáo và xóa kích hoạt tự động tải lại quảng cáo nếu có
     */
    fun clearAndRemoveActive(adPlaceName: AdPlaceName, requiredNotify: Boolean = true) {
        clearWithAdPlaceName(adPlaceName = adPlaceName, requiredNotify = requiredNotify)

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