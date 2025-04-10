package com.example.gsadmob.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
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
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }

        bindingView.tvInterstitialWithoutVideo.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }

        bindingView.tvRewarded.setOnClickListener {
            adGsRewardedManager?.showAds(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.imageRewardedClose.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.imageRewardedClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.tvRewardedInterstitial.setOnClickListener {
            adGsRewardedManager?.showAds(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }

        bindingView.imageRewardedInterstitialClose.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }

        bindingView.imageRewardedInterstitialClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }
    }
}