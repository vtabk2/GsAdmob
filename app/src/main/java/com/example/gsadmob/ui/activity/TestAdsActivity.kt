package com.example.gsadmob.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.callback.AdGsExtendListener
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdGsRewardedManager.TypeShowAds
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
import com.gs.core.ui.view.toasty.Toasty
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestAdsActivity : BaseAdsActivity<ActivityTestAdsBinding>(ActivityTestAdsBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        lifecycleScope.launch {
            async {
                AdGsManager.instance.isVipFlow.collect { isVip ->
                    if (isVip) {
                        bindingView.tvActiveVip.text = "Vip Active"
                    } else {
                        bindingView.tvActiveVip.text = "Vip Inactive"
                    }
                }
            }
        }

        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerTestAds,
            bannerGsAdView = bindingView.bannerView
        )
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            VipPreferences.instance.isPro = !VipPreferences.instance.isPro
        }

        bindingView.tvInterstitial.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL,
                adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "TestAdsActivity_onAdClicked: AD_PLACE_NAME_INTERSTITIAL")
                }
            })
        }

        bindingView.tvInterstitialWithoutVideo.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO, adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "TestAdsActivity_onAdClicked: AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO")
                }
            })
        }

        bindingView.tvRewarded.setOnClickListener {
            adGsRewardedManager?.showAds(
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED,
                callback = { typeShowAds ->
                    log("TestAdsActivity_typeShowAds", typeShowAds)
                    when (typeShowAds) {
                        TypeShowAds.SUCCESS -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded SUCCESS", Toasty.SUCCESS)
                        }

                        TypeShowAds.FAILED -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded FAILED", Toasty.ERROR)
                        }

                        TypeShowAds.TIMEOUT -> {
                            Toasty.showToast(this@TestAdsActivity, R.string.check_network_connection, Toasty.WARNING)
                        }

                        TypeShowAds.SSL_HANDSHAKE -> {
                            Toasty.showToast(this@TestAdsActivity, R.string.text_please_check_time, Toasty.WARNING)
                        }

                        TypeShowAds.CANCEL -> {
                            // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                            Toasty.showToast(this@TestAdsActivity, "Rewarded CANCEL", Toasty.WARNING)
                        }

                        TypeShowAds.NO_AD_PLACE_NAME -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded Chưa có AdPlaceName truyền vào", Toasty.WARNING)
                        }
                    }
                })
        }

        bindingView.imageRewardedClose.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.imageRewardedClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.tvRewardedInterstitial.setOnClickListener {
            adGsRewardedManager?.showAds(
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL,
                callback = { typeShowAds ->
                    log("TestAdsActivity_typeShowAds", typeShowAds)
                    when (typeShowAds) {
                        TypeShowAds.SUCCESS -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded SUCCESS", Toasty.SUCCESS)
                        }

                        TypeShowAds.FAILED -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded FAILED", Toasty.ERROR)
                        }

                        TypeShowAds.TIMEOUT -> {
                            Toasty.showToast(this@TestAdsActivity, R.string.check_network_connection, Toasty.WARNING)
                        }

                        TypeShowAds.SSL_HANDSHAKE -> {
                            Toasty.showToast(this@TestAdsActivity, R.string.text_please_check_time, Toasty.WARNING)
                        }

                        TypeShowAds.CANCEL -> {
                            // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                            Toasty.showToast(this@TestAdsActivity, "Rewarded CANCEL", Toasty.WARNING)
                        }

                        TypeShowAds.NO_AD_PLACE_NAME -> {
                            Toasty.showToast(this@TestAdsActivity, "Rewarded Chưa có AdPlaceName truyền vào", Toasty.WARNING)
                        }
                    }
                })
        }

        bindingView.imageRewardedInterstitialClose.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }

        bindingView.imageRewardedInterstitialClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }
    }
}