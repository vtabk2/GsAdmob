package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.banner.BannerAdGsData
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestNativeActivity : BaseAdsActivity<ActivityTestNativeBinding>() {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }
    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf<AdPlaceName>().apply {
            add(AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
            add(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
            add(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
    }

    private var isVip: Boolean = false

    override fun getViewBinding(): ActivityTestNativeBinding {
        return ActivityTestNativeBinding.inflate(layoutInflater)
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

            async {
                AdGsManager.instance.adGsDataMapMutableStateFlow.collect {
                    it.forEach { adGsDataMap ->
                        when (adGsDataMap.key) {
                            AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.bannerView.startShimmer()
                                } else {
                                    bindingView.bannerView.setBannerAdView((adGsDataMap.value as? BannerAdGsData)?.bannerAdView)
                                }
                            }

                            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.nativeFrame.startShimmer()
                                } else {
                                    bindingView.nativeFrame.setNativeAd((adGsDataMap.value as? NativeAdGsData)?.nativeAd)
                                }
                            }

                            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.nativeLanguage.startShimmer()
                                } else {
                                    bindingView.nativeLanguage.setNativeAd((adGsDataMap.value as? NativeAdGsData)?.nativeAd)
                                }
                            }
                        }
                    }
                }
            }
        }

        AdGsManager.instance.startShimmerLiveData.observe(this) { shimmerMap ->
            shimmerMap.forEach {
                if (it.value) {
                    when (it.key) {
                        AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                            bindingView.bannerView.startShimmer()
                        }

                        AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                            bindingView.nativeFrame.startShimmer()
                        }

                        AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                            bindingView.nativeLanguage.startShimmer()
                        }
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

        bindingView.tvNativeFrame.setOnClickListener {
            AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.imageFrameClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
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