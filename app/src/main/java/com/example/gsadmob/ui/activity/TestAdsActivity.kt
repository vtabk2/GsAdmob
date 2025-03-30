package com.example.gsadmob.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.databinding.ActivityTestAdsBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.gs.core.ui.view.toasty.Toasty
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestAdsActivity : BaseAdsActivity<ActivityTestAdsBinding>(ActivityTestAdsBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }
    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf(AdPlaceNameConfig.instance.AD_PLACE_NAME_BANNER_HOME.apply {
            tagActivity = TestAdsActivity::class.java.simpleName
        })
    }

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
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
//            VipPreferences.instance.save(VipPreferences.KEY_IS_PRO, !VipPreferences.instance.load(VipPreferences.KEY_IS_PRO))
            VipPreferences.instance.isPro = !VipPreferences.instance.isPro
        }

        bindingView.tvInterstitial.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_INTERSTITIAL)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }

        bindingView.tvInterstitialWithoutVideo.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }

        bindingView.tvRewarded.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED, isCancel = false)
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
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.imageRewardedClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED)
        }

        bindingView.tvRewardedInterstitial.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL, isCancel = false)
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
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }

        bindingView.imageRewardedInterstitialClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // tắt ứng dụng đi thì sẽ xóa hết data quảng cáo đi
        AdGsManager.instance.clearAll()
    }
}