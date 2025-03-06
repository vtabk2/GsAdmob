package com.example.gsadmob.ui.activity

import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.core.gsadmob.rewarded.RewardedInterstitialUtils
import com.core.gsadmob.rewarded.RewardedUtils
import com.core.gscore.utils.extensions.setClickSafeAll
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.extensions.dialogLayout
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.google.android.ump.ConsentInformation
import com.gs.core.ui.view.toasty.Toasty
import com.gs.core.utils.network.NetworkUtils
import java.util.concurrent.atomic.AtomicBoolean

class TestAdsActivity : BaseMVVMActivity<ActivityTestAdsBinding>() {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    override fun getViewBinding(): ActivityTestAdsBinding {
        return ActivityTestAdsBinding.inflate(layoutInflater)
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvRewarded.setClickSafeAll {
            Log.d("TAG5", "initListener: Click Rewarded")
            checkShowRewardedAds(callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded SUCCESS", Toasty.SUCCESS)
                        Log.d("TAG5", "initListener: Rewarded SUCCESS")
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded FAILED", Toasty.ERROR)
                        Log.d("TAG5", "initListener: Rewarded FAILED")
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded TIMEOUT", Toasty.WARNING)
                        Log.d("TAG5", "initListener: Rewarded TIMEOUT")
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@TestAdsActivity, "Rewarded CANCEL", Toasty.WARNING)
                        Log.d("TAG5", "initListener: Rewarded CANCEL")
                    }
                }
            }, isRewardedInterstitialAds = false)
        }

        bindingView.tvRewardedInterstitial.setClickSafeAll {
            Log.d("TAG5", "initListener: Click Rewarded Interstitial")
            checkShowRewardedAds(callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial SUCCESS", Toasty.SUCCESS)
                        Log.d("TAG5", "initListener: Rewarded Interstitial SUCCESS")
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial FAILED", Toasty.ERROR)
                        Log.d("TAG5", "initListener: Rewarded Interstitial FAILED")
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial TIMEOUT", Toasty.WARNING)
                        Log.d("TAG5", "initListener: Rewarded Interstitial TIMEOUT")
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial CANCEL", Toasty.WARNING)
                        Log.d("TAG5", "initListener: Rewarded Interstitial CANCEL")
                    }
                }
            })
        }
    }

    fun checkShowRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit, isRewardedInterstitialAds: Boolean = true, requireCheck: Boolean = true) {
        Log.d("TAG5", "checkShowRewardedAds: isRewardedInterstitialAds = $isRewardedInterstitialAds")
        Log.d("TAG5", "checkShowRewardedAds: requireCheck = $requireCheck")
        NetworkUtils.hasInternetAccessCheck(doTask = {
            if (googleMobileAdsConsentManager == null) {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            }
            when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        if (cmpUtils.isCheckGDPR) {
                            googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                if (isRewardedInterstitialAds) {
                                    showRewardedInterstitialAds(callback)
                                } else {
                                    showRewardedAds(callback)
                                }
                            }, onDisableAds = {
                                callback(TypeShowAds.CANCEL)
                            }, isDebug = BuildConfig.DEBUG, timeout = 0)
                        } else {
                            gdprPermissionsDialog?.dismiss()
                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
                                if (granted) {
                                    googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                        if (isRewardedInterstitialAds) {
                                            showRewardedInterstitialAds(callback)
                                        } else {
                                            showRewardedAds(callback)
                                        }
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
                        if (isRewardedInterstitialAds) {
                            showRewardedInterstitialAds(callback)
                        } else {
                            showRewardedAds(callback)
                        }
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    if (isRewardedInterstitialAds) {
                        showRewardedInterstitialAds(callback)
                    } else {
                        showRewardedAds(callback)
                    }
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(this, isDebug = BuildConfig.DEBUG, callback = { _ ->
                            checkShowRewardedAds(callback, isRewardedInterstitialAds, requireCheck = false)
                        })
                    } else {
                        if (isRewardedInterstitialAds) {
                            showRewardedInterstitialAds(callback)
                        } else {
                            showRewardedAds(callback)
                        }
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

    private fun showRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit) {
        val check = AtomicBoolean(true)
        RewardedUtils.instance.registerAdsListener(object : RewardedUtils.RewardedAdCloseListener {
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
                RewardedUtils.instance.removeAdsListener()
            }
        })
        RewardedUtils.instance.showAd(this) {
            check.set(false)
        }
        NetworkUtils.hasInternetAccessCheck(doTask = {
            // nothing
        }, doException = { networkError ->
            if (!check.get()) return@hasInternetAccessCheck
            callback(TypeShowAds.TIMEOUT)
            RewardedUtils.instance.removeAdsListener()
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