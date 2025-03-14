package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.BannerAdGsData
import com.core.gsadmob.model.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FirstActivity : BaseMVVMActivity<ActivityTestNativeBinding>() {

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

        AdGsManager.instance.shimmerLiveData.observe(this) { shimmerMap ->
            shimmerMap.forEach {
                when (it.key) {
                    AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                        if (it.value) {
                            bindingView.bannerView.startShimmer()
                        }
                    }
                    AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                        if (it.value) {
                            bindingView.nativeFrame.startShimmer()
                        }
                    }

                    AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                        if (it.value) {
                            bindingView.nativeLanguage.startShimmer()
                        }
                    }
                }
            }
            AdGsManager.instance.clearAllShimmer()
        }

        AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            AdGsManager.instance.notifyVip(isVip = !isVip)
        }

        bindingView.tvNativeFrame.setOnClickListener {
            AdGsManager.instance.activeAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
            AdGsManager.instance.loadAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE, requiredLoadNewAds = true)
        }

        bindingView.imageFrameClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            AdGsManager.instance.activeAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
            AdGsManager.instance.loadAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE, requiredLoadNewAds = true)
        }

        bindingView.imageLanguageClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
    }

    override fun onDestroy() {
        AdGsManager.instance.destroyActivity()
        super.onDestroy()
    }
}