package com.example.gsadmob.ui.activity.home

import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.example.gsadmob.AdGsRemoteExtraConfig
import com.example.gsadmob.databinding.ActivityHomeBinding
import com.example.gsadmob.ui.activity.base.BaseAdsActivity

class HomeActivity : BaseAdsActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }

    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return mutableListOf<AdPlaceName>(
            AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome
        )
    }

    override fun setupNative(adPlaceName: AdPlaceName, nativeAdGsData: NativeAdGsData?) {
        super.setupNative(adPlaceName, nativeAdGsData)

    }

    override fun startNativeShimmer(adPlaceName: AdPlaceName) {
        super.startNativeShimmer(adPlaceName)

    }

    override fun onBackPressed() {
        super.onBackPressed()

        // tắt ứng dụng đi thì sẽ xóa hết data quảng cáo đi
        AdGsManager.instance.clearAll()
    }
}