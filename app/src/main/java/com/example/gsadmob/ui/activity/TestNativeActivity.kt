package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.banner.BannerAdGsData
import com.core.gsadmob.natives.NativeUtils
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
                        }
                    }
                }
            }
        }

        AdGsManager.instance.startShimmerLiveData.observe(this) { shimmerMap ->
            shimmerMap.forEach {
                when (it.key) {
                    AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                        if (it.value) {
                            bindingView.bannerView.startShimmer()
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
            NativeUtils.loadNativeAds(lifecycleOwner = this@TestNativeActivity, activity = this@TestNativeActivity, nativeId = com.core.gsadmob.R.string.native_id, isVip = isVip, callbackStart = {
                bindingView.nativeFrame.startShimmer()
            }, callback = { nativeAd ->
                bindingView.nativeFrame.setNativeAd(if (isVip) null else nativeAd)
            })
        }

        bindingView.imageFrameClear.setOnClickListener {
            bindingView.nativeFrame.setNativeAd(null)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            NativeUtils.loadNativeAds(
                lifecycleOwner = this@TestNativeActivity,
                activity = this@TestNativeActivity,
                nativeId = com.core.gsadmob.R.string.native_id_language,
                isVip = isVip,
                callbackStart = {
                    bindingView.nativeLanguage.startShimmer()
                },
                callback = { nativeAd ->
                    bindingView.nativeLanguage.setNativeAd(if (isVip) null else nativeAd)
                })
        }

        bindingView.imageLanguageClear.setOnClickListener {
            bindingView.nativeLanguage.setNativeAd(null)
        }
    }

    override fun onDestroy() {
        AdGsManager.instance.destroyActivity()

        AdGsManager.instance.removeActive(mutableListOf<AdPlaceName>().apply {
            add(AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
        })
        super.onDestroy()
    }
}