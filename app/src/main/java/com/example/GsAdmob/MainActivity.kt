package com.example.GsAdmob

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.interstitial.InterstitialGsWithDelayUtils
import com.core.gsadmob.natives.AdsMode
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.natives.view.BaseNativeAdView
import com.example.GsAdmob.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private lateinit var bindingView: ActivityMainBinding
    private val deviceTestList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        initMobileAds()

        val builder = BaseNativeAdView.Builder().apply {
            adLayoutId = R.layout.ad_native_test
            adLayoutShimmerId = R.layout.ad_native_test_shimmer
//            adLayoutShimmerId = 0 // nếu không muốn dùng shimmer
            adHeadlineId = R.id.ad_headline_test
            adStarsId = R.id.ad_stars_test
            adAppIconId = R.id.ad_app_icon_test
            adCallToActionId = R.id.ad_call_to_action_test
            adViewId = R.id.ad_view_test
            adShimmerId = R.id.ad_view_test_shimmer
            adsNativeViewRoot = R.style.ads_NativeTestRoot
            adsMode = AdsMode.CUSTOM
        }
        bindingView.nativeTest1.applyBuilder(builder)

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

    fun initMobileAds() {
        deviceTestList.add(md5(getAndroidId()).uppercase())

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

    private fun md5(input: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(input.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(java.lang.String.format("%02X", 0xFF and messageDigest[i].toInt()))
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(): String {
        return try {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            ""
        }
    }
}