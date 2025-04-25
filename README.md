# GsAdmob 📚

Thư viện quản lý và tùy chỉnh quảng cáo trong ứng dụng Android một cách linh hoạt, hỗ trợ đa dạng
loại quảng cáo và tích hợp GDPR/CMP.

## 🌟 Tính năng nổi bật

- **Quản lý trạng thái VIP** với `VipPreferences`
- **Tùy chỉnh Native Ads** dễ dàng qua XML/Code
- **Hỗ trợ GDPR/CMP** và Remote config (Firebase)
- **Quảng cáo đa dạng**: Banner, Native, Interstitial, Rewarded, Rewarded Interstitial, App Open
- **Tích hợp Adapter** cho RecyclerView với `BaseWithAdsAdapter`
- **Xử lý lifecycle** tự động cho quảng cáo
- **Hiệu ứng Shimmer** khi tải quảng cáo

---

## 📥 Cài đặt

### Gradle

### 1. Thêm repository vào `settings.gradle`:

```css
      dependencyResolutionManagement {
          repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
          repositories {
              google()
              mavenCentral()
              maven { url "https://jitpack.io" }
          }
      }
```

### 2. Thêm dependency vào `build.gradle`:

```css
      dependencies {
          implementation 'com.github.vtabk2:GsAdmob:1.3.23'
      }
```

## 🛠 Cấu hình cơ bản

### 1. Khởi tạo trong Application

- Tạo 1 application ví dụ
  [TestApplication](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/TestApplication.kt)

- Ở trong registerAdGsManager() sẽ khởi
  tạo [AdGsManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt)

- keyVipList là danh sách các key vip được dùng trong ứng dụng của bạn, xem chi tiết ở [VipPreferences](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/preferences/VipPreferences.kt)

  - Mặc định 
  
    ```css
          keyVipList = VipPreferences.defaultKeyVipList
    ```
  - Tùy chỉnh
  
    ```css
          keyVipList = mutableListOf("isPro", "isProByYear", "isProByMonth")
    ```
- TestApplication

  ```css
        class TestApplication : GsAdmobApplication() {
            private val mainScope = MainScope()
            
            override fun registerAdGsManager() {
                super.registerAdGsManager()
                
                AdGsManager.instance.registerCoroutineScope(
                    application = this,
                    coroutineScope = mainScope,
                    applicationId = BuildConfig.APPLICATION_ID,
                    keyVipList = VipPreferences.defaultKeyVipList,
                    callbackStartLifecycle = { activity ->
                    },
                    callbackPauseLifecycle = { activity ->
                    },
                    callbackNothingLifecycle = {
                    },
                    callbackChangeVip = { currentActivity, isVip ->
                    }, showLog = BuildConfig.DEBUG 
                )
            }
        }
  ```

### 2. Cấu hình quảng cáo

- Tạo file `config_admob.xml` trong `res/values`:

  ```css
        <resources>
            <!-- App ID -->
            <string name="app_id" translatable="false">ca-app-pub-3940256099942544~3347511713</string>
  
            <!-- Ad Unit IDs -->
            <string name="app_open_id" translatable="false">ca-app-pub-3940256099942544/9257395921</string>
            <string name="app_open_id_resume" translatable="false">ca-app-pub-3940256099942544/9257395921</string>
            <string name="banner_id" translatable="false">ca-app-pub-3940256099942544/9214589741</string>
            <string name="banner_id_home" translatable="false">ca-app-pub-3940256099942544/9214589741</string>
            <string name="banner_id_collapsible" translatable="false">ca-app-pub-3940256099942544/2014213617</string>
            <string name="interstitial_id" translatable="false">ca-app-pub-3940256099942544/1033173712</string>
            <string name="interstitial_id_without_video" translatable="false">ca-app-pub-3940256099942544/1033173712</string>
            <string name="native_id" translatable="false">ca-app-pub-3940256099942544/2247696110</string>
            <string name="native_id_language" translatable="false">ca-app-pub-3940256099942544/2247696110</string>
            <string name="rewarded_id" translatable="false">ca-app-pub-3940256099942544/5224354917</string>
            <string name="rewarded_interstitial_id" translatable="false">ca-app-pub-3940256099942544/5354046379</string>
        </resources>
  ```

## 🎮 Sử dụng

### Quảng cáo App Open

### 1. Quảng cáo app open ở màn hình splash

Cách dùng chi tiết xem ở [SplashActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/splash/SplashActivity.kt)

- Khởi tạo

  ```css
        AdGsSplashManager(
            this@SplashActivity,
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
            }, isDebug = BuildConfig.DEBUG
        )
  ```

### 2. Quảng cáo app open resume khi trở lại ứng dụng

- Bước 1: Cấu hình ở Application

- Bước 2: Cấu hình ở ResumeDialogFragment

### Quảng cáo Banner

- Khai báo trong xml:

  ```css
        <com.core.gsadmob.banner.BannerGsAdView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            app:adsShowType="alwaysShow"/>
  ```

- Tải quảng cáo với adPlaceName mặc định ở [AdPlaceNameDefaultConfig](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdPlaceNameDefaultConfig.kt)

  ```css
        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.HOME_BANNER,
            bannerGsAdView = binding.bannerView
        )
  ```

- Khi đã cấu hình Remote Config ở [AdGsRemoteExtraConfig](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/utils/remoteconfig/AdGsRemoteExtraConfig.kt)

  ```css
         AdGsManager.instance.registerBanner(
             lifecycleOwner = this,
             adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
             bannerGsAdView = bindingView.bannerView
         )      
  ```

### Quảng cáo Interstitial

### Quảng cáo Native

- Quảng cáo Native ở bên ngoài

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE,
            nativeGsAdView = bindingView.nativeFrame
        )
  ```

- Quảng cáo Native ở trong RecyclerView

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
            }
        )
  ```

- Tự do chuyển đổi giữ quảng cáo Native và Banner

  ```css
        AdGsManager.instance.registerNativeOrBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameLanguage,
            bannerGsAdView = bindingView.bannerView,
            nativeGsAdView = bindingView.nativeLanguage
        )
  ```

### Quảng cáo Rewarded và quảng cáo Rewarded Interstitial

- Mặc định AdPlaceName:

> AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED là adPlaceName của quảng cáo Rewarded

> AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL là adPlaceName của quảng cáo Rewarded Interstitial

- Trường hợp có một quảng cáo trả thưởng:

  - Khởi tạo luôn adPlaceName

  ```css
        val adGsRewardedManager = AdGsRewardedManager(
            activity = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL,
            isDebug = BuildConfig.DEBUG
        )
  ```

  - Sử dụng:

  ```css
        adGsRewardedManager?.showAds(               
            callback = { typeShowAds ->
                    
            })        
  ```


- Trường hợp có nhiều quảng cáo trả thưởng:

  - Khởi tạo:

  ```css
        val adGsRewardedManager = AdGsRewardedManager(
            activity = this,
            isDebug = BuildConfig.DEBUG
        )
  ```

  - Sử dụng:

  ```css
        adGsRewardedManager?.showAds(
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL,
            callback = { typeShowAds ->
                    
            })                
  ```

## 🔧 Tuỳ chỉnh nâng cao

### Tùy chỉnh quảng cáo Banner

- Đổi màu nền banner với `adsBannerGsBackgroundColor`

  ```css
        app:adsBannerGsBackgroundColor="@android:color/holo_green_dark"
  ```

- Thay đổi kiểu hiển thị với `adsShowType`

  ```css
        app:adsShowType="alwaysShow"
  ```

| adsShowType   | Trạng thái                                                                                                                                                                                  |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| showIfSuccess | Quảng cáo chỉ chiếm kích thước và hiển thị khi quảng cáo được tải thành công                                                                                                                |
| alwaysShow    | Quảng cáo luôn chiếm kích thước và hiển thị nếu quảng cáo đươc tải thành công                                                                                                               |
| hide          | Ẩn quảng cáo đi nhưng vẫn chiếm kích thước và không hiển thị ngay cả khi quảng cáo được tải thành công (được dùng khi đang show quảng cáo app open hiển thị thì tạm ẩn banner đi chẳng hạn) |
| notShow       | Ẩn quảng cáo đi không chiếm kích thước và không hiển thị ngày cả khi quảng cáo được tải thành công                                                                                          |

- Ví dụ

  ```css
        <com.core.gsadmob.banner.BannerGsAdView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            app:adsBannerGsBackgroundColor="@android:color/holo_green_dark"
            app:adsShowType="alwaysShow"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"/>
  ```

### Tùy chỉnh quảng cáo Native

### 1. Khai báo trong xml

- Dùng các adsNativeMode mặc định (album, font, frame...)

  ```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeAdView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:adsNativeMode="album"/>
  ```

- Dùng mode custom và sử dụng ID gốc, chỉ thay đổi layout

  ```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeLanguage"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:adsLayoutId="@layout/ad_native_custom"
            app:adsLayoutShimmerId="@layout/ad_native_custom_shimmer"
            app:adsNativeMode="custom"/>
  ```

- Dùng mode custom và thay đổi hết id thì tốt nhất là tạo style

  ```css
        <style name="NativeTest" parent="ads_BaseNativeCustom">
            <item name="adsLayoutId">@layout/ad_native_test</item>
            <item name="adsLayoutShimmerId">@layout/ad_native_test_shimmer</item>
            <item name="adsHeadlineId">@id/ad_headline_test</item>
            <item name="adsBodyId">@id/ad_body_test</item>
            <item name="adsStarsId">@id/ad_stars_test</item>
            <item name="adsAppIconId">@id/ad_app_icon_test</item>
            <item name="adsCallToActionId">@id/ad_call_to_action_test</item>
            <item name="adsViewId">@id/ad_view_test</item>
            <item name="adsShimmerId">@id/ad_shimmer_test</item>
            <item name="adsNativeViewRoot">@style/ads_NativeAlbumRoot</item>
        </style>
  ```

  ```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeLanguage"
            style="@style/NativeTest"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
  ```

### 2. Cấu hình trong kotlin

- Dùng các adsNativeMode mặc định (album, font, frame...)

  ```css
        applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
  ```

- Dùng mode custom và sử dụng ID gốc, chỉ thay đổi layout

  ```css
        val builder = BaseNativeAdView.Builder().apply {
            adsLayoutId = R.layout.ad_native_test
            adsLayoutShimmerId = R.layout.ad_native_test_shimmer
            adsNativeMode = AdsNativeMode.CUSTOM
        }
        binding.nativeAdView.applyBuilder(builder)
  ```
  
- Tùy chỉnh toàn bộ id

  ```css
        val builder = BaseNativeAdView.Builder().apply {
            adsLayoutId = R.layout.ad_native_test
            adsLayoutShimmerId = R.layout.ad_native_test_shimmer
            adsHeadlineId = R.id.ad_headline_test
            adsBodyId = R.id.ad_body_test
            adsStarsId = R.id.ad_stars_test
            adsAppIconId = R.id.ad_app_icon_test
            adsCallToActionId = R.id.ad_call_to_action_test
            adsViewId = R.id.ad_view_test
            adsShimmerId = R.id.ad_shimmer_test
            adsNativeViewRoot = R.style.ads_NativeTestRoot
            adsNativeMode = AdsNativeMode.CUSTOM
        }
        binding.nativeAdView.applyBuilder(builder)
  ```
  
- Dùng style có sẵn

  ```css
        binding.nativeAdView.setStyle(R.style.NativeFont)
  ```
  
- Dùng style tự tạo ví dụ như `NativeTest` đã tạo ở trên

  ```css
        binding.nativeAdView.setStyle(R.style.NativeTest)
  
        bindingView.nativeTest1.setStyle(com.core.gsadmob.R.style.NativeFont)
  ```
- Cấu hình ngôi sao rating ở ads_RatingBar

  ```css
        <style name="ads_RatingBar" parent="Theme.AppCompat">
            <item name="colorControlNormal">#FFBF1C</item>
            <item name="colorControlActivated">#FFBF1C</item>
        </style>
  ```

### Tùy chỉnh VipPreferences

- Lưu 1 key mới

  ```css
        fun save(key: String, value: Boolean) {}
  ```

- Lấy giá trị từ 1 key mới

  ```css
        fun load(key: String, valueDefault: Boolean = false) {}
  ```

- Có thể dùng các biến mặc định như isPro, isProByYear, isProByMonth

### Cấu hình Remote Config

### Theme

- Cấu hình để BottomSheet không bị giật khi di chuyển từ dưới lên

Thêm bottomSheetDialogTheme vào style gốc của ứng dụng

  ```css
        <item name="bottomSheetDialogTheme">@style/BaseBottomSheetDialogTheme</item>
  ```

hoặc mở rộng style gốc với `Base.Theme.GsAdmob`

  ```css
        <style name="Theme.GsAdmob" parent="Base.Theme.GsAdmob"/>
  ```

## 📜 Lịch sử phiên bản

**Version 1.3.23**
- Tăng tốc di chuyển BottomSheetDialogFragment của ResumeDialogFragment
- Tạo style Base.Theme.GsAdmob

```css
      <style name="Theme.GsAdmob" parent="Base.Theme.GsAdmob"/>
```

<details> <summary>👉 Click để xem thêm lịch sử cập nhật</summary>

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
- Thêm [registerNativeOrBanner()](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt) để tải quảng cáo là có kiểu thay đổi giữa banner và native(thường là ở màn chọn ngôn ngữ)
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
- Thêm [AdGsDelayManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsDelayManager.kt) để quản lý tải quảng cáo cần thời gian chờ(thường là quảng cáo app open resume)
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

</details>

**Nếu thư viện này giúp ích cho bạn theo bất kỳ cách nào, hãy thể hiện tình yêu của bạn ❤️ bằng cách đặt ⭐ vào dự án này ✌️️**

## 📄 Giấy phép

## 🤝 Đóng góp

Mọi đóng góp vui lòng tạo `Pull requests` hoặc `Issues` trên [GitHub](https://github.com/vtabk2/GsAdmob).


