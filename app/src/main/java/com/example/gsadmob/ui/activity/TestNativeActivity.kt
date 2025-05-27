package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameDefaultConfig
import com.core.gsadmob.utils.preferences.VipPreferences
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestNativeActivity : BaseMVVMActivity<ActivityTestNativeBinding>(ActivityTestNativeBinding::inflate) {
    private var isHide = false

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
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerTestNative,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false
        )

        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE,
            nativeGsAdView = bindingView.nativeFrame,
            useShimmer = false
        )

        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE,
            nativeGsAdView = bindingView.nativeLanguage,
            useShimmer = false
        )
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            VipPreferences.instance.isPro = !VipPreferences.instance.isPro
        }

        bindingView.tvNativeFrame.setOnClickListener {
            AdGsManager.instance.registerNative(
                lifecycleOwner = this,
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE,
                nativeGsAdView = bindingView.nativeFrame
            )
        }

        bindingView.imageFrameClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE)
        }

        bindingView.imageFrameHide.setOnClickListener {
            isHide = !isHide

            if (isHide) {
                bindingView.nativeFrame.hide()
            } else {
                bindingView.nativeFrame.show()
            }
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            AdGsManager.instance.registerNative(
                lifecycleOwner = this,
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE,
                nativeGsAdView = bindingView.nativeLanguage
            )
        }

        bindingView.imageLanguageClear.setOnClickListener {
            AdGsManager.instance.clearWithAdPlaceName(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE)
        }
    }
}