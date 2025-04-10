package com.core.gsadmob.utils

import android.R.attr.dialogLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.utils.network.NetworkUtils
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.ump.ConsentInformation
import java.util.concurrent.atomic.AtomicBoolean

class AdGsRewardedManager(
    private val activity: AppCompatActivity,
    fragment: Fragment? = null,
    private val adPlaceName: AdPlaceName? = null,
    private val callback: (TypeShowAds) -> Unit,
    private var isDebug: Boolean = false
) {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
        }

        override fun onPause(owner: LifecycleOwner) {
        }

        override fun onDestroy(owner: LifecycleOwner) {
            gdprPermissionsDialog?.dismiss()
            gdprPermissionsDialog = null
        }
    }

    init {
        fragment?.lifecycle?.addObserver(lifecycleObserver) ?: activity.lifecycle.addObserver(lifecycleObserver)
    }

    fun checkShowRewardedAds(requireCheck: Boolean = true) {
        fun gatherConsent() {
            googleMobileAdsConsentManager?.gatherConsent(
                activity = activity,
                onCanShowAds = {
                    loadAndShowRewardedAds()
                },
                onDisableAds = {
                    callback(TypeShowAds.CANCEL)
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
//                            gdprPermissionsDialog?.dismiss()
//                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
//                                if (granted) {
//                                    gatherConsent()
//                                } else {
//                                    callback(TypeShowAds.CANCEL)
//                                }
//                            })
//                            gdprPermissionsDialog?.show()
//                            dialogLayout(gdprPermissionsDialog)
                        }
                    } else {
                        loadAndShowRewardedAds()
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    loadAndShowRewardedAds()
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(activity, isDebug = isDebug, callback = { _ ->
                            checkShowRewardedAds(requireCheck = false)
                        })
                    } else {
                        loadAndShowRewardedAds()
                    }
                }
            }
        }, doException = { networkError ->
            callback.invoke(if (networkError == NetworkUtils.NetworkError.SSL_HANDSHAKE) TypeShowAds.SSL_HANDSHAKE else TypeShowAds.TIMEOUT)
        }, activity = activity)
    }

    private fun loadAndShowRewardedAds() {
        adPlaceName?.let {
            val check = AtomicBoolean(true)
            AdGsManager.instance.registerAndShowAds(
                adPlaceName = it,
                adGsListener = object : AdGsListener {
                    override fun onAdClose(isFailed: Boolean) {
                        if (isFailed) {
                            callback(TypeShowAds.FAILED)
                            check.set(false)
                        } else {
                            callback(TypeShowAds.CANCEL)
                            check.set(false)
                            AdGsManager.instance.removeAdsListener(adPlaceName = it)
                        }
                    }

                    override fun onShowFinishSuccess(rewardItem: RewardItem) {
                        log("AdGsRewardedManager_onShowFinishSuccess: rewardItem.type", rewardItem.type)
                        log("AdGsRewardedManager_onShowFinishSuccess: rewardItem.amount", rewardItem.amount)
                        callback(TypeShowAds.SUCCESS)
                        check.set(false)
                    }

                    override fun onAdShowing() {
                        check.set(false)
                    }
                },
                callbackShow = { adShowStatus ->
                    log("AdGsRewardedManager_adShowStatus", adShowStatus)
                })

            NetworkUtils.hasInternetAccessCheck(doTask = {
                // nothing
            }, doException = { networkError ->
                if (!check.get()) return@hasInternetAccessCheck
                AdGsManager.instance.removeAdsListener(adPlaceName = it)
                callback.invoke(if (networkError == NetworkUtils.NetworkError.SSL_HANDSHAKE) TypeShowAds.SSL_HANDSHAKE else TypeShowAds.TIMEOUT)
            }, activity = activity)
        }
    }

    enum class TypeShowAds {
        SUCCESS,
        FAILED,
        TIMEOUT,
        SSL_HANDSHAKE,
        CANCEL,
    }
}