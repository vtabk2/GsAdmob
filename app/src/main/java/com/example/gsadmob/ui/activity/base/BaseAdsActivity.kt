package com.example.gsadmob.ui.activity.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.core.gsadmob.utils.AdGsRewardedManager
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.gs.core.ui.view.toasty.Toasty

abstract class BaseAdsActivity<VB : ViewBinding>(inflateBinding: (LayoutInflater) -> VB) : BaseMVVMActivity<VB>(inflateBinding) {
    var adGsRewardedManager: AdGsRewardedManager? = null

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        adGsRewardedManager = AdGsRewardedManager(
            activity = this,
            isDebug = BuildConfig.DEBUG
        )
    }

    fun updateUiWithVip(isVip: Boolean) {
        if (isVip) {
            Toasty.showToast(this, "Bạn đã là thành viên vip", Toasty.SUCCESS)
        } else {
            Toasty.showToast(this, "Bạn chưa là thành viên vip", Toasty.WARNING)
        }
    }
}