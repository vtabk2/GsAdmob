package com.example.gsadmob.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.setClickSafeAll
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.TestApplication
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.cmpUtils
import com.example.gsadmob.utils.extensions.dialogLayout
import com.example.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.google.android.ump.ConsentInformation
import com.gs.core.ui.view.toasty.Toasty
import com.gs.core.utils.network.NetworkUtils
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class TestAdsActivity : BaseMVVMActivity<ActivityTestAdsBinding>() {
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    private var gdprPermissionsDialog: AlertDialog? = null

    private var isVip: Boolean = false

    override fun getViewBinding(): ActivityTestAdsBinding {
        return ActivityTestAdsBinding.inflate(layoutInflater)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        TestApplication.applicationContext().initMobileAds()

        lifecycleScope.launch {
            AdGsManager.instance.isVipFlow.collect {
                isVip = it
                Log.d("TAG5", "setupView: isVip = $isVip")
                if (isVip) {
                    bindingView.tvActiveVip.text = "Vip Active"
                } else {
                    bindingView.tvActiveVip.text = "Vip Inactive"
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            AdGsManager.instance.notifyVip(isVip = !isVip)
        }

        bindingView.tvInterstitial.setOnClickListener {
            AdGsManager.instance.loadAd(AdPlaceNameConfig.AD_PLACE_NAME_FULL)
        }

        bindingView.tvRewarded.setClickSafeAll {
            checkShowRewardedAds(callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded SUCCESS", Toasty.SUCCESS)
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded FAILED", Toasty.ERROR)
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded TIMEOUT", Toasty.WARNING)
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@TestAdsActivity, "Rewarded CANCEL", Toasty.WARNING)
                    }
                }
            }, isRewardedInterstitialAds = false)
        }

        bindingView.tvRewardedInterstitial.setClickSafeAll {
            checkShowRewardedAds(callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial SUCCESS", Toasty.SUCCESS)
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial FAILED", Toasty.ERROR)
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial TIMEOUT", Toasty.WARNING)
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial CANCEL", Toasty.WARNING)
                    }
                }
            })
        }

        bindingView.tvNativeFrame.setClickSafeAll {
            NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
                bindingView.nativeFrame.startShimmer()
            }, callback = { nativeAd ->
                bindingView.nativeFrame.setNativeAd(nativeAd)
            })
        }

        bindingView.imageFrameClose.setOnClickListener {
            bindingView.nativeFrame.gone()
        }

        bindingView.tvNativeLanguage.setClickSafeAll {
            NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
                bindingView.nativeLanguage.startShimmer()
            }, callback = { nativeAd ->
                bindingView.nativeLanguage.setNativeAd(nativeAd)
            })
        }

        bindingView.imageLanguageClose.setOnClickListener {
            bindingView.nativeLanguage.gone()
        }
    }

    private fun checkShowRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit, isRewardedInterstitialAds: Boolean = true, requireCheck: Boolean = true) {
        NetworkUtils.hasInternetAccessCheck(doTask = {
            if (googleMobileAdsConsentManager == null) {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            }
            when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        if (cmpUtils.isCheckGDPR) {
                            googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                loadRewardedAds(isRewardedInterstitialAds, callback)
                            }, onDisableAds = {
                                callback(TypeShowAds.CANCEL)
                            }, isDebug = BuildConfig.DEBUG, timeout = 0)
                        } else {
                            gdprPermissionsDialog?.dismiss()
                            gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
                                if (granted) {
                                    googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                        loadRewardedAds(isRewardedInterstitialAds, callback)
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
                        loadRewardedAds(isRewardedInterstitialAds, callback)
                    }
                }

                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                    loadRewardedAds(isRewardedInterstitialAds, callback)
                }

                else -> {
                    // mục đích chỉ check 1 lần không được thì thôi
                    if (requireCheck) {
                        googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(this, isDebug = BuildConfig.DEBUG, callback = { _ ->
                            checkShowRewardedAds(callback, isRewardedInterstitialAds, requireCheck = false)
                        })
                    } else {
                        loadRewardedAds(isRewardedInterstitialAds, callback)
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

    private fun loadRewardedAds(isRewardedInterstitialAds: Boolean, callback: (typeShowAds: TypeShowAds) -> Unit) {
        val check = AtomicBoolean(true)

        val adPlaceName = if (isRewardedInterstitialAds) AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL else AdPlaceNameConfig.AD_PLACE_NAME_REWARDED

        AdGsManager.instance.registerAdsListener(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
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
                AdGsManager.instance.removeAdsListener(adPlaceName = adPlaceName)
            }
        })

        AdGsManager.instance.showRewardedAd(adPlaceName = adPlaceName, callbackShow = {
            check.set(false)
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