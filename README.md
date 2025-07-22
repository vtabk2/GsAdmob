# GsAdmob 📚

Thư viện quản lý và tùy chỉnh quảng cáo trong ứng dụng Android một cách linh hoạt, hỗ trợ đa dạng
loại quảng cáo và tích hợp GDPR/CMP.

## 🌟🌟🌟 Tính năng nổi bật

- **Quản lý trạng thái VIP** với `VipPreferences`
- **Tùy chỉnh Native Ads** dễ dàng qua XML/Code
- **Hỗ trợ GDPR/CMP** và Remote config (Firebase)
- **Quảng cáo đa dạng**: `Banner`, `Native`, `Interstitial`, `Rewarded`, `Rewarded Interstitial`, `App Open`
- **Tích hợp Adapter** cho RecyclerView với `BaseWithAdsAdapter`
- **Xử lý lifecycle** tự động cho quảng cáo `Banner` và `Native`
- **Hiệu ứng Shimmer** khi tải quảng cáo `Banner` và `Native`

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
          implementation 'com.github.vtabk2:GsAdmob:1.6.12'
      }
```

## 🛠 Cấu hình cơ bản

### 1. Khởi tạo trong Application

- Tạo 1 application mở rộng GsAdmobApplication ví dụ
  [TestApplication](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/TestApplication.kt)

- Ở trong `registerAdGsManager()` của `TestApplication` sẽ khởi tạo [AdGsManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt)

- `keyVipList` là danh sách các key vip được dùng trong ứng dụng của bạn, xem chi tiết ở [VipPreferences](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/preferences/VipPreferences.kt)

  - Mặc định 
  
    ```css
          keyVipList = VipPreferences.defaultKeyVipList
    ```

  - Tùy chỉnh các key mình dùng ví dụ sử dụng 2 key `isPro`, `isProByYear`
  
    ```css
          keyVipList = mutableListOf("isPro", "isProByYear")
    ```
  - Nếu không muốn sử dụng thì khởi tạo
    
    ```css
          keyVipList = mutableListOf()
    ```

- `TestApplication`

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
                    adPlaceNameAppOpenResume = AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume,
                    canShowAppOpenResume = { activity ->
                        canShowAppOpenResume && activity !is SplashActivity
                    },
                    callbackNothingLifecycle = {
                        // 1 số logic cần thiết khác (ví dụ retry vip hoặc Lingver)
                    }
                )
            }
        }
  ```

### 2. Cấu hình quảng cáo

- Tạo file `config_admob.xml` trong `res/values`:

  ```css
        <resources>
            <!-- App id -->
            <string name="app_id" translatable="false">ca-app-pub-3940256099942544~3347511713</string>
  
            <!-- Ad Unit ids -->
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

- Đổi string của dialog GDPR/CMP nếu trong ứng dụng có sử dụng quảng cáo trả thưởng: `Rewarded` hoặc `Rewarded Interstitial`

  ```css
        <string name="ads_msg_gdpr">@string/msg_gdpr</string>
  
        <string name="ads_text_grant_permission">@string/text_grant_permission</string>
  ```

- Đổi string welcome_back của màn hình chờ của quảng cáo `App Open Resume`

  ```css
        <string name="ad_text_welcome_back" translatable="false">@string/text_welcome_back</string>
  ```

## 🎮 Sử dụng

### Quảng cáo App Open

### 1. Quảng cáo app open ở màn hình splash

- Sử dụng [AdGsSplashManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsSplashManager.kt) để tải và hiển thị quảng cáo cho màn hình splash

Hướng dẫn chi tiết cách dùng xem ở [SplashActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/splash/SplashActivity.kt)

- Khởi tạo `AdGsSplashManager`

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

- Mặc định `timeout = 3500` có thể thay đổi khi khởi tạo `AdGsSplashManager`

### 2. Quảng cáo app open resume khi trở lại ứng dụng

- Cấu hình ở [Application](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/TestApplication.kt)

  Các giá trị có thể tùy chỉnh :

  - `adPlaceNameAppOpenResume` là quảng cáo app open resume bạn muốn dùng (mặc định adPlaceNameAppOpenResume = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)

  - `canShowAppOpenResume` điều kiện để hiển thị quảng cáo app open resume (mặc định canShowAppOpenResume = { false })

  - `requireScreenAdLoading` để tùy chỉnh có hiển thị màn hình chờ tải quảng cáo app open resume không? (mặc định requireScreenAdLoading = true)

- Khi có sử dụng RemoteConfig thì khởi tạo `RemoteConfig.instance.initRemoteConfig()` trước

  ```css
        override fun registerAdGsManager() {
            super.registerAdGsManager()

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
                callbackNothingLifecycle = {
                    // 1 số logic cần thiết khác (ví dụ retry vip hoặc Lingver)
                }
           )
       }
  ```

- Khi không sử dụng RemoteConfig

  ```css
        override fun registerAdGsManager() {
            super.registerAdGsManager()

            AdGsManager.instance.registerCoroutineScope(
                application = this,
                coroutineScope = mainScope,
                applicationId = BuildConfig.APPLICATION_ID,
                keyVipList = VipPreferences.defaultKeyVipList,
                adPlaceNameAppOpenResume = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME,
                canShowAppOpenResume = { activity ->
                    canShowAppOpenResume && activity !is SplashActivity
                },
                callbackNothingLifecycle = {
                    // 1 số logic cần thiết khác (ví dụ retry vip hoặc Lingver)
                }
           )
       }
  ```

- Khi không muốn có màn hình chờ tải quảng cáo thì dùng `requireScreenAdLoading = false` khi khởi tạo `AdGsManager.instance.registerCoroutineScope()`

### Quảng cáo Banner

- Khai báo trong xml:

  ```css
        <com.core.gsadmob.banner.BannerGsAdView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
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

- Khi không muốn sử dụng shimmer `useShimmer = false`

  ```css
         AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false
        )
  ```

- Khi muốn dùng quảng cáo `banner collapsible` tự động mở rộng cần truyền thêm `activity = this`

  ```css
         AdGsManager.instance.registerBanner(
            activity = this,
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false
        )
  ```

### Quảng cáo Interstitial
- Hiển thị 2 quảng cáo xen kẽ mặc định

  ```css
        AdGsManager.instance.showInterstitial()

        AdGsManager.instance.showInterstitialWithoutVideo()
  ```
  
- Hiển thị quảng cáo xen kẽ

  ```css
        AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL)
        
        AdGsManager.instance.showAd(adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL_WITHOUT_VIDEO)
  ```

### Quảng cáo Native

- Quảng cáo Native ở bên ngoài (khi không muốn sử dụng shimmer thì dùng `useShimmer = false`)

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE,
            nativeGsAdView = bindingView.nativeFrame,
            useShimmer = true
        )
  ```

- Quảng cáo Native ở trong RecyclerView (khi không muốn sử dụng shimmer thì dùng `useShimmer = false`)

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer, useShimmer = true)
            }
        )
  ```

- Tự do chuyển đổi giữ quảng cáo Native và Banner (khi không muốn sử dụng shimmer thì dùng `useShimmer = false`)

  ```css
        AdGsManager.instance.registerNativeOrBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameLanguage,
            bannerGsAdView = bindingView.bannerView,
            nativeGsAdView = bindingView.nativeLanguage,
            useShimmer = true
        )
  ```

- Tạm ẩn quảng cáo native

  ```css
        bindingView.nativeFrame.hide()
  ```
- Hiển thị lại quảng cáo native đã ẩn

  ```css
        bindingView.nativeFrame.show()
  ```

### Quảng cáo Rewarded và quảng cáo Rewarded Interstitial

- Mặc định AdPlaceName:

  - AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED là adPlaceName của quảng cáo Rewarded

  - AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_REWARDED_INTERSTITIAL là adPlaceName của quảng cáo Rewarded Interstitial

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

- Thay đổi thời gian giữa 2 lần hiển thị

  ```css
        AdGsManager.instance.registerDelayShowTime(delayShowTime = 30, adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME)
  ```

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

- Bắt các sự kiện của quảng cáo `adGsExtendListener`, `adGsListener`

  ```css
        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView,
            useShimmer = false,
            adGsListener = object : AdGsListener {
                
            },
            adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: adPlaceNameBannerHome")
                }
            }
        )
  ```

### Tùy chỉnh quảng cáo Native
  
  | adsNativeMode | Trạng thái                                               |
  |---------------|----------------------------------------------------------|
  | none          | (Mặc định) Không hiển thị quảng cáo                      |
  | album         | Quảng cáo mẫu album                                      |
  | font          | Quảng cáo mẫu font                                       |
  | frame         | Quảng cáo mẫu frame                                      |
  | language      | Quảng cáo mẫu language                                   |
  | share         | Quảng cáo mẫu share                                      |
  | sticker       | Quảng cáo mẫu sticker                                    |
  | template      | Quảng cáo mẫu template                                   |
  | vip           | Quảng cáo mẫu vip                                        |
  | custom ⭐⭐⭐    | Khi muốn tự tùy chỉnh quảng cáo native với giao diện mới |

### 1. Khai báo trong xml

- Dùng các adsNativeMode mặc định (album, font, frame...)

  ```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeAdView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:adsNativeMode="album"/>
  ```

- Dùng mode custom và sử dụng id gốc, chỉ thay đổi layout

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

- Dùng các `adsNativeMode` mặc định (album, font, frame...)

  ```css
        applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
  ```

- Dùng mode custom và sử dụng id gốc, chỉ thay đổi layout

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

- Bắt các sự kiện của quảng cáo `adGsExtendListener`, `adGsListener`

  ```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer, useShimmer = true)
            },
            adGsListener = object : AdGsListener {
                
            },
            adGsExtendListener = object : AdGsExtendListener{
                override fun onAdClicked() {
                    Log.d("TAG5", "HomeActivity_onAdClicked: adPlaceNameNativeHome")
                }
            }
        )
  ```
  
### 3. Tùy chỉnh các mẫu native có sẵn xem ở [config_admob.xml](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/res/values/config_admob.xml)

#### 3.1. Ví dụ album có thể thay đổi các thông số

  ```css
        <!--    album-->
        <color name="ads_text_color_headline_album">@android:color/white</color>
        <color name="ads_text_color_body_album">@android:color/white</color>
        <color name="ads_text_color_attribution_album">@android:color/white</color>
        <color name="ads_text_color_call_button_album">@color/selector_color_white_black</color>
        <color name="ads_call_button_album">#14B261</color>
        <color name="ads_call_button_album_pressed">#DFE1E6</color>
        <color name="ads_bg_color_album">#212121</color>
        <color name="ads_bg_text_ad_album_color">#15C969</color>
    
        <drawable name="ads_bg_album">@drawable/ads__bg_album</drawable>
        <drawable name="ads_bg_text_ad_album">@drawable/ads__bg_text_ad_album</drawable>
        <drawable name="ads_bg_call_action_button_album">
            @drawable/ads_selector_bg_call_action_button_album
        </drawable>
    
        <bool name="ads_call_button_album_textAllCaps">false</bool>
    
        <dimen name="ads_text_size_headline_album">15sp</dimen>
        <dimen name="ads_text_size_body_album">13sp</dimen>
        <dimen name="ads_text_size_attribution_album">8sp</dimen>
        <dimen name="ads_text_size_call_to_action_album">14sp</dimen>
        <dimen name="ads_height_call_to_action_album">32dp</dimen>
        <dimen name="ads_radius_call_to_action_album">10dp</dimen>
        <dimen name="ads_padding_bottom_call_to_action_album">6dp</dimen>
        <dimen name="ads_padding_left_call_to_action_album">10dp</dimen>
        <dimen name="ads_padding_right_call_to_action_album">10dp</dimen>
        <dimen name="ads_padding_top_call_to_action_album">6dp</dimen>
        <dimen name="ads_radius_bg_album">0dp</dimen>
        
        <dimen name="ads_bg_text_ad_album_radius">5dp</dimen>
        <dimen name="ads_bg_text_ad_album_padding_bottom">2dp</dimen>
        <dimen name="ads_bg_text_ad_album_padding_left">@dimen/ads_bg_text_ad_album_radius</dimen>
        <dimen name="ads_bg_text_ad_album_padding_right">@dimen/ads_bg_text_ad_album_radius</dimen>
        <dimen name="ads_bg_text_ad_album_padding_top">2dp</dimen>
    
        <style name="ads_NativeAlbumRoot" parent="ads_BaseNativeAdViewRoot">
    
        </style>
  ```

- Thay đổi kích thước margin của quảng cáo

  ```css
        <style name="ads_NativeAlbumRoot" parent="ads_BaseNativeAdViewRoot">
            <item name="android:layout_marginStart">6dp</item>
            <item name="android:layout_marginEnd">6dp</item>
            <item name="android:layout_marginBottom">8dp</item>
        </style>
  ```

#### 3.2. Tùy chỉnh shimmer

  ```css
        <!--    shimmer root-->
        <color name="ads_bg_shimmer_root_color">#CACACA</color>
        <color name="ads_bg_banner_shimmer_root_color">#CACACA</color>
    
        <drawable name="ads_bg_shimmer_album">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_custom">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_font">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_frame">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_language">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_share">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_sticker">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_template">@drawable/ads__bg_shimmer_root</drawable>
        <drawable name="ads_bg_shimmer_vip">@drawable/ads__bg_shimmer_root</drawable>
    
        <drawable name="ads_bg_banner_shimmer_root">@drawable/ads__bg_banner_shimmer_root</drawable>
    
        <dimen name="ads_bg_shimmer_root_radius">0dp</dimen>
    
        <dimen name="ads_bg_banner_shimmer_root_radius">0dp</dimen>
    
        <!--    shimmer-->
        <color name="ads_bg_shimmer_color">#80000000</color>
    
        <drawable name="ads_bg_shimmer">@drawable/ads__bg_shimmer</drawable>
    
        <dimen name="ads_bg_shimmer_radius">5dp</dimen>
  ```

#### 3.3. Cấu hình ngôi sao rating ở ads_RatingBar

  ```css
        <style name="ads_RatingBar" parent="Theme.AppCompat">
            <item name="colorControlNormal">#FFBF1C</item>
            <item name="colorControlActivated">#FFBF1C</item>
        </style>
  ```

#### 3.4. Tùy chỉnh quảng cáo Native trong RecyclerView bằng cách dùng [BaseWithAdsAdapter](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/adapter/BaseWithAdsAdapter.kt)

Hướng dẫn chi tiết xem [ImageAdapter](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/adapter/ImageAdapter.kt)

- Khi muốn thay đổi giao diện quảng cáo

  ```css
         override val nativeAdLayoutId: Int = R.layout.item_ads_image
  ```

- Khi muốn thay đổi id của `NativeGsAdView`

  ```css
        override val nativeAdId: Int = R.id.nativeAd
  ```

- Khởi tạo giao diện các item khác

  ```css
        override onCreateItemViewHolder
  ```

- Cập nhật dữ liệu vào giao diện item

  ```css
        override onBindItemViewHolder
  ```

- Khởi tạo giao diên quảng cáo

  ```css
        override onCreateAdViewHolder
  ```

- Cập nhật dữ liệu vào giao diện quảng cáo

  ```css
        override onBindAdViewHolder
  ```

- Kích hoạt chức năng thay đổi trạng thái CallActionButton

  ```css
        override canCheckUpdateCallActionButton
  ```

- Lấy res cho CallActionButton và chỉ có tác dụng khi canCheckUpdateCallActionButton = true

  ```css
        override getBackgroundResourceCallActionButton
  ```

- Cập nhật dữ liệu mà không dùng DiffUtil

  ```css
        setData()
  ```

- Cập nhật dữ liệu có sử dụng DiffUtil

  ```css
        setDataWithCalculateDiff()
  ``` 

  - Kiểm tra xem 2 đối tượng có giống nhau không?

    ```css
          override fun areItemsTheSameDiff(oldItem: Any, newItem: Any)
    ```

  - Kiểm tra xem 2 đối tượng có dữ liệu giống nhau không và chỉ được gọi khi areItemsTheSameDiff =
    true

    ```css
          override fun areContentsTheSameDiff(oldItem: Any, newItem: Any)
    ```
### Tùy chỉnh quảng cáo `Interstitial` 

- Bắt các sự kiện của quảng cáo `AdGsManager.instance.registerAndShowAds`

    ```css
          AdGsManager.instance.registerAndShowAds
    ```

- Bắt các sự kiện mở rộng của quảng cáo `adGsExtendListener`

    ```css
          AdGsManager.instance.showAd(
                adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL,
                adGsExtendListener = object : AdGsExtendListener {
                override fun onAdClicked() {
                    Log.d("TAG5", "TestAdsActivity_onAdClicked: AD_PLACE_NAME_INTERSTITIAL")
                }
            })
    ```

- Thay đổi thời gian giữa 2 lần hiển thị

  ```css
        AdGsManager.instance.registerDelayShowTime(delayShowTime = 30, adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_INTERSTITIAL)
  ```

### Tùy chỉnh `VipPreferences` (nơi chứa các key vip của ứng dụng nếu cần)

- Lưu 1 key mới

  ```css
        fun save(key: String, value: Boolean) {}
  ```

- Lấy giá trị từ 1 key mới

  ```css
        fun load(key: String, valueDefault: Boolean = false) {}
  ```

- Có thể dùng các biến mặc định như isPro, isProByYear, isProByMonth

### Cấu hình `Remote Config`

- Tạo file `remote_config_defaults.xml` ở thư mục `res/xml`
- Tạo file [RemoteConfig](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/utils/remoteconfig/RemoteConfig.kt) sẽ mở rộng [AdGsRemoteConfig](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/remoteconfig/AdGsRemoteConfig.kt)

Bên trong `updateRemoteConfig` của `RemoteConfig` là nơi lấy các cấu hình từ RemoteConfig trên Firebase

- Khởi tạo trong `registerAdGsManager()` ở Application

  ```css
        RemoteConfig.instance.initRemoteConfig(
            application = this,
            remoteConfigDefaultsId = R.xml.remote_config_defaults,
            isDebug = BuildConfig.DEBUG
        )
  ```

- Tạo [AdGsRemoteExtraConfig](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/utils/remoteconfig/AdGsRemoteExtraConfig.kt) nơi
  này sẽ quản lý các quảng cáo có trong ứng dụng để RemoteConfig cập nhật thông tin từ RemoteConfig vào

### GsAdmobApplication là abstract class

- Sửa lỗi webview, truyền getPackageName() của ứng dụng vào

  ```css
        override fun fixWebView(packageName: String) {
            super.fixWebView(getPackageName())
        }
  ```

- Cài đặt có dùng thiết bị thành máy test không, super.setupDeviceTest(true) sẽ cho thiết bị thành máy test

  ```css
        override fun setupDeviceTest(isDebug: Boolean) {
            super.setupDeviceTest(BuildConfig.DEBUG)
        }
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

## [Lịch sử cập nhật](https://github.com/vtabk2/GsAdmob/blob/main/HISTORY.md)



