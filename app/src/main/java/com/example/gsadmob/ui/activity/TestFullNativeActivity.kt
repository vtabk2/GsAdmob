package com.example.gsadmob.ui.activity

import android.os.Bundle
import com.core.gsadmob.utils.AdGsManager
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityTestFullNativeBinding
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig

class TestFullNativeActivity : BaseMVVMActivity<ActivityTestFullNativeBinding>(ActivityTestFullNativeBinding::inflate) {

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeTestFull,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                bindingView.nativeAlbum.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeFont.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeFrame.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeLanguage.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeShare.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeSticker.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeTemplate.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeVip.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
                bindingView.nativeCustom.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
            }
        )
    }
}