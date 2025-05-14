package com.core.gsadmob.callback

import com.google.android.gms.ads.rewarded.RewardItem

interface AdGsListener {
    /**
     * Gom 3 trường hợp tải lỗi, không hiển thị được quảng cáo và tắt quảng cáo lại isFailed = true cho trường hợp tải quảng cáo lỗi
     * Trong nhiều trường hợp thì logic của chúng đều được xử lý giống nhau
     */
    fun onAdClose(isFailed: Boolean = false) {}

    /**
     * Quảng cáo tải thành công
     */
    fun onAdSuccess() {}

    /**
     *  Dùng cho quảng cáo trả thưởng
     */
    fun onShowFinishSuccess(rewardItem: RewardItem) {}

    /**
     * Quảng cáo đang hiển thị
     */
    fun onAdShowing() {}
}