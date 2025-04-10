package com.core.gsadmob.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

data class AdPlaceName(
    /**
     * Tên của quảng cáo = loại quảng cáo + tên màn hình(Ví dụ : banner_home)
     */
    @SerializedName("name") var name: String = "",
    /**
     * id của quảng cáo
     */
    @SerializedName("adUnitId") var adUnitId: String = "",
    /**
     * Có tự động tải lại quảng cáo khi đóng không dùng cho quảng cáo loại BaseShowAdGsData(app open, interstitial, rewarded, rewarded interstitial)
     */
    @SerializedName("autoReloadWhenDismiss") var autoReloadWhenDismiss: Boolean = false,
    /**
     * Thời gian tải lại quảng cáo giữa các lần, đây là cấu hình mặc định ban đầu thôi, khi data đã thay đổi trong AdGsManager sẽ ko dùng nữa
     */
    @SerializedName("delayTime") var delayTime: Long = 0L,
    /**
     * Dùng để xác định quảng cáo có được dùng không(thường dùng khi cấu hình trên firebase tắt bật)
     * isEnable = true tức là ứng dụng có sử dụng
     * isEnable = false tức là ứng dụng không sử dụng
     */
    @SerializedName("isEnable") var isEnable: Boolean = true,
    /**
     * Loại quảng cáo được cấu hình ở AdGsType
     */
    @SerializedName("adGsType") var adGsType: AdGsType = AdGsType.INTERSTITIAL
) {

    fun update(adPlaceName: AdPlaceName): AdPlaceName {
        name = adPlaceName.name
        adUnitId = adPlaceName.adUnitId
        autoReloadWhenDismiss = adPlaceName.autoReloadWhenDismiss
        delayTime = adPlaceName.delayTime
        isEnable = adPlaceName.isEnable
        adGsType = adPlaceName.adGsType
        return this
    }

    fun update(name: String, adUnitId: String): AdPlaceName {
        updateName(name)
        updateId(adUnitId)
        return this
    }

    fun updateName(name: String): AdPlaceName {
        this.name = name
        return this
    }

    fun updateId(adUnitId: String): AdPlaceName {
        if (!isValidate()) {
            this.adUnitId = adUnitId
        }
        return this
    }

    fun disable(): AdPlaceName {
        isEnable = false
        return this
    }

    fun isValidate(): Boolean {
        return !TextUtils.isEmpty(adUnitId)
    }
}