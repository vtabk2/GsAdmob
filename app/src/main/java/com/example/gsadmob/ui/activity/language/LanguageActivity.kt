package com.example.gsadmob.ui.activity.language

import android.os.Bundle
import com.core.gsadmob.utils.AdGsManager
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityLanguageBinding
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig

class LanguageActivity : BaseMVVMActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        AdGsManager.instance.registerNativeOrBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameLanguage,
            bannerGsAdView = bindingView.bannerView,
            nativeGsAdView = bindingView.nativeLanguage,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->

            },
            callbackFailed = {

            }
        )
    }
}