package com.example.gsadmob.ui.activity

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.rewarded.RewardedInterstitialUtils
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.extensions.dialogLayout
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.google.android.ump.ConsentInformation
import com.gs.core.ui.view.toasty.Toasty
import com.gs.core.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class TestAdsActivity : AppCompatActivity() {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    fun showRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit, requireCheck: Boolean = true) {
        NetworkUtils.hasInternetAccessCheck(doTask = {
            if (googleMobileAdsConsentManager == null) {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            }
            when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        if (cmpUtils.isCheckGDPR) {
                            googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                showRewardedInterstitialAds(callback)
                            }, onDisableAds = {
                                callback(TypeShowAds.CANCEL)
                            }, isDebug = BuildConfig.DEBUG, timeout = 0)
                        } else {
                            gdprPermissionsDialog?.dismiss()
                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
                                if (granted) {
                                    googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                        showRewardedInterstitialAds(callback)
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
                        showRewardedInterstitialAds(callback)
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    showRewardedInterstitialAds(callback)
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(this, isDebug = BuildConfig.DEBUG, callback = { _ ->
                            showRewardedAds(callback, false)
                        })
                    } else {
                        showRewardedInterstitialAds(callback)
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

    private fun showRewardedInterstitialAds(callback: (typeShowAds: TypeShowAds) -> Unit) {
        val check = AtomicBoolean(true)
        RewardedInterstitialUtils.instance.registerAdsListener(object : RewardedInterstitialUtils.RewardedAdCloseListener {
            override fun onAdCloseIfFailed() {
                callback(TypeShowAds.FAILED)
                check.set(false)
            }

            override fun onShowFinishSuccess() {
                callback(TypeShowAds.SUCCESS)
                check.set(false)
            }

            override fun onAdClose() {
                callback(TypeShowAds.CANCEL)
                check.set(false)
                RewardedInterstitialUtils.instance.removeAdsListener()
            }
        })
        RewardedInterstitialUtils.instance.showAd(this) {
            check.set(false)
        }
        NetworkUtils.hasInternetAccessCheck(doTask = {
            // nothing
        }, doException = { networkError ->
            if (!check.get()) return@hasInternetAccessCheck
            callback(TypeShowAds.TIMEOUT)
            RewardedInterstitialUtils.instance.removeAdsListener()
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

    enum class TypeShowAds {
        SUCCESS,
        FAILED,
        TIMEOUT,
        CANCEL,
    }

    override fun onDestroy() {
        gdprPermissionsDialog?.dismiss()
        gdprPermissionsDialog = null

        super.onDestroy()
    }
}