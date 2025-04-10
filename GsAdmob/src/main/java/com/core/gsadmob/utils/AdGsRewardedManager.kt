package com.core.gsadmob.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.extensions.dialogLayout
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.utils.network.NetworkUtils
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.ump.ConsentInformation
import java.util.concurrent.atomic.AtomicBoolean

class AdGsRewardedManager(
    private val activity: AppCompatActivity,
    fragment: Fragment? = null,
    private var adPlaceName: AdPlaceName? = null,
    private var isDebug: Boolean = false
) {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            // hủy dialog khi lifecycle bị hủy
            gdprPermissionsDialog?.dismiss()
            gdprPermissionsDialog = null
        }
    }

    init {
        fragment?.lifecycle?.addObserver(lifecycleObserver) ?: activity.lifecycle.addObserver(lifecycleObserver)
    }

    /**
     * @param adPlaceName truyền vào nếu có nhiều quảng cáo trả thưởng hoặc chưa khởi tạo từ đầu.
     * Nếu không truyền mới sẽ dùng adPlaceName cuối cùng truyền vào
     * @param callback lấy ra trạng thái của quảng cái trả thưởng
     * @param callbackShow trạng thái hiện tại của quảng cáo
     * @param callbackStart khi bắt đầu tải quảng cáo
     */
    fun showAds(
        adPlaceName: AdPlaceName? = null,
        callback: ((TypeShowAds) -> Unit)? = null,
        callbackShow: ((AdShowStatus) -> Unit)? = null,
        callbackStart: (() -> Unit)? = null
    ) {
        if (adPlaceName != null) {
            // khi đổi quảng cáo trả thưởng thì bỏ listener quảng cáo trả thưởng cũ đi
            this.adPlaceName?.let {
                AdGsManager.instance.cancelRewardAd(adPlaceName = it)
            }
            //
            this.adPlaceName = adPlaceName
        }
        this.adPlaceName?.let {
            // bỏ trạng thái hủy của quảng cáo trả thưởng hiện tại để quảng cáo này có thể được hiển thị
            AdGsManager.instance.cancelRewardAd(adPlaceName = it, isCancel = false)
        }
        checkShowRewardedAds(
            callback = callback,
            callbackShow = callbackShow,
            callbackStart = callbackStart,
            requireCheck = true
        )
    }

    /**
     * Kiểm tra xem GDPR có được chấp nhận chưa, nếu chưa thì hiển thị GDPR
     */
    private fun checkShowRewardedAds(
        callback: ((TypeShowAds) -> Unit)? = null,
        callbackShow: ((AdShowStatus) -> Unit)? = null,
        callbackStart: (() -> Unit)? = null,
        requireCheck: Boolean = true
    ) {
        fun gatherConsent() {
            googleMobileAdsConsentManager?.gatherConsent(
                activity = activity,
                onCanShowAds = {
                    loadAndShowRewardedAds(
                        callback = callback,
                        callbackShow = callbackShow,
                        callbackStart = callbackStart
                    )
                },
                onDisableAds = {
                    callback?.invoke(TypeShowAds.CANCEL)
                },
                isDebug = isDebug,
                timeout = 0
            )
        }

        NetworkUtils.hasInternetAccessCheck(doTask = {
            if (googleMobileAdsConsentManager == null) {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity)
            }
            when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                    if (activity.cmpUtils.requiredShowCMPDialog()) {
                        if (activity.cmpUtils.isCheckGDPR) {
                            gatherConsent()
                        } else {
                            gdprPermissionsDialog?.dismiss()
                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(activity, callback = { granted ->
                                if (granted) {
                                    gatherConsent()
                                } else {
                                    callback?.invoke(TypeShowAds.CANCEL)
                                }
                            })
                            gdprPermissionsDialog?.show()
                            activity.dialogLayout(gdprPermissionsDialog)
                        }
                    } else {
                        loadAndShowRewardedAds(
                            callback = callback,
                            callbackShow = callbackShow,
                            callbackStart = callbackStart
                        )
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    loadAndShowRewardedAds(
                        callback = callback,
                        callbackShow = callbackShow,
                        callbackStart = callbackStart
                    )
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(activity, isDebug = isDebug, callback = { _ ->
                            checkShowRewardedAds(
                                callback = callback,
                                callbackShow = callbackShow,
                                callbackStart = callbackStart,
                                requireCheck = false
                            )
                        })
                    } else {
                        loadAndShowRewardedAds(
                            callback = callback,
                            callbackShow = callbackShow,
                            callbackStart = callbackStart
                        )
                    }
                }
            }
        }, doException = { networkError ->
            callback?.invoke(if (networkError == NetworkUtils.NetworkError.SSL_HANDSHAKE) TypeShowAds.SSL_HANDSHAKE else TypeShowAds.TIMEOUT)
        }, activity = activity)
    }

    private fun loadAndShowRewardedAds(
        callback: ((TypeShowAds) -> Unit)? = null,
        callbackShow: ((AdShowStatus) -> Unit)? = null,
        callbackStart: (() -> Unit)? = null
    ) {
        adPlaceName?.let {
            callbackStart?.invoke()
            val check = AtomicBoolean(true)
            AdGsManager.instance.registerAndShowAds(
                adPlaceName = it,
                adGsListener = object : AdGsListener {
                    override fun onAdClose(isFailed: Boolean) {
                        if (isFailed) {
                            callback?.invoke(TypeShowAds.FAILED)
                            check.set(false)
                        } else {
                            callback?.invoke(TypeShowAds.CANCEL)
                            check.set(false)
                            AdGsManager.instance.removeAdsListener(adPlaceName = it)
                        }
                    }

                    override fun onShowFinishSuccess(rewardItem: RewardItem) {
                        log("AdGsRewardedManager_onShowFinishSuccess: rewardItem.type", rewardItem.type)
                        log("AdGsRewardedManager_onShowFinishSuccess: rewardItem.amount", rewardItem.amount)
                        callback?.invoke(TypeShowAds.SUCCESS)
                        check.set(false)
                    }

                    override fun onAdShowing() {
                        check.set(false)
                    }
                },
                callbackShow = { adShowStatus ->
                    callbackShow?.invoke(adShowStatus)
                    log("AdGsRewardedManager_adShowStatus", adShowStatus)
                })

            NetworkUtils.hasInternetAccessCheck(doTask = {
                // nothing
            }, doException = { networkError ->
                if (!check.get()) return@hasInternetAccessCheck
                // bắt buộc phải cancel quảng cáo này đi
                AdGsManager.instance.cancelRewardAd(adPlaceName = it)
                callback?.invoke(if (networkError == NetworkUtils.NetworkError.SSL_HANDSHAKE) TypeShowAds.SSL_HANDSHAKE else TypeShowAds.TIMEOUT)
            }, activity = activity)
        } ?: run {
            callback?.invoke(TypeShowAds.NO_AD_PLACE_NAME)
        }
    }

    enum class TypeShowAds {
        /**
         * Quảng cáo trả thưởng đã xem thành công có thể nhận thưởng
         */
        SUCCESS,

        /**
         * Quảng cáo trả thưởng bị lỗi
         */
        FAILED,

        /**
         * Lỗi không bật mạng hoặc mạng yếu
         */
        TIMEOUT,

        /**
         * Lỗi chỉnh thời gian máy -> hết hạn các chứng chỉ
         */
        SSL_HANDSHAKE,

        /**
         * Hủy xem quảng cáo
         */
        CANCEL,

        /**
         * Chưa có adPlaceName nào được truyền vào
         */
        NO_AD_PLACE_NAME
    }
}