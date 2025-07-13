**Version 1.6.6**

- Sửa quảng cáo banner collapsible từ giờ có thể tự động mở rộng lần đầu

**Version 1.6.5**

- Thêm `ads_marginHorizontal`
- Thêm `ads_marginBottom`

**Version 1.6.4**

- Sửa lỗi ko khởi tạo được `deviceTestList` khi build debug

**Version 1.6.1**
- Thay đổi vị trí lưu `lastShowTime`. Từ bây giờ `lastShowTime` sẽ được tính khi đóng quảng cáo chứ không phải khi quảng cáo được hiển thị
- 2 loại quảng cáo ảnh hưởng là `Interstitial` và `App Open`

**Version 1.6.0**

- Đổi isRequestingPermission thành isResetPause
- Sửa lỗi không cập nhật trạng thái isPause khi đã mua vip

**Version 1.5.7**

- Tối ưu lại `GsAdmobApplication`

**Version 1.5.5**

- Thêm `isRequestingPermission` để xử lý trường hợp khi xin cấp quyền thì isPause bị chuyển thành true

**Version 1.5.4**

- Tối ưu lại `GsAdmobApplication`

**Version 1.5.3**

- Sửa `log` của `LogExtensions` để có thể dùng `showLog` của `AdGsManager`

**Version 1.5.2**

- Thêm `setOnCancelListener` cho `GdprPermissionDialog`

**Version 1.5.1**

- Thêm `getNativeGsAdView` ở `BaseWithAdsAdapter`
- Tối ưu lại `AdResumeDialogFragment`
- Đẩy `delayShowTime` và `lastShowTime` về cho `BaseShowAdGsData`

**Version 1.5.0**

- Sửa style `ads_Autoscroll`

**Version 1.4.8**

- Xóa `callbackChangeVip` ở `AdGsManager.registerCoroutineScope()`
- Thêm 2 function gọi hiển thị nhanh 2 quảng cáo Interstitial mặc định `showInterstitial`, `showInterstitialWithoutVideo`

  ```css
        AdGsManager.instance.showInterstitial()

        AdGsManager.instance.showInterstitialWithoutVideo()
  ```    

**Version 1.4.7**
- Sửa chức năng callbackChangeVip từ giờ sẽ chỉ trả về currentActivity của ứng dụng

- Thêm `whitePackageNameList` ở `AdGsManager.registerCoroutineScope()` để chứa các `package name` của ứng dụng nếu ứng dụng có `applicationId != application.packageName`

**Version 1.4.6**

- Thêm logic timeout kiểm tra CMP/GDPR ở `GoogleMobileAdsConsentManager` các class ảnh hưởng `AdGsRewardedManager` và `AdGsSplashManager`

  ```css
       AdGsSplashManager(
            activity = this@SplashActivity,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameSplash,
            onRetryAdPlaceNameListener = object : AdGsSplashManager.OnRetryAdPlaceNameListener {
                override fun getAdPlaceName(): AdPlaceName {
                    return AdGsRemoteExtraConfig.instance.adPlaceNameSplash
                }
            },
            goToHomeCallback = {
                goToHome()
            }, initMobileAds = {
                TestApplication.applicationContext().initMobileAds()
            }, adsLoading = {
                bindingView?.clBlur?.isVisible = it
            }, timeout = 3500, isDebug = BuildConfig.DEBUG
        )
  ```

**Version 1.4.5**
- Thêm tính năng tạm ẩn hiển thị quảng cáo native

- Tạm ẩn quảng cáo native

  ```css
        bindingView.nativeFrame.hide()
  ```
- Hiển thị lại quảng cáo native

  ```css
        bindingView.nativeFrame.show()
  ```

**Version 1.4.4**
- Thêm `delayShowTime` ở `AdPlaceName` với mục đích quản lý thời gian giữa 2 lần hiển thị của quảng cáo `Interstitial` và `App Open`
- Cập nhật thời gian thực bằng `AdGsManager.instance.registerDelayShowTime`
- Thêm các function `updateDelayTime` và `updateDelayShowTime` cho `AdPlaceName`

    ```css
          AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME.updateDelayShowTime(delayShowTime = 20)
    ```

**Version 1.4.3**
- Thêm `AdGsExtendListener` để bắt sự kiện mở quảng cáo
- Banner thêm `adGsExtendListener = `

  ```css
        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false,
            adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: adPlaceNameBannerHome")
                }
            }
        )
  ```
- Native thêm `adGsExtendListener = `

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE,
            nativeGsAdView = bindingView.nativeLanguage,
            useShimmer = false,
            adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: AD_PLACE_NAME_NATIVE_LANGUAGE")
                }
            }
        )
  ```

- Interstitial thêm `adGsExtendListener = `

  ```css
        AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO, adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "TestAdsActivity_onAdClicked: AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO")
                }
            })
  ```

**Version 1.4.2**

- Thêm tính năng tắt không sử dụng shimmer cho native và banner `useShimmer = false`
- Banner

  ```css
         AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false
        )
  ```
- Native bên ngoài
 
  ```css
         AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE_LANGUAGE,
            nativeGsAdView = bindingView.nativeLanguage,
            useShimmer = false
        )
  ```
- Native bên trong recycler view

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer, useShimmer = false)
            }
        )
  ```

- Không biết native hay banner

  ```css
        AdGsManager.instance.registerNativeOrBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameLanguage,
            bannerGsAdView = bindingView.bannerView,
            nativeGsAdView = bindingView.nativeLanguage,
            useShimmer = false,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->

            },
            callbackFailed = {

            }
        )
  ```

**Version 1.4.1**

- Thêm `requireScreenAdLoading` để cấu hình có dùng màn hình chờ tải quảng cáo không
- Khi không muốn có màn hình chờ tải quảng cáo thì dùng `requireScreenAdLoading = false` khi khởi tạo `AdGsManager.instance.registerCoroutineScope()`

**Version 1.4.0**
- Đổi cách sử dụng quảng cáo app open resume đơn giản hơn
  - `adPlaceNameAppOpenResume` là quảng cáo app open resume truyền vào
  - `canShowAppOpenResume` là điều kiện có thể hiển thị quảng cáo app open resume

  ```css
        RemoteConfig.instance.initRemoteConfig(
            application = this,
            remoteConfigDefaultsId = R.xml.remote_config_defaults,
            isDebug = BuildConfig.DEBUG
        )

        AdGsManager.instance.registerCoroutineScope(
            application = this,
            coroutineScope = mainScope,
            applicationId = BuildConfig.APPLICATION_ID,
            keyVipList = VipPreferences.defaultKeyVipList,
            adPlaceNameAppOpenResume = AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume,
            canShowAppOpenResume = { activity ->
                canShowAppOpenResume && activity !is SplashActivity
            },
            callbackChangeVip = { currentActivity, isVip ->
                if (currentActivity is BaseAdsActivity<*>) {
                    currentActivity.updateUiWithVip(isVip = isVip)
                }
            }
        )
  ```

- Từ giờ chuyển mặc định showLog = false
- Thêm tùy chọn app open resume

### Tùy chỉnh quảng cáo App open resume

- Thay đổi text

  ```css
        <string name="ad_text_welcome_back">Welcome back</string>
  ```

- Thay đổi animation LottieAppOpenResume

  ```css
         <style name="LottieAppOpenResume" parent="ads_LottieAppOpenResume">

         </style>
  ```

- Thay đổi TextAppOpenResume

  ```css
        <style name="TextAppOpenResume" parent="ads_TextAppOpenResume">

        </style>
  ```
- Thay đổi blurOverlayColor App open resume

  ```css
        <color name="ad_blurOverlayColor">#80000000</color>
  ```

**Version 1.3.30**

- Thêm config cho bg_text_ad cho các quảng cáo native mẫu `album`, `font`, `frame`, `language`, `share`, `sticker`, `template`, `vip` giờ có thể chỉnh `text ad` 3 thông số
  background color , radius và padding
- Cập nhật GsCore mới nhất

**Version 1.3.29**
- Sửa chức năng setData và setDataWithCalculateDiff của `BaseWithAdsAdapter` bị mất dữ liệu quảng cáo cũ khi cập nhật danh sách mới

**Version 1.3.28**
- Thêm setDataWithCalculateDiff để cập nhật dữ liệu có sử dụng DiffUtil

**Version 1.3.27**
- Sửa lỗi Fatal Exception: java.lang.IllegalArgumentException Unknown package: com.google.android.webview

**Version 1.3.26**
- Sửa lỗi Unable to add window -- token is not valid; is your activity running?

```css
                Fatal Exception: android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@1a9a45f is not valid; is your activity running?
       at android.view.ViewRootImpl.setView(ViewRootImpl.java:1588)
       at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:509)
       at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:133)
       at android.app.Dialog.show(Dialog.java:512)
       at com.core.gsadmob.utils.AdGsRewardedManager.checkShowRewardedAds$lambda$6(AdGsRewardedManager.kt:119)
       at com.core.gscore.utils.network.NetworkUtils$hasInternetAccessCheck$1$1.invokeSuspend(NetworkUtils.kt:143)
       at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
       at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:101)
       at android.os.Handler.handleCallback(Handler.java:938)
       at android.os.Handler.dispatchMessage(Handler.java:99)
       at android.os.Looper.loopOnce(Looper.java:226)
       at android.os.Looper.loop(Looper.java:313)
       at android.app.ActivityThread.main(ActivityThread.java:8669)
       at java.lang.reflect.Method.invoke(Method.java)
       at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:571)
       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1135)
        
```

**Version 1.3.25**
- Cải tiến BaseWithAdsAdapter
- Thêm onCreateAdViewHolder: để có thể tùy chỉnh item quảng cáo native
- Thêm onBindAdViewHolder: để có thể tùy chỉnh item quảng cáo native
- Thêm biến canCheckUpdateCallActionButton: để xác định CallActionButton có thay đôi trạng thái không
- Thêm getBackgroundResourceCallActionButton: để thay đổi trạng thái CallActionButton và hàm này chỉ có tác dụng khi canCheckUpdateCallActionButton = true

**Version 1.3.24**

- Cải tiến BaseWithAdsAdapter để tự xử lý quảng cáo native

- Tùy chỉnh [BaseWithAdsAdapter](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/adapter/BaseWithAdsAdapter.kt)

Hướng dẫn chi tiết xem [ImageAdapter](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/adapter/ImageAdapter.kt)

> override nativeAdLayoutId : khi muốn thay đổi native layout id

> override nativeAdId: khi muốn thay đổi id NativeGsAdView

> override onCreateItemViewHolder: để tự xử dữ liệu mới

> override onBindItemViewHolder: để tự xử dữ liệu mới

**Version 1.3.23**

- Tăng tốc di chuyển BottomSheetDialogFragment của ResumeDialogFragment
- Tạo style Base.Theme.GsAdmob

```css
      <style name="Theme.GsAdmob" parent="Base.Theme.GsAdmob"/>
```

- Đổi HashMap thành ConcurrentHashMap ở AdGsManager

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