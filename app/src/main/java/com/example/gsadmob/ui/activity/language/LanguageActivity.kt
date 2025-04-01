package com.example.gsadmob.ui.activity.language

import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.example.gsadmob.AdGsRemoteExtraConfig
import com.example.gsadmob.databinding.ActivityLanguageBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity

class LanguageActivity : BaseAdsActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }

    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf(AdGsRemoteExtraConfig.instance.adPlaceNameLanguage)
    }

    override fun setupNative(adPlaceName: AdPlaceName, nativeAdGsData: NativeAdGsData?) {
        super.setupNative(adPlaceName, nativeAdGsData)
        bindingView.nativeLanguage.setNativeAd(nativeAdGsData?.nativeAd)
    }

    override fun startNativeShimmer(adPlaceName: AdPlaceName) {
        super.startNativeShimmer(adPlaceName)
        bindingView.nativeLanguage.startShimmer()
    }
}