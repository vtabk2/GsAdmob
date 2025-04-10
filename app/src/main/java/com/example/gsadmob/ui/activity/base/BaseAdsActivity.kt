package com.example.gsadmob.ui.activity.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.core.gsadmob.utils.AdGsRewardedManager
import com.core.gsadmob.utils.AdGsRewardedManager.TypeShowAds
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.R
import com.gs.core.ui.view.toasty.Toasty

abstract class BaseAdsActivity<VB : ViewBinding>(inflateBinding: (LayoutInflater) -> VB) : BaseMVVMActivity<VB>(inflateBinding) {
    var adGsRewardedManager: AdGsRewardedManager? = null

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        adGsRewardedManager = AdGsRewardedManager(
            activity = this,
            callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@BaseAdsActivity, "Rewarded SUCCESS", Toasty.SUCCESS)
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@BaseAdsActivity, "Rewarded FAILED", Toasty.ERROR)
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@BaseAdsActivity, R.string.check_network_connection, Toasty.WARNING)
                    }

                    TypeShowAds.SSL_HANDSHAKE -> {
                        Toasty.showToast(this@BaseAdsActivity, R.string.text_please_check_time, Toasty.WARNING)
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@BaseAdsActivity, "Rewarded CANCEL", Toasty.WARNING)
                    }

                    TypeShowAds.NO_AD_PLACE_NAME -> {
                        Toasty.showToast(this@BaseAdsActivity, "Rewarded Chưa có AdPlaceName truyền vào", Toasty.WARNING)
                    }
                }
            },
            callbackShow = { adShowStatus ->

            },
            callbackStart = {
                Toasty.showToast(this@BaseAdsActivity, "Bắt đầu tải quảng cáo", Toasty.WARNING)
            },
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