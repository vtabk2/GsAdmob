package com.example.gsadmob.ui.activity.base

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.utils.network.NetworkUtils
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.dialogLayout
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.ump.ConsentInformation
import com.gs.core.ui.view.toasty.Toasty
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAdsActivity<VB : ViewBinding>(inflateBinding: (LayoutInflater) -> VB) : BaseMVVMActivity<VB>(inflateBinding) {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    fun checkShowRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit, isRewardedInterstitialAds: Boolean = true, requireCheck: Boolean = true) {
        NetworkUtils.hasInternetAccessCheck(doTask = {
            if (googleMobileAdsConsentManager == null) {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            }
            when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        if (cmpUtils.isCheckGDPR) {
                            googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                            }, onDisableAds = {
                                callback(TypeShowAds.CANCEL)
                            }, isDebug = BuildConfig.DEBUG, timeout = 0)
                        } else {
                            gdprPermissionsDialog?.dismiss()
                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
                                if (granted) {
                                    googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                        loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                                    }, onDisableAds = {
                                        callback(TypeShowAds.CANCEL)
                                    }, isDebug = BuildConfig.DEBUG, timeout = 0)
                                } else {
                                    callback(TypeShowAds.CANCEL)
                                }
                            })
                            gdprPermissionsDialog?.show()
                            dialogLayout(gdprPermissionsDialog)
                        }
                    } else {
                        loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(this, isDebug = BuildConfig.DEBUG, callback = { _ ->
                            checkShowRewardedAds(callback, isRewardedInterstitialAds, requireCheck = false)
                        })
                    } else {
                        loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                    }
                }
            }
        }, doException = { networkError ->
            callback(TypeShowAds.TIMEOUT)
            when (networkError) {
                NetworkUtils.NetworkError.SSL_HANDSHAKE -> {
                    Toasty.showToast(this, R.string.text_please_check_time, Toasty.WARNING)
                }

                else -> {
                    Toasty.showToast(this, R.string.check_network_connection, Toasty.WARNING)
                }
            }
        }, context = this)
    }

    private fun loadAndShowRewardedAds(isRewardedInterstitialAds: Boolean, callback: (typeShowAds: TypeShowAds) -> Unit) {
        val check = AtomicBoolean(true)
        val adPlaceName = if (isRewardedInterstitialAds) AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL else AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED

        AdGsManager.instance.registerAndShowAds(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
            override fun onAdClose(isFailed: Boolean) {
                if (isFailed) {
                    callback(TypeShowAds.FAILED)
                    check.set(false)
                } else {
                    callback(TypeShowAds.CANCEL)
                    check.set(false)
                    AdGsManager.instance.removeAdsListener(adPlaceName = adPlaceName)
                }
            }

            override fun onShowFinishSuccess(rewardItem: RewardItem) {
                log("BaseAdsActivity_onShowFinishSuccess: rewardItem.type", rewardItem.type)
                log("BaseAdsActivity_onShowFinishSuccess: rewardItem.amount", rewardItem.amount)
                callback(TypeShowAds.SUCCESS)
                check.set(false)
            }

            override fun onAdShowing() {
                check.set(false)
            }
        }, callbackShow = { adShowStatus ->
            when (adShowStatus) {
                AdShowStatus.ERROR_WEB_VIEW -> {
                    Toasty.showToast(this, "Điện thoại không bật Android System WebView. Vui lòng kiểm tra Cài dặt -> Ứng dụng -> Android System WebView", Toasty.WARNING)
                }

                AdShowStatus.ERROR_VIP -> {
                    Toasty.showToast(this, "Bạn đã là thành viên vip", Toasty.WARNING)
                }

                else -> {

                }
            }
        })

        NetworkUtils.hasInternetAccessCheck(doTask = {
            // nothing
        }, doException = { networkError ->
            if (!check.get()) return@hasInternetAccessCheck
            callback(TypeShowAds.TIMEOUT)
            AdGsManager.instance.removeAdsListener(adPlaceName = adPlaceName)
            when (networkError) {
                NetworkUtils.NetworkError.SSL_HANDSHAKE -> {
                    Toasty.showToast(this, R.string.text_please_check_time, Toasty.WARNING)
                }

                else -> {
                    Toasty.showToast(this, R.string.check_network_connection, Toasty.WARNING)
                }
            }
        }, this)
    }

    fun updateUiWithVip(isVip: Boolean) {
        if (isVip) {
            Toasty.showToast(this, "Bạn đã là thành viên vip", Toasty.SUCCESS)
        } else {
            Toasty.showToast(this, "Bạn chưa là thành viên vip", Toasty.WARNING)
        }
    }

    enum class TypeShowAds {
        SUCCESS,
        FAILED,
        TIMEOUT,
        CANCEL,
    }
}