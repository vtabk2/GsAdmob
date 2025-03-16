package com.core.gsadmob.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.appopen.AppOpenAdGsData
import com.core.gsadmob.model.base.BaseAdGsData
import com.core.gsadmob.model.base.BaseShowAdGsData
import com.core.gsadmob.model.interstitial.InterstitialAdGsData
import com.core.gsadmob.model.rewarded.RewardedAdGsData
import com.core.gsadmob.model.rewarded.RewardedInterstitialAdGsData
import com.core.gsadmob.utils.extensions.isWebViewEnabled
import com.core.gscore.utils.network.NetworkUtils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
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
    private val adShowGsDataMap = HashMap<AdPlaceName, BaseAdGsData>()

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

        defaultScope = coroutineScope

        defaultScope?.launch {
            isVipFlow.collect { isVip ->
                if (isVip) {
                    clearAll()
                } else {
                    // nothing
                }
            }
        }
    }

    /**
     * Tải quảng cáo
     * @param requiredLoadNewAds = true sẽ yêu cầu tải quảng cáo mới không quan tâm đã có quảng cáo cũ rồi
     */
    fun loadAd(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL, requiredLoadNewAds: Boolean = false, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
        application?.let {

            if (!isWebViewEnabled) {
                clearAll()
                callbackError?.invoke(false)
                return
            }
            if (isVipFlow.value) {
                clearAll()
                callbackError?.invoke(true)
                return
            }
            val adGsData = getAdGsData(adPlaceName = adPlaceName)
            adShowGsDataMap[adPlaceName] = adGsData

            if (adGsData.isLoading) {
                return
            }

            val ads: Any? = if (requiredLoadNewAds) { // requiredLoadNewAds = true tức là sẽ cho phép load ads mới mặc dù đã load đc ad cũ rồi
                null
            } else {
                when (adPlaceName.adGsType) {
                    AdGsType.APP_OPEN_AD -> (adGsData as? AppOpenAdGsData)?.appOpenAd
                    AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd
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
                        requiredLoadNewAds = requiredLoadNewAds,
                        callbackError = callbackError
                    )

                    AdGsType.INTERSTITIAL -> loadInterstitialAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as InterstitialAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds,
                        callbackError = callbackError
                    )

                    AdGsType.REWARDED -> loadRewardedAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as RewardedAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds,
                        callbackError = callbackError
                    )
                    AdGsType.REWARDED_INTERSTITIAL -> loadRewardedInterstitialAd(
                        app = it,
                        adPlaceName = adPlaceName,
                        adGsData = adGsData as RewardedInterstitialAdGsData,
                        requiredLoadNewAds = requiredLoadNewAds,
                        callbackError = callbackError
                    )
                }
            } else {
                adGsData.listener?.onAdClose(isFailed = true)
            }
        }
    }

    private fun loadAppOpenAd(app: Application, adPlaceName: AdPlaceName, adGsData: AppOpenAdGsData, requiredLoadNewAds: Boolean, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
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

                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                    }
                    adGsData.appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
     * Tải quảng cáo xen kẽ
     */
    private fun loadInterstitialAd(app: Application, adPlaceName: AdPlaceName, adGsData: InterstitialAdGsData, requiredLoadNewAds: Boolean, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.clearData(isResetReload = false)

                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose(isFailed = true)
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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

    private fun loadRewardedAd(app: Application, adPlaceName: AdPlaceName, adGsData: RewardedAdGsData, requiredLoadNewAds: Boolean, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                //
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                    }
                    adGsData.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
        requiredLoadNewAds: Boolean,
        callbackError: ((errorVip: Boolean) -> Unit)? = null
    ) {
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        RewardedInterstitialAd.load(app, app.getString(adPlaceName.adUnitId), adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adGsData.listener?.onAdClose(isFailed = true)
                adGsData.clearData(isResetReload = false)
                //
                if (!adGsData.isReload) {
                    adGsData.isReload = true
                    loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
                        showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                    }
                    adGsData.rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            adGsData.listener?.onAdClose()
                            adGsData.clearData(isResetReload = true)
                            //
                            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
    fun showAd(adPlaceName: AdPlaceName, requiredLoadNewAds: Boolean = false, callbackCanShow: ((canShow: Boolean) -> Unit)? = null, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
        (adShowGsDataMap[adPlaceName] as? BaseShowAdGsData)?.let { adGsData ->
            val canShow = if (adGsData.isCancel) {
                false
            } else if (adGsData.isShowing) {
                false
            } else {
                when (adPlaceName.adGsType) {
                    AdGsType.APP_OPEN_AD -> (adGsData as? AppOpenAdGsData)?.appOpenAd != null && wasLoadTimeLessThanNHoursAgo(adGsData, 4)
                    AdGsType.INTERSTITIAL -> (adGsData as? InterstitialAdGsData)?.interstitialAd != null
                    AdGsType.REWARDED -> (adGsData as? RewardedAdGsData)?.rewardedAd != null
                    AdGsType.REWARDED_INTERSTITIAL -> (adGsData as? RewardedInterstitialAdGsData)?.rewardedInterstitialAd != null
                }
            }
            callbackCanShow?.invoke(canShow)
            if (canShow) {
                showOrCancelAd(adPlaceName = adPlaceName, adGsData = adGsData, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
            } else {
                // chưa có thì load
                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
            }
        } ?: run {
            callbackCanShow?.invoke(false)
            // chưa có thì load
            loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
        }
    }

    private fun showOrCancelAd(adPlaceName: AdPlaceName, adGsData: BaseShowAdGsData, requiredLoadNewAds: Boolean, callbackError: ((errorVip: Boolean) -> Unit)? = null) {
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
                                loadAd(adPlaceName = adPlaceName, requiredLoadNewAds = requiredLoadNewAds, callbackError = callbackError)
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
        adShowGsDataMap[adPlaceName] = adGsData
    }

    fun registerAds(adPlaceName: AdPlaceName, adGsListener: AdGsListener? = null, callbackError: (errorVip: Boolean) -> Unit) {
        registerAdsListener(adPlaceName = adPlaceName, adGsListener = adGsListener)
        loadAd(adPlaceName = adPlaceName, callbackError = callbackError)
    }

    /**
     * Xóa đăng ký sự kiện khi tải quảng cáo
     */
    fun removeAdsListener(adPlaceName: AdPlaceName) {
        adShowGsDataMap[adPlaceName]?.listener = null
    }

    private fun getAdGsData(adPlaceName: AdPlaceName): BaseAdGsData {
        return adShowGsDataMap[adPlaceName] ?: run {
            when (adPlaceName.adGsType) {
                AdGsType.APP_OPEN_AD -> AppOpenAdGsData()
                AdGsType.INTERSTITIAL -> InterstitialAdGsData()
                AdGsType.REWARDED -> RewardedAdGsData()
                AdGsType.REWARDED_INTERSTITIAL -> RewardedInterstitialAdGsData()

            }.apply {
                delayTime = backupDelayTimeMap[adPlaceName] ?: 0L
            }
        }
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
        adShowGsDataMap[adPlaceName]?.clearData(isResetReload = true)
    }

    /**
     * Xóa hết quảng cáo đi (thường dùng cho trường hợp đã mua vip)
     */
    fun clearAll() {
        adShowGsDataMap.forEach { data ->
            data.value.clearData(isResetReload = true)
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
     * Hủy quảng cáo không cho hiển thị nữa (đa phần là rewardAd khi đang tải thì tắt -> không cho show nữa)
     * @param adPlaceName ad cần cancel
     * @param isCancel = true -> cancel ads và hủy listener đi
     */
    fun cancelAd(adPlaceName: AdPlaceName, isCancel: Boolean = true) {
        if (isCancel) {
            adShowGsDataMap[adPlaceName]?.listener = null
        }
        (adShowGsDataMap[adPlaceName] as? BaseShowAdGsData)?.isCancel = isCancel
    }

    /**
     * Hủy tất cả quảng cáo không cho show nữa (đa phần là rewardAd khi đang tải thì tắt -> không cho show nữa)
     * @param isCancel = true -> cancel ads và hủy listener đi
     */
    fun cancelAllAd(isCancel: Boolean = true) {
        adShowGsDataMap.forEach {
            if (isCancel) {
                it.value.listener = null
            }
            (it.value as? BaseShowAdGsData)?.isCancel = isCancel
        }
    }

    /**
     * Xóa danh sách quảng cáo sử dụng trong activity hiện tại
     * Xóa các listener đăng ký trong activity hiện tại
     */
    fun destroyActivity() {
        adShowGsDataMap.forEach {
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