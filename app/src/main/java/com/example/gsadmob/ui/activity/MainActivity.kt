package com.example.gsadmob.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.interstitial.InterstitialGsWithDelayUtils
import com.core.gsadmob.natives.AdsMode
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.natives.view.BaseNativeAdView
import com.core.gsadmob.utils.extensions.getAndroidId
import com.core.gsadmob.utils.extensions.md5
import com.core.gscore.utils.network.LiveDataNetworkStatus
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var bindingView: ActivityMainBinding
    private val deviceTestList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        initMobileAds()

        val builder = BaseNativeAdView.Builder().apply {
            adsLayoutId = R.layout.ad_native_test
            adsLayoutShimmerId = R.layout.ad_native_test_shimmer
//            adsLayoutShimmerId = 0 // nếu không muốn dùng shimmer
            adsHeadlineId = R.id.ad_headline_test
            adsStarsId = R.id.ad_stars_test
            adsAppIconId = R.id.ad_app_icon_test
            adsCallToActionId = R.id.ad_call_to_action_test
            adsViewId = R.id.ad_view_test
            adsShimmerId = R.id.ad_view_test_shimmer
            adsNativeViewRoot = R.style.ads_NativeTestRoot
            adsMode = AdsMode.CUSTOM
        }
//        bindingView.nativeTest1.applyBuilder(builder)


        bindingView.nativeTest1.setStyle(com.core.gsadmob.R.style.NativeAlbum)

//        bindingView.nativeTest1.applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
//
//        bindingView.nativeTest2.applyBuilder(NativeDefaultConfig.BUILDER_FONT)
//
//        bindingView.nativeTest3.applyBuilder(NativeDefaultConfig.BUILDER_FRAME)
//
//        bindingView.nativeTest4.applyBuilder(NativeDefaultConfig.BUILDER_LANGUAGE)
//
//        bindingView.nativeTest5.applyBuilder(NativeDefaultConfig.BUILDER_SHARE)
//
//        bindingView.nativeTest6.applyBuilder(NativeDefaultConfig.BUILDER_STICKER)
//
//        bindingView.nativeTest7.applyBuilder(NativeDefaultConfig.BUILDER_TEMPLATE)
//
//        bindingView.nativeTest8.applyBuilder(NativeDefaultConfig.BUILDER_VIP)

        val liveDataNetworkStatus = LiveDataNetworkStatus(this)
        liveDataNetworkStatus.observe(this) { connect ->
            if (connect) {
                loadAds(true)
            }
        }

        bindingView.bannerView.registerDelayTime(10)
        loadAds(false)

        InterstitialGsWithDelayUtils.instance.registerDelayTime(10)

        bindingView.tvClear.setOnClickListener {
            InterstitialGsWithDelayUtils.instance.clearWithId()
        }

        bindingView.tvClearAll.setOnClickListener {
            InterstitialGsWithDelayUtils.instance.clearAll()
        }

        bindingView.tvShowInterstitial.setOnClickListener {
            InterstitialGsWithDelayUtils.instance.checkReloadInterstitialAdIfNeed(this, isVip = false, callback = { canShow ->
                if (canShow) {
                    InterstitialGsWithDelayUtils.instance.showInterstitialAd(this, isVip = false)
                } else {
                    Toast.makeText(this, "Không có sẵn ad", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun initMobileAds() {
        deviceTestList.add(md5(getAndroidId(this)).uppercase())

        val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(deviceTestList).build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    override fun onResume() {
        super.onResume()

        InterstitialGsWithDelayUtils.instance.loadAd(this, isVip = false)
    }

    private fun loadAds(isReload: Boolean) {
        bindingView.bannerView.loadAds(isVip = false)

        NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
//            bindingView.nativeTest0.startShimmer()
            bindingView.nativeTest1.startShimmer()
//            bindingView.nativeTest2.startShimmer()
//            bindingView.nativeTest3.startShimmer()
//            bindingView.nativeTest4.startShimmer()
//            bindingView.nativeTest5.startShimmer()
//            bindingView.nativeTest6.startShimmer()
//            bindingView.nativeTest7.startShimmer()
//            bindingView.nativeTest8.startShimmer()
//
//            bindingView.nativeCustom1.startShimmer()
//            bindingView.nativeCustom2.startShimmer()
        }, callback = { nativeAd ->
            Handler(Looper.getMainLooper()).postDelayed({
                //            bindingView.nativeTest0.setNativeAd(nativeAd)
                bindingView.nativeTest1.setNativeAd(nativeAd)
//            bindingView.nativeTest2.setNativeAd(nativeAd)
//            bindingView.nativeTest3.setNativeAd(nativeAd)
//            bindingView.nativeTest4.setNativeAd(nativeAd)
//            bindingView.nativeTest5.setNativeAd(nativeAd)
//            bindingView.nativeTest6.setNativeAd(nativeAd)
//            bindingView.nativeTest7.setNativeAd(nativeAd)
//            bindingView.nativeTest8.setNativeAd(nativeAd)
//
//            bindingView.nativeCustom1.setNativeAd(nativeAd)
//            bindingView.nativeCustom2.setNativeAd(nativeAd)
            }, 2000)
        })
    }
}