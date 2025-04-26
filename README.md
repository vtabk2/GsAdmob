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

  ```css
        fun onShowAds(from: String) {
            (activity as? AppCompatActivity)?.let {
                AdGsDelayManager(
                    activity = it,
                    fragment = this,
                    adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume,
                    callbackFinished = {
                        dismissAllowingStateLoss()
                    })
            }
        }
  ```

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

- Hiển thị quảng cáo xen kẽ

  ```css
        AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL)
        
        AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO)
  ```

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

  - Hủy hiển thị quảng cáo trả thưởng (chỉ có tác dụng khi quảng cáo trả thưởng chưa hiển thị)

  ```css
        AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED)
  
        AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL)
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

- Tạo file remote_config_defaults

- Khởi tạo trong registerAdGsManager() ở Application

  ```css
        RemoteConfig.instance.initRemoteConfig(
            application = this,
            remoteConfigDefaultsId = R.xml.remote_config_defaults,
            isDebug = BuildConfig.DEBUG
        )
  ```

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

## Nếu thư viện này giúp ích cho bạn theo bất kỳ cách nào, hãy thể hiện tình yêu của bạn ❤️ bằng cách đặt ⭐ vào dự án này ✌️️

## 📄 Giấy phép

  ```css
        MIT License

        Copyright (c) [2025] [Vũ Tuấn Anh]
        
        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:
        
        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.
        
        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
  ```

## 🤝 Đóng góp

Mọi đóng góp vui lòng tạo `Pull requests` hoặc `Issues` trên [GitHub](https://github.com/vtabk2/GsAdmob).

## [Lịch sử cập nhật]()



