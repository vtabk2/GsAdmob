package com.example.gsadmob.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.extensions.cmpUtils
import com.core.gsadmob.utils.preferences.GoogleMobileAdsConsentManager
import com.core.gscore.utils.network.NetworkUtils
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.dialogLayout
import com.google.android.ump.ConsentInformation
import com.gs.core.ui.view.toasty.Toasty
import kotlinx.coroutines.async
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

        lifecycleScope.launch {
            async {
                AdGsManager.instance.isVipFlow.collect {
                    isVip = it
                    if (isVip) {
                        bindingView.tvActiveVip.text = "Vip Active"
                    } else {
                        bindingView.tvActiveVip.text = "Vip Inactive"
                    }
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
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(AdPlaceNameConfig.AD_PLACE_NAME_FULL)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllAd()
        }

        bindingView.tvInterstitialWithoutVideo.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(AdPlaceNameConfig.AD_PLACE_NAME_FULL_WITHOUT_VIDEO)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllAd()
        }

        bindingView.tvRewarded.setOnClickListener {
            AdGsManager.instance.cancelAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_REWARDED, isCancel = false)
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

        bindingView.imageRewardedClose.setOnClickListener {
            AdGsManager.instance.cancelAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_REWARDED)
        }

        bindingView.imageRewardedClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_REWARDED)
        }

        bindingView.tvRewardedInterstitial.setOnClickListener {
            AdGsManager.instance.cancelAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL, isCancel = false)
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

        bindingView.imageRewardedInterstitialClose.setOnClickListener {
            AdGsManager.instance.cancelAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }

        bindingView.imageRewardedInterstitialClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
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
        val adPlaceName = if (isRewardedInterstitialAds) AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL else AdPlaceNameConfig.AD_PLACE_NAME_REWARDED

        AdGsManager.instance.registerAdsListener(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
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

            override fun onShowFinishSuccess() {
                callback(TypeShowAds.SUCCESS)
                check.set(false)
            }

            override fun onAdShowing() {
                check.set(false)
            }
        })
        AdGsManager.instance.showAd(adPlaceName = adPlaceName)

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

        AdGsManager.instance.destroyActivity()

        super.onDestroy()
    }
}