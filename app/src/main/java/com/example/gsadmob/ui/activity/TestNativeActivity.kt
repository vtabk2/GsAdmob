package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.banner.BannerAdGsData
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestNativeActivity : BaseMVVMActivity<ActivityTestNativeBinding>() {

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

        AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
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

    override fun onPause() {
        bindingView.bannerView.pause()
        super.onPause()
    }

    override fun onResume() {
        bindingView.bannerView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        bindingView.bannerView.destroy()

        AdGsManager.instance.destroyActivity()

        AdGsManager.instance.clearAndRemoveActive(mutableListOf<AdPlaceName>().apply {
            add(AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
            add(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
            add(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        })
        super.onDestroy()
    }
}