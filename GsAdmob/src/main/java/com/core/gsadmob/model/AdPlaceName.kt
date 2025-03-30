package com.core.gsadmob.model

data class AdPlaceName(
    /**
     * Tên của quảng cáo = loại quảng cáo + tên màn hình(Ví dụ : banner_home)
     */
    var name: String = "",
    /**
     * id của quảng cáo
     */
    var adUnitId: String = "",
    /**
     * Có tự động tải lại quảng cáo khi đóng không dùng cho quảng cáo loại BaseShowAdGsData(app open, interstitial, rewarded, rewarded interstitial)
     */
    var autoReloadWhenDismiss: Boolean = false,
    /**
     * Thời gian tải lại quảng cáo giữa các lần, đây là cấu hình mặc định ban đầu thôi, khi data đã thay đổi trong AdGsManager sẽ ko dùng nữa
     */
    var delayTime: Long = 0L,
    /**
     * Tên Activity chứa quảng cáo này thường dùng để xử lý pause(), resume(), destroy() banner
     */
    var tagActivity: String = "",
    /**
     * Dùng để xác định quảng cáo có được dùng không(thường dùng khi cấu hình trên firebase tắt bật)
     * isEnable = true tức là ứng dụng có sử dụng
     * isEnable = false tức là ứng dụng không sử dụng
     */
    var isEnable: Boolean = true,
    /**
     * Loại quảng cáo được cấu hình ở AdGsType
     */
    var adGsType: AdGsType = AdGsType.INTERSTITIAL
)