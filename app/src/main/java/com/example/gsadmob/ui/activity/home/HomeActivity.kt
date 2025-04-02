package com.example.gsadmob.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.core.gsadmob.banner.BannerGsAdView
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.model.nativead.NativeAdGsData
import com.core.gsadmob.utils.AdGsManager
import com.core.gscore.utils.extensions.removeBlink
import com.example.gsadmob.utils.remoteConfig.AdGsRemoteExtraConfig
import com.example.gsadmob.databinding.ActivityHomeBinding
import com.example.gsadmob.ui.activity.TestAdsActivity
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.example.gsadmob.ui.activity.language.LanguageActivity
import com.example.gsadmob.ui.adapter.ImageAdapter

class HomeActivity : BaseAdsActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    override val bannerGsAdView: BannerGsAdView by lazy { bindingView.bannerView }

    private val viewModel: HomeViewModel by viewModels()

    private var adapter: ImageAdapter? = null

    private val list = mutableListOf(AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome, AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome)

    override fun getAdPlaceNameList(): MutableList<AdPlaceName> {
        return list
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        viewModel.loadData()

        viewModel.imageListLiveData.observe(this) { list ->
            adapter?.setData(list)
        }

        val manager = GridLayoutManager(this, 4)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return adapter?.let {
                    if (it.isAds(position)) 4 else 1
                } ?: 1
            }
        }

        adapter = ImageAdapter(this)
        bindingView.rvImage.adapter = adapter
        bindingView.rvImage.layoutManager = manager
        bindingView.rvImage.removeBlink()
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        bindingView.tvTestAds.setOnClickListener {
            startActivity(Intent(this, TestAdsActivity::class.java))
        }
    }

    override fun setupNative(adPlaceName: AdPlaceName, nativeAdGsData: NativeAdGsData?) {
        super.setupNative(adPlaceName, nativeAdGsData)

        adapter?.setupItemAds(nativeAdGsData?.nativeAd)
    }

    override fun startNativeShimmer(adPlaceName: AdPlaceName) {
        super.startNativeShimmer(adPlaceName)

        adapter?.startShimmer()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // tắt ứng dụng đi thì sẽ xóa hết data quảng cáo đi
        AdGsManager.instance.clearAll()
    }
}