# GsAdmob

Thư viện được tạo ra với mục đích quản lý và tùy chỉnh giao diện của các quảng cáo trong ứng dụng 1 cách dễ dàng :

- Có lưu trạng thái vip của ứng dụng ở VipPreferences
- Có xử lý việc thay đổi trạng thái vip
- Tùy chỉnh dễ dàng giao diện quảng cáo Native
- Có thêm trạng thái đang tải quảng cáo
- Có hỗ trợ kiểm tra CMP/GDPR
- Có BaseWithAdsAdapter để dùng adapter có chứa quảng cáo native  

# Cấu hình Gradle gồm 2 bước

**Step 1.** Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:
```css
        dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    mavenCentral()
                    maven { url 'https://jitpack.io' }
                }
            }
```

**Step 2.** Add the dependency
```css
        dependencies {
                    implementation 'com.github.vtabk2:GsAdmob:1.3.22'
            }
```

# Quan trọng

- [AdGsManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsManager.kt): Quản lý toàn bộ quảng cáo ở trong ứng dụng, chứa các hàm tải và hiển thị quảng
  cáo...
- [AdGsSplashManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsSplashManager.kt): Được tạo ra dùng cho màn hình splash
- [AdGsDelayManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsDelayManager.kt): Được tạo ra dùng cho các quảng cáo cần thời gian chờ, hiện tại thường dùng
  cho
  quảng cáo lúc mở lại ứng dụng (app open resume)
- [AdGsRewardedManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsRewardedManager.kt): Được tạo ra dùng cho các chức năng dùng quảng cáo trả thưởng

# Cấu hình quảng cáo

Thay đổi cấu hình quảng cáo trong [config_admob](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/res/values/config_admob.xml)

**Step 1.** Trong ứng dụng tạo 1 file config_admob.xml ở values

**Step 2.** Tùy chỉnh config_admob

- Ở trên cùng là app id và các id của quảng cáo dùng trong ứng dụng. Hiện tại có cấu hình 11 id quảng cáo cho 5 loại quảng cáo
- Tiếp theo là cấu hình các thuộc tính có thể thay đổi của các mẫu quảng cáo native cấu hình sẵn (album, font, frame, language, share, sticker, template, vip)
- Tiếp theo là cấu hình các thuộc tính của shimmer
- Cấu hình view root của các quảng cáo native (thường là khi là 1 item trong recyclerview)

Ví dụ : Thêm margin cho quảng cáo native album

```css
         <style name="ads_NativeAlbumRoot" parent="ads_BaseNativeAdViewRoot">
            <item name="android:layout_marginStart">6dp</item>
            <item name="android:layout_marginEnd">6dp</item>
            <item name="android:layout_marginBottom">8dp</item>
        </style>
```

- Tạo style mới cho quảng cáo native 

```css
            <!--    album-->
        <style name="NativeAlbum" parent="ads_BaseNativeCustom">
            <item name="adsLayoutId">@layout/ad_native_album</item>
            <item name="adsLayoutShimmerId">@layout/ad_native_album_shimmer</item>
            <item name="adsHeadlineId">@id/ad_headline_album</item>
            <item name="adsBodyId">@id/ad_body_album</item>
            <item name="adsStarsId">@id/ad_stars_album</item>
            <item name="adsAppIconId">@id/ad_app_icon_album</item>
            <item name="adsCallToActionId">@id/ad_call_to_action_album</item>
            <item name="adsViewId">@id/ad_view_album</item>
            <item name="adsShimmerId">@id/ad_shimmer_album</item>
            <item name="adsNativeViewRoot">@style/ads_NativeAlbumRoot</item>
            <item name="adsNativeMode">album</item>
        </style>
```

Khi dùng : có thể thay trong code
```css
        bindingView.nativeTest1.setStyle(R.style.NativeAlbum)
```

hoặc khởi tạo sẵn trong xml
```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeCustom"
            style="@style/NativeAlbum"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"/>

```


- Cấu hình ngôi sao rating ở ads_RatingBar
```css
        <style name="ads_RatingBar" parent="Theme.AppCompat">
            <item name="colorControlNormal">#FFBF1C</item>
            <item name="colorControlActivated">#FFBF1C</item>
        </style>
```

# [AdGsRemoteConfig](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/remoteconfig/AdGsRemoteConfig.kt)
- Dùng để khởi tạo remote config trên firebase
- Cách dùng xem [RemoteConfig](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/utils/remoteconfig/RemoteConfig.kt)
- Trong updateRemoteConfig sẽ cấu hình các dữ liệu lấy từ firebase xuống

# [BaseAdsActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/base/BaseAdsActivity.kt)

- Khởi tạo [AdGsRewardedManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsRewardedManager.kt) để dùng cho quảng cáo trả thưởng

# [VipPreferences](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/preferences/VipPreferences.kt) Nơi lưu trạng thái đã mua vip

- Đăng ký keyVipList khi khởi tạo AdGsManager

> Nếu dùng mặc định thì xài: keyVipList = VipPreferences.defaultKeyVipList

> Nếu muốn thay đổi thì khởi tạo: keyVipList = mutableListOf("isPro", "isProByYear")

```css
        AdGsManager.instance.registerCoroutineScope(
            application = this,
            coroutineScope = mainScope,
            applicationId = BuildConfig.APPLICATION_ID,
            keyVipList = VipPreferences.defaultKeyVipList,
            callbackStartLifecycle = { activity ->
                if (canShowAppOpenResume && activity !is SplashActivity) {
                    AdGsManager.instance.showAd(adPlaceName = adPlaceName, onlyCheckNotShow = true, callbackShow = { adShowStatus ->
                        when (adShowStatus) {
                            AdShowStatus.CAN_SHOW, AdShowStatus.REQUIRE_LOAD -> {
                                activity.supportFragmentManager.let { fragmentManager ->
                                    val bottomDialogFragment = fragmentManager.findFragmentByTag(tag) as? ResumeDialogFragment
                                    if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                                        // BottomDialogFragment đang hiển thị
                                        bottomDialogFragment.onShowAds("onResume")
                                    } else {
                                        // BottomDialogFragment không hiển thị
                                        val fragment = (activity.window.decorView.rootView as? ViewGroup)?.let { ResumeDialogFragment.newInstance(it) }
                                        fragment?.show(fragmentManager, tag)
                                    }
                                }
                            }

                            else -> {

                            }
                        }
                    })
                }
            },
            callbackPauseLifecycle = { activity ->
                val bottomDialogFragment = activity.supportFragmentManager.findFragmentByTag(tag) as? ResumeDialogFragment
                if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                    // BottomDialogFragment đang hiển thị
                    activity.runOnUiThread {
                        bottomDialogFragment.dismissAllowingStateLoss()
                    }
                } else {
                    // BottomDialogFragment không hiển thị
                }
            }, callbackNothingLifecycle = {
                // 1 số logic cần thiết khác (ví dụ retry vip hoặc Lingver)
            }, callbackChangeVip = { currentActivity, isVip ->
                if (currentActivity is BaseAdsActivity<*>) {
                    currentActivity.updateUiWithVip(isVip = isVip)
                }
            }
        )
```

- Lưu 1 key mới

```css
         fun save(key: String, value: Boolean) {}
```

- Lấy giá trị từ 1 key mới

```css
        fun load(key: String, valueDefault: Boolean = false){}
```

- Có thể dùng các key mặc định như isPro, isProByYear, isProByMonth

# Banner

- Đổi màu nền banner adsBannerGsBackgroundColor

```css
      app:adsBannerGsBackgroundColor="@android:color/holo_green_dark"
```

- Chú ý adsShowType có các kiểu hiển thị khác nhau: 

| adsShowType   | Trạng thái                                                                                                                                                                                  |
|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| showIfSuccess | Quảng cáo chỉ chiếm kích thước và hiển thị khi quảng cáo được tải thành công                                                                                                                |
| alwaysShow    | Quảng cáo luôn chiếm kích thước và hiển thị nếu quảng cáo đươc tải thành công                                                                                                               |
| hide          | Ẩn quảng cáo đi nhưng vẫn chiếm kích thước và không hiển thị ngay cả khi quảng cáo được tải thành công (được dùng khi đang show quảng cáo app open hiển thị thì tạm ẩn banner đi chẳng hạn) |
| notShow       | Ẩn quảng cáo đi không chiếm kích thước và không hiển thị ngày cả khi quảng cáo được tải thành công                                                                                          |

```css
        <com.core.gsadmob.banner.BannerGsAdView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            app:adsShowType="alwaysShow"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"/>
```

- Cách truyền dữ liệu

```css
        bannerGsAdView?.setBannerAdView()
```

# Native Ads

- Tùy biến NativeAdView thì chọn adsNativeMode = custom

```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeCustom"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            app:adsNativeMode="custom"/>
```

- Cách 1: Giữ id gốc chỉ đổi id layout

```css
        val builder = BaseNativeAdView.Builder().apply {
            adsLayoutId = R.layout.ad_native_test
            adsLayoutShimmerId = R.layout.ad_native_test_shimmer
        }
        bindingView.nativeCustom.applyBuilder(builder)
```

- Cách 2: Đổi tất cả id thì cấu hình lại trong builder:

```css
        val builder = BaseNativeAdView.Builder().apply {
            adsLayoutId = R.layout.ad_native_test
            adsLayoutShimmerId = R.layout.ad_native_test_shimmer
            adsHeadlineId = R.id.ad_headline_test
            adsStarsId = R.id.ad_stars_test
            adsAppIconId = R.id.ad_app_icon_test
            adsCallToActionId = R.id.ad_call_to_action_test
            adsViewId = R.id.ad_view_test
            adsShimmerId = R.id.ad_view_test_shimmer
        }
        bindingView.nativeCustom.applyBuilder(builder)
```

- Cách 3:

```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeTest8"
            style="@style/NativeVip"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"/>

        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeCustom"
            style="@style/NativeTest"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"/>

        <style name="NativeTest" parent="BaseNativeCustom">
            <item name="adsLayoutId">@layout/ad_native_test</item>
            <item name="adsLayoutShimmerId">@layout/ad_native_test_shimmer</item>
            <item name="adsHeadlineId">@id/ad_headline_test</item>
            <item name="adsStarsId">@id/ad_stars_test</item>
            <item name="adsAppIconId">@id/ad_app_icon_test</item>
            <item name="adsCallToActionId">@id/ad_call_to_action_test</item>
            <item name="adsViewId">@id/ad_view_test</item>
            <item name="adsShimmerId">@id/ad_view_test_shimmer</item>
        </style>
```

- Nếu muốn dùng các id mặc định thì ko cần đổi(xem id mặc định ở ads_BaseNativeCustom)

```css  
        <style name="NativeTest" parent="BaseNativeCustom">
            <item name="adsLayoutId">@layout/ad_native_test</item>
            <item name="adsLayoutShimmerId">@layout/ad_native_test_shimmer</item>
            <item name="adsNativeViewRoot">@style/ads_NativeTestRoot</item>
        </style>
        
        bindingView.nativeTest1.applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
        
        bindingView.nativeTest2.applyBuilder(NativeDefaultConfig.BUILDER_FONT)
```

- Cách 4: Có thể tạo style rồi set trực tiếp bằng cách dưới đây

```css
        bindingView.nativeTest1.setStyle(com.core.gsadmob.R.style.NativeVip)
```

hoặc

```css        
        bindingView.nativeTest1.setStyle(R.style.NativeTest)
```

# Hướng dẫn GDPR xem ở SplashActivity

# Cách load quảng cáo

Tạo cách AdPlaceName trước giống cấu trúc ở AdPlaceNameConfig

**Show quảng cáo xen kẽ**

```css
        bindingView.tvInterstitial.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL)
        }
```

**Quảng cáo banner**

```css
        AdGsManager.instance.registerBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameBannerHome,
            bannerGsAdView = bindingView.bannerView
        )
```

**Quảng cáo native**

- Khi không rõ là native hay banner thì dùng hàm này

```css
        AdGsManager.instance.registerNativeOrBanner(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameLanguage,
            bannerGsAdView = bindingView.bannerView,
            nativeGsAdView = bindingView.nativeLanguage,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->

            },
            callbackFailed = {

            }
        )
```
- Khi native ở trong recycler view

```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameNativeHome,
            callbackSuccess = { nativeAdGsData, isStartShimmer ->
                adapter?.setupItemAds(nativeAd = nativeAdGsData?.nativeAd, isStartShimmer = isStartShimmer)
            }
        )
```

- Khi native ở ngoài

```css
        AdGsManager.instance.registerNative(
            lifecycleOwner = this,
            adPlaceName = AdPlaceNameDefaultConfig.instance.AD_PLACE_NAME_NATIVE,
            nativeGsAdView = bindingView.nativeFrame
        )
```

**Quảng cáo trả thưởng**

Từ version 1.3.12 đã cải tiến để khởi tạo và quản lý bằng [AdGsRewardedManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsRewardedManager.kt)

Nếu có nhiều quảng cáo trả thưởng thì khởi tạo như sau:

```css
        adGsRewardedManager = AdGsRewardedManager(
                activity = this,
                isDebug = BuildConfig.DEBUG
            )
```

và khi sử dụng thì truyền adPlaceName vào khi gọi:

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

**Quảng cáo app open**
Gồm 2 loại :

- Quảng cáo 1 lần khi mở ứng dụng

Hướng dẫn chi tiết ở [SplashActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/splash/SplashActivity.kt)

Sử dụng [AdGsSplashManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsSplashManager.kt)


- Quảng cáo khi trở lại ứng dụng

Hướng dẫn chi tiết ở [TestApplication](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/TestApplication.kt)

Sử dụng [AdGsDelayManager](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/AdGsDelayManager.kt) ở trong ResumeDialogFragment quản lý việc tải quảng cáo có thời gian chờ

**GsAdmobApplication**

- Cấu trúc mở rộng application

> Sửa lỗi webview (fixWebView)

> Cấu hình thiết bị test (setupDeviceTest)

> Cấu hình đồng ý analytics (setupConsentMode)

> Đăng ký lắng nghe quảng cáo (registerAdGsManager)

> Khởi tạo các cấu hình khác (initOtherConfig)

> Khởi tạo quảng cáo (initMobileAds)

# BaseWithAdsAdapter Adapter chứa quảng cáo native

# Lịch sử cập nhật
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


