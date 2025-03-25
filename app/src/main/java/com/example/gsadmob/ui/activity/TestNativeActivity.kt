package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestNativeActivity : BaseAdsActivity<ActivityTestNativeBinding>(ActivityTestNativeBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }
    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf(AdPlaceNameConfig.AD_PLACE_NAME_BANNER, AdPlaceNameConfig.AD_PLACE_NAME_NATIVE, AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
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

        bindingView.tvNativeFrame.setOnClickListener {
            AdGsManager.instance.registerActiveAndLoadAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.imageFrameClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            AdGsManager.instance.registerActiveAndLoadAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }

        bindingView.imageLanguageClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
    }

    override fun setupNative(adPlaceName: AdPlaceName, nativeAdGsData: NativeAdGsData?) {
        super.setupNative(adPlaceName, nativeAdGsData)
        when (adPlaceName) {
            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                bindingView.nativeFrame.setNativeAd(nativeAdGsData?.nativeAd)
            }

            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                bindingView.nativeLanguage.setNativeAd(nativeAdGsData?.nativeAd)
            }
        }
    }

    override fun startNativeShimmer(adPlaceName: AdPlaceName) {
        super.startNativeShimmer(adPlaceName)
        when (adPlaceName) {
            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                bindingView.nativeFrame.startShimmer()
            }

            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                bindingView.nativeLanguage.startShimmer()
            }
        }
    }
}