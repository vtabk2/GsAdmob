package com.example.gsadmob.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.core.gsadmob.utils.AdGsManager
import com.core.gscore.utils.extensions.removeBlink
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityHomeBinding
import com.example.gsadmob.ui.activity.TestAdsActivity
import com.example.gsadmob.ui.activity.language.LanguageActivity
import com.example.gsadmob.ui.adapter.ImageAdapter
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig

class HomeActivity : BaseMVVMActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()

    private var adapter: ImageAdapter? = null

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
                    if (it.isAds(position) || it.isTitle(position)) 4 else 1
                } ?: 1
            }
        }

        adapter = ImageAdapter(this)
        bindingView.rvImage.adapter = adapter
        bindingView.rvImage.layoutManager = manager
        bindingView.rvImage.removeBlink()

        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView
        )

        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
            }
        )
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        bindingView.tvTestAds.setOnClickListener {
            startActivity(Intent(this, TestAdsActivity::class.java))
        }

        bindingView.imageReload.setOnClickListener {
            viewModel.loadData()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // tắt ứng dụng đi thì sẽ xóa hết data quảng cáo đi
        AdGsManager.instance.clearAll()
    }
}