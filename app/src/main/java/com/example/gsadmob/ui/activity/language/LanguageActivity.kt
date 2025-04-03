package com.example.gsadmob.ui.activity.language

import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.example.gsadmob.databinding.ActivityLanguageBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig

class LanguageActivity : BaseAdsActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }

    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf(AdGsRemoteExtraConfig.instance.adPlaceNameLanguage)
    }

    override fun setupNative(adPlaceName: AdPlaceName, nativeAdGsData: NativeAdGsData?, isStartShimmer: Boolean) {
        super.setupNative(adPlaceName, nativeAdGsData, isStartShimmer)

        bindingView.nativeLanguage.setNativeAd(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
    }
}