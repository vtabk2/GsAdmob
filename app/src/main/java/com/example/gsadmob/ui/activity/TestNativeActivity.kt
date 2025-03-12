package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
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

        bindingView.bannerView.loadAds(isVip = false, adUnitId = com.core.gsadmob.R.string.banner_id)
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            AdGsManager.instance.notifyVip(isVip = !isVip)
        }

        bindingView.tvNativeFrame.setOnClickListener {
            AdGsManager.instance.activeAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
            AdGsManager.instance.loadAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE, requiredLoadNewAds = true, callbackStart = {
                bindingView.nativeFrame.startShimmer()
            })
        }

        bindingView.imageFrameClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            AdGsManager.instance.activeAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
            AdGsManager.instance.loadAd(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE, requiredLoadNewAds = true, callbackStart = {
                bindingView.nativeLanguage.startShimmer()
            })
        }

        bindingView.imageLanguageClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
    }
}