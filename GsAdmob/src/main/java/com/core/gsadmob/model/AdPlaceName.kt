package com.core.gsadmob.model

data class AdPlaceName(
    /**
     * Tên của quảng cáo = loại quảng cáo + tên màn hình (Ví dụ : Banner home)
     */
    var name: String = "",
    /**
     * id của quảng cáo
     */
    var adUnitId: Int = 0,
    /**
     * Cái này dùng cho quảng cáo app open resume, cái fragment mà app open resume cần mở lên trước
     */
    var fragmentTagAppOpenResumeResId: Int = 0,
    /**
     * Có tự động tải lại quảng cáo khi đóng không dùng cho quảng cáo loại BaseShowAdGsData (app open, interstitial, rewarded, rewarded interstitial)
     */
    var autoReloadWhenDismiss: Boolean = false,
    /**
     * Thời gian tải lại quảng cáo giữa các lần, đây là cấu hình mặc định ban đầu thôi, khi data đã thay đổi trong AdGsManager sẽ ko dùng nữa
     */
    var delayTime: Long = 0L,
    /**
     * Loại quảng cáo được cấu hình ở AdGsType
     */
    var adGsType: AdGsType = AdGsType.INTERSTITIAL
)