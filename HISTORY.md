**Version 1.3.23**

- Tăng tốc di chuyển BottomSheetDialogFragment của ResumeDialogFragment
- Tạo style Base.Theme.GsAdmob

```css
      <style name="Theme.GsAdmob" parent="Base.Theme.GsAdmob"/>
```

**Version 1.3.22**

- Sửa lỗi hủy quảng cáo trả thưởng không được

**Version 1.3.21**

- Fix crash Caused by java.lang.RuntimeException java.lang.NoSuchFieldException: _decisionAndIndex

**Version 1.3.20**

- AdGsRemoteConfig thêm isDebug để có thể test remoteConfig nhanh hơn

**Version 1.3.19**

- Thêm click vào icon logo quảng cáo native để mở quảng cáo

**Version 1.3.18**

- Update GsCore
- Từ giờ khi hiển thị quảng cáo xen kẽ sẽ hủy tất cả quảng cáo trả thưởng đang có đi để không thể tự hiển thị khi tải xong được
- Thêm biến isUse vào [BaseAdGsData](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/model/base/BaseAdGsData.kt) để xem quảng cáo được sử dụng chưa

**Version 1.3.17**

- Update gscore

**Version 1.3.16**

- Sửa lỗi AdGsSplashManager xử lý timeout chưa chuẩn phải dựa vào delayTime và delayRetry
- Ở bản 1.3.15 thời gian treo ở màn hình splash là 14s

**Version 1.3.15**

- Sửa lỗi mạng yếu thì RemoteConfig tải dữ liệu chậm hơn splash
- AdGsSplashManager thêm logic retry lại 1 lần để tải lại quảng cáo
- Update gscore

**Version 1.3.14**

- Sửa lỗi Fatal Exception: java.util.ConcurrentModificationException
- Thêm updateName ở AdPlaceName để thay đổi tên quảng cáo
- Thêm updateId ở AdPlaceName để thay đổi id quảng cáo
- Thêm disable ở AdPlaceName để tắt sử dụng quảng cáo

**Version 1.3.13**

- Fix lỗi khi dùng dialog GDPR thì mất callback

**Version 1.3.12**

- Thêm [AdGsRewardedManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsRewardedManager.kt) xử lý cho quảng cáo trả thưởng và kiểm tra GDPR

Nếu có nhiều quảng cáo trả thưởng thì khởi tạo như sau:

```css
      adGsRewardedManager = AdGsRewardedManager(
              activity = this,
              isDebug = BuildConfig.DEBUG
          )
```

và khi sử dụng thì gọi:

```css
      adGsRewardedManager?.showAds(
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL,
                callback = { typeShowAds ->
                
                })
```

Nếu có 1 quảng cáo trả thưởng thì khởi tạo như sau:

```css
      adGsRewardedManager = AdGsRewardedManager(
              activity = this,
              adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL,
              isDebug = BuildConfig.DEBUG
          )
```

và khi sử dụng thì gọi:

```css
      adGsRewardedManager?.showAds(               
                callback = { typeShowAds ->
                
                })
```

- Thêm TypeShowAds trong AdGsRewardedManager để trả về khi tải quảng cáo trả thưởng
- Thêm removeAdsListener cho AdGsDelayManager và AdGsSplashManager
- Xóa destroyActivity ở AdGsManager
- Thêm các cấu hình dialog xin quyền GDPR ở config_admob.xml
- Đổi time_delay_loading thành ads_time_delay_loading
- Đổi time_fake_delay thành ads_time_fake_delay
- Thêm ads_msg_gdpr: string thông báo người dùng từ chối quyền GDPR
- Thêm ads_text_grant_permission: string xin phép cấp quyền

**Version 1.3.11**

- Thêm SerializedName vào AdPlaceName
- Thêm isValidate() vào AdPlaceName
- Thêm log

**Version 1.3.10**

- Sửa lỗi load lỗi không có mạng thì chưa cập nhật các loại BaseActiveAdGsData

**Version 1.3.9**

- Sửa lỗi BannerGsAdView không gravity BOTTOM khi sử dụng layout_height wrap_content và sử dụng minHeight

**Version 1.3.8**

- Sửa lỗi AdGsSplashManager khi quảng cáo tải quảng cáo lỗi (adUnitId trống hoặc adGsType sai loại) bị treo

**Version 1.3.7**

- Thêm requiredLoadNewAds vào registerBanner, registerNativeOrBanner, registerNative

**Version 1.3.6**

- Thêm adGsListener vào registerBanner, registerNativeOrBanner, registerNative

**Version 1.3.5**

- Bỏ adPlaceName ở callbackSuccess của registerNative, registerNativeOrBanner, [registerBanner](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt)

**Version 1.3.4**

- Gom startShimmer vào setupItemAds của [BaseWithAdsAdapter](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/adapter/BaseWithAdsAdapter.kt)
- Gom startShimmer vào setBannerAdView của [BannerGsAdView](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/banner/BannerGsAdView.kt)
- Gom startShimmer vào setNativeAd của [BaseNativeAdView](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/natives/view/BaseNativeAdView.kt)
- Thêm [registerNativeOrBanner()](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt) để tải quảng cáo là có kiểu thay đổi giữa banner và native(
  thường là ở màn chọn ngôn ngữ)
- Thêm [registerBanner()](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt) để tải quảng cáo banner
- Thêm [registerNative()](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt) để tải quảng cáo native
- Cấu trúc lại BaseAdsActivity không cần tạo abstract BannerGsAdView và abstract getAdPlaceNameList nữa
- Xóa bỏ logic tự động tải quảng cáo native và banner tự động -> người dùng chủ động gọi registerNative(), registerBanner() hoặc registerNativeOrBanner()
- Xóa bỏ tagActivity đi giờ registerNative(), registerBanner() hoặc registerNativeOrBanner() sẽ tự động quản lý pause(), resume() và destroy()
- Xóa bỏ BannerLife
- Xóa bỏ clearAndRemoveActive(adPlaceNameList: MutableList<AdPlaceName>)
- Sửa các id mặc định của native bỏ custom đi xem [ads_BaseNativeCustom](https://github.com/vtabk2/GsAdmob/blob/GsAdmob/src/main/res/values/config_admob.xml)
- Đổi full_id thành interstitial_id
- Đổi full_id_without_video thành interstitial_id_without_video

**Version 1.3.3**

- Thêm style ads_Autoscroll để text có thử tự động chạy(custom native có thể dùng cho text headline)
- Thêm update ở [AdPlaceName](https://github.com/vtabk2/GsAdmob/blob/GsAdmob/src/main/java/com/core/gsadmob/model/AdPlaceName.kt)
- Sửa banner không ở cuối khi fix cứng size

**Version 1.3.2**

- Đổi tên AdPlaceNameConfig thành AdPlaceNameDefaultConfig
- Gom class vào package remoteconfig

**Version 1.3.1**

- Thêm [AdGsDelayManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsDelayManager.kt) để quản lý tải quảng cáo cần thời gian chờ(thường là quảng cáo
  app open resume)
- Thêm [time_delay_loading](https://github.com/vtabk2/GsAdmob/blob/GsAdmob/src/main/res/values/config_admob.xml) để chỉnh thời gian chờ khi tải quảng cáo(mặc định 3500, min 1000)
- Thêm [time_fake_delay](https://github.com/vtabk2/GsAdmob/blob/GsAdmob/src/main/res/values/config_admob.xml) để chỉnh thời gian giả trước khi hiển thị quảng cáo(mặc định 1000, min 500)
- Xem hướng dẫn ở [ResumeDialogFragment](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/fragment/ResumeDialogFragment.kt)

```css
      (activity as? AppCompatActivity)?.let {
            AdGsDelayManager(
                activity = it,
                fragment = this,
                adPlaceName = adPlaceName,
                callbackFinished = {
                    dismissAllowingStateLoss()
                })
        }
```

**Version 1.3.0**

- Hỗ trợ cấu hình RemoteConfig của Firebase xem ở [RemoteConfig](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/utils/remoteconfig/RemoteConfig.kt)
- Thêm [GsAdmobApplication](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/GsAdmobApplication.kt)
- Thêm ADS_DISABLE vào AdShowStatus
- Đổi APP_OPEN_AD thành APP_OPEN ở AdGsType

**Version 1.2.21**

- Thêm [SplashAdsManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsSplashManager.kt)
  được khởi tạo ở màn hình splash

```css
        SplashAdsManager(
            this@SplashActivity,
            adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_APP_OPEN,
            goToHomeCallback = {
                goToHome()
            }, initMobileAds = {
                TestApplication.applicationContext().initMobileAds()
            }, adsLoading = {
                bindingView?.clBlur?.isVisible = it
            }
        )
```

- Fix BlurView

**Version 1.2.20**

- Thêm biến showLog ở registerCoroutineScope() để có thể hiển thị log nội bộ của AdGsManager
- Cải tiến clearAndRemoveActive() khi truyền 1 danh sách vào thì chỉ notify 1 lần thôi
- Thêm BannerLife
- Thêm tagActivity ở AdPlaceName để xác định tên của Activity đang dùng AdPlaceName này để tự động BannerLife
- Thêm log

**Version: 1.2.19**

- Thêm log error load quảng cáo
- Thêm destroy của banner và native
- Thêm NATIVE_AD_DEBUGGER_ENABLED

**Version: 1.2.18**

- Thêm callbackChangeVip ở registerCoroutineScope để có thể xử lý cập nhật giao diện khi thay đổi vip
- Ở BaseAdsActivity thêm hàm updateUiWithVip để cập nhật giao diện khi thay đổi vip
- Thêm RewardItem vào onShowFinishSuccess() để có thể lấy đuợc cấu hình phần thưởng sau khi xem quảng cáo trả thưởng

**Version: 1.2.17**

- Lưu lại currentKeyVipList khi khởi tạo để khi dùng hàm kiểm tra vip isFullVersion() không cần truyền keyVipList vào nữa mà dùng currentKeyVipList luôn