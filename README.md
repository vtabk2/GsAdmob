# GsAdmob

**Gradle**
**Step 1.** Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
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
                    implementation 'com.github.vtabk2:GsAdmob:1.1.4'
            }
```

- admob : cấu hình trong config_admob

- BannerUtils
  Cách 1:
```css
        BannerUtils.initBannerAds(activity = this, flBannerAds = bindingView.flBannerAds, isVip = false, show = true, alwaysShow = true, callbackAdMob = { adView ->
            adBanner = adView
        })
```

  Cách 2:

```css
        <com.core.gsadmob.banner.BannerGsAdView
            android:id="@+id/bannerView"
            android:layout_width="match_parent"
            android:layout_height="60dp"/>
```

  Load banner

```css
        bindingView.bannerView.loadAds(isVip = false, alwaysShow = true)
       
       
        bindingView.bannerView.loadAds(isVip = false)
```

  Nếu muốn có thời gian delay giữa các lần load lại banner

```css
        bindingView.bannerView.registerDelayTime(10)
```

- InterstitialWithDelayUtils dùng khi cần load InterstitialAds có delay load giữa các lần
```css
        InterstitialWithDelayUtils.instance.registerDelayTime(10)
        
        InterstitialWithDelayUtils.instance.showInterstitialAd(activity = this, isVip = false, listener = object : InterstitialWithDelayUtils.AdCloseListener {
                override fun onAdClose() {
                    // todo
                }

                override fun onAdCloseIfFailed() {
                    // todo
                }
            })
```
- InterstitialUtils

```css
        InterstitialUtils.instance.showInterstitialAd(activity = this, isVip = false, listener = object : InterstitialUtils.AdCloseListener {
                override fun onAdClose() {
                    // todo
                }

                override fun onAdCloseIfFailed() {
                    // todo
                }
            })
```

- NativeUtils

- Tùy biến NativeAdView thì chọn ads_mode = custom

```css
        <com.core.gsadmob.natives.view.NativeGsAdView
            android:id="@+id/nativeCustom"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            app:ads_mode="custom"/>
```

- Cách 1: Giữ id gốc chỉ đổi id layout
```css
        val builder = BaseNativeAdView.Builder().apply {
            adLayoutId = R.layout.ad_native_test
            adLayoutShimmerId = R.layout.ad_native_test_shimmer
            //            adLayoutShimmerId = 0 // nếu không muốn dùng shimmer
        }
        bindingView.nativeCustom.applyBuilder(builder)
```

- Cách 2: Đổi tất cả id thì cấu hình lại trong builder:
```css
        val builder = BaseNativeAdView.Builder().apply {
            adLayoutId = R.layout.ad_native_test
            adLayoutShimmerId = R.layout.ad_native_test_shimmer
            adHeadlineId = R.id.ad_headline_test
            adStarsId = R.id.ad_stars_test
            adAppIconId = R.id.ad_app_icon_test
            adCallToActionId = R.id.ad_call_to_action_test
            adViewId = R.id.ad_view_test
            adShimmerId = R.id.ad_view_test_shimmer
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
            <item name="adLayoutId">@layout/ad_native_test</item>
            <item name="adLayoutShimmerId">@layout/ad_native_test_shimmer</item>
            <item name="adHeadlineId">@id/ad_headline_test</item>
            <!--        <item name="adBodyId">@id/ad_body_test</item>-->
            <item name="adStarsId">@id/ad_stars_test</item>
            <item name="adAppIconId">@id/ad_app_icon_test</item>
            <item name="adCallToActionId">@id/ad_call_to_action_test</item>
            <item name="adViewId">@id/ad_view_test</item>
            <!--        <item name="adMediaViewId">@id/ad_media_test</item>-->
            <item name="adShimmerId">@id/ad_view_test_shimmer</item>
        </style>
        
        bindingView.nativeTest1.applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
        
        bindingView.nativeTest2.applyBuilder(NativeDefaultConfig.BUILDER_FONT)
```
- Cách load native

```css
        NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
            bindingView.nativeCustom.startShimmer()
        }, callback = { nativeAd ->
            bindingView.nativeCustom.setNativeAd(nativeAd)
        })
```

- RewardedInterstitialUtils

- AppOpenAdManager
- AppResumeAdManager