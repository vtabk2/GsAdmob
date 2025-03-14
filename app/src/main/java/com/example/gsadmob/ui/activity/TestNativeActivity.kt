package com.example.gsadmob.ui.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
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
        }

        bindingView.imageFrameClear.setOnClickListener {
        }

        bindingView.tvNativeLanguage.setOnClickListener {
        }

        bindingView.imageLanguageClear.setOnClickListener {
        }
    }

    override fun onDestroy() {
        AdGsManager.instance.destroyActivity()
        super.onDestroy()
    }
}