package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.utils.AdGsManager
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityTestNativeBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestNativeActivity : BaseMVVMActivity<ActivityTestNativeBinding>() {

    private var isVip: Boolean = false

    override fun getViewBinding(): ActivityTestNativeBinding {
        return ActivityTestNativeBinding.inflate(layoutInflater)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        lifecycleScope.launch {
            async {
                AdGsManager.instance.isVipFlow.collect {
                    isVip = it
                    if (isVip) {
                        bindingView.tvActiveVip.text = "Vip Active"
                        bindingView.nativeFrame.setNativeAd(null)
                        bindingView.nativeLanguage.setNativeAd(null)
                    } else {
                        bindingView.tvActiveVip.text = "Vip Inactive"
                    }
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()

        bindingView.tvActiveVip.setOnClickListener {
            AdGsManager.instance.notifyVip(isVip = !isVip)
        }

        bindingView.tvNativeFrame.setOnClickListener {
            NativeUtils.loadNativeAds(lifecycleOwner = this@TestNativeActivity, activity = this@TestNativeActivity, nativeId = com.core.gsadmob.R.string.native_id, isVip = isVip, callbackStart = {
                bindingView.nativeFrame.startShimmer()
            }, callback = { nativeAd ->
                bindingView.nativeFrame.setNativeAd(if (isVip) null else nativeAd)
            })
        }

        bindingView.imageFrameClear.setOnClickListener {
            bindingView.nativeFrame.setNativeAd(null)
        }

        bindingView.tvNativeLanguage.setOnClickListener {
            NativeUtils.loadNativeAds(
                lifecycleOwner = this@TestNativeActivity,
                activity = this@TestNativeActivity,
                nativeId = com.core.gsadmob.R.string.native_id_language,
                isVip = isVip,
                callbackStart = {
                    bindingView.nativeLanguage.startShimmer()
                },
                callback = { nativeAd ->
                    bindingView.nativeLanguage.setNativeAd(if (isVip) null else nativeAd)
                })
        }

        bindingView.imageLanguageClear.setOnClickListener {
            bindingView.nativeLanguage.setNativeAd(null)
        }
    }

    override fun onDestroy() {
        AdGsManager.instance.destroyActivity()
        super.onDestroy()
    }
}