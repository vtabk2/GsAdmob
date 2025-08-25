package com.example.gsadmob.ui.activity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.core.gsadmob.callback.AdGsExtendListener
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.utils.AdGsManager
import com.core.gscore.utils.extensions.removeBlink
import com.example.gsadmob.TestApplication
import com.example.gsadmob.databinding.ActivityHomeBinding
import com.example.gsadmob.ui.activity.TestAdsActivity
import com.example.gsadmob.ui.activity.base.BasePermissionActivity
import com.example.gsadmob.ui.activity.language.LanguageActivity
import com.example.gsadmob.ui.adapter.ImageAdapter
import com.example.gsadmob.utils.extensions.PERMISSION_CAMERA
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
import com.gs.core.ui.view.toasty.Toasty

class HomeActivity : BasePermissionActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
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
            activity = this,
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = true,
            adGsListener = object : AdGsListener {

            },
            adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: adPlaceNameBannerHome")
                }
            }
        )

        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            state = Lifecycle.State.STARTED,
            nativeGsAdView = bindingView.nativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
//                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer, useShimmer = true)
            },
            adGsListener = object : AdGsListener {

            },
            adGsExtendListener = object : AdGsExtendListener{
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: adPlaceNameNativeHome")
                }
            }
        )
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        bindingView.tvRestart.setOnClickListener {
            AdGsManager.instance.clearAndRemoveListener(adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome)
            AdGsManager.instance.clearAndRemoveListener(adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome)

            val intent = Intent(this@HomeActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindingView.tvTestAds.setOnClickListener {
            startActivity(Intent(this, TestAdsActivity::class.java))
        }

        bindingView.imageReload.setOnClickListener {
            TestApplication.applicationContext().canShowAppOpenResume = true
            viewModel.loadData()
        }

        bindingView.tvGetPermission.setOnClickListener {
            TestApplication.applicationContext().canShowAppOpenResume = false
            checkPermission(PERMISSION_CAMERA, callback = { granted ->
                if (granted) {
                    goToOtherHasPermission()
                }
            })
        }

        bindingView.imageCheckPause.setOnClickListener {
            Toasty.showToast(this@HomeActivity, "getPauseApp = ${AdGsManager.instance.getPauseApp()}", Toasty.NORMAL)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // tắt ứng dụng đi thì sẽ xóa hết data quảng cáo đi
        AdGsManager.instance.clearAll()
    }
}