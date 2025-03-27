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
                    implementation 'com.github.vtabk2:GsAdmob:1.2.19'
            }
```

# Cấu hình quảng cáo

Thay đổi cấu hình quảng cáo trong [config_admob](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/res/values/config_admob.xml)

**Step 1.** Trong ứng dụng tạo 1 file config_admob.xml ở values

**Step 2.** Tùy chỉnh config_admob

- Ở trên cùng là app id và các id của quảng cáo dùng trong ứng dụng. Hiện tại có cấu hình 11 id quảng cáo cho 5 loại quảng cáo
- Tiếp theo là cấu hình các thuộc tính có thể thay đổi của các mẫu quảng cáo native cấu hình sẵn (album, font, frame, language, share, sticker, template, vip)
- Tiêp theo là cấu hình các thuộc tính của shimmer
- Cấu hình view root của các quảng cáo native(thường là khi là 1 item trong recyclerview)

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


# [BaseAdsActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/base/BaseAdsActivity.kt)

Các hàm cơ bản được dùng trong đây

- setupNative override để xử lý cho từng activity
- startNativeShimmer override để bắt đầu chạy shimmer load cho từng activity
- registerAds nếu không muốn load tất cả quảng cáo thì có thể override lại

# [VipPreferences](https://github.com/vtabk2/GsAdmob/blob/main/GsAdmob/src/main/java/com/core/gsadmob/utils/preferences/VipPreferences.kt) Nơi lưu trạng thái đã mua vip

- Khởi tạo ở Application

```css
        VipPreferences.instance.initVipPreferences(this, BuildConfig.APPLICATION_ID)
```

- Đăng ký thay đổi trạng thái mua vip ở Application (keyVipList là danh sách key vip của ứng dụng)

```css
        private val mainScope = MainScope()
        
        mainScope.launch {
            VipPreferences.instance.getVipChangeFlow(keyVipList)
                .stateIn(mainScope, SharingStarted.Eagerly, VipPreferences.instance.isFullVersion())
                .collect { isVip ->
                    AdGsManager.instance.notifyVip(isVip)
                }
        }
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

- Từ ***Version 1.2.15*** đã cải tiến tích hợp vào registerCoroutineScope của AdGsManager
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

# Banner

Chú ý adsShowType có các kiểu hiển thị khác nhau: 

| adsShowType   | Trạng thái |
|---------------| ---------- |
| showIfSuccess | Quảng cáo chỉ chiếm kích thước và hiển thị khi quảng cáo được tải thành công |
| alwaysShow    | Quảng cáo luôn chiếm kích thước và hiển thị nếu quảng cáo đươc tải thành công |
| hide          | Ẩn quảng cáo đi nhưng vẫn chiếm kích thước và không hiển thị ngay cả khi quảng cáo được tải thành công (được dùng khi đang show quảng cáo app open hiển thị thì tạm ẩn banner đi chẳng hạn) |
| hide          | Ẩn quảng cáo đi không chiếm kích thước và không hiển thị ngày cả khi quảng cáo được tải thành công |

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

- Cách chạy shimmer
```css
        bannerGsAdView?.startShimmer()
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
            //            adsLayoutShimmerId = 0 // nếu không muốn dùng shimmer
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
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }
```

**Quảng cáo banner**

```css

        lifecycleScope.launch {
            async {
                AdGsManager.instance.adGsDataMapMutableStateFlow.collect {
                    it.forEach { adGsDataMap ->
                        when (adGsDataMap.key) {
                            AdPlaceNameConfig.AD_PLACE_NAME_BANNER_HOME -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.bannerView.startShimmer()
                                } else {
                                    bindingView.bannerView.setBannerAdView((adGsDataMap.value as? BannerAdGsData)?.bannerAdView)
                                }
                            }
                        }
                    }
                }
            }
        }

         AdGsManager.instance.startShimmerLiveData.observe(this) { shimmerMap ->
            shimmerMap.forEach {
                if (it.value) {
                    when (it.key) {
                        AdPlaceNameConfig.AD_PLACE_NAME_BANNER_HOME -> {
                            bindingView.bannerView.startShimmer()
                        }
                    }
                }
            }
        }
        
         AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_BANNER_HOME)
```

**Quảng cáo native**

```css
        lifecycleScope.launch {          
            async {
                AdGsManager.instance.adGsDataMapMutableStateFlow.collect {
                    it.forEach { adGsDataMap ->
                        when (adGsDataMap.key) {                         
                            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.nativeFrame.startShimmer()
                                } else {
                                    bindingView.nativeFrame.setNativeAd((adGsDataMap.value as? NativeAdGsData)?.nativeAd)
                                }
                            }

                            AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.nativeLanguage.startShimmer()
                                } else {
                                    bindingView.nativeLanguage.setNativeAd((adGsDataMap.value as? NativeAdGsData)?.nativeAd)
                                }
                            }
                        }
                    }
                }
            }
        }

        AdGsManager.instance.startShimmerLiveData.observe(this) { shimmerMap ->
            shimmerMap.forEach {
                if (it.value) {
                    when (it.key) {                     
                        AdPlaceNameConfig.AD_PLACE_NAME_NATIVE -> {
                            bindingView.nativeFrame.startShimmer()
                        }

                        AdPlaceNameConfig.AD_PLACE_NAME_NATIVE_LANGUAGE -> {
                            bindingView.nativeLanguage.startShimmer()
                        }
                    }
                }
            }
        }

        AdGsManager.instance.registerAds(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_BANNER)
```

**Quảng cáo trả thưởng**

```css  

        bindingView.tvRewardedInterstitial.setOnClickListener {
            AdGsManager.instance.cancelRewardAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL, isCancel = false)
            checkShowRewardedAds(callback = { typeShowAds ->
                when (typeShowAds) {
                    TypeShowAds.SUCCESS -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial SUCCESS", Toasty.SUCCESS)
                    }

                    TypeShowAds.FAILED -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial FAILED", Toasty.ERROR)
                    }

                    TypeShowAds.TIMEOUT -> {
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial TIMEOUT", Toasty.WARNING)
                    }

                    TypeShowAds.CANCEL -> {
                        // xử lý khi đóng ads thì làm gì ko quan trọng đã thành công hay không
                        Toasty.showToast(this@TestAdsActivity, "Rewarded Interstitial CANCEL", Toasty.WARNING)
                    }
                }
            })
        }
        
        private fun checkShowRewardedAds(callback: (typeShowAds: TypeShowAds) -> Unit, isRewardedInterstitialAds: Boolean = true, requireCheck: Boolean = true) {
            NetworkUtils.hasInternetAccessCheck(doTask = {
                if (googleMobileAdsConsentManager == null) {
                    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
                }
                when (googleMobileAdsConsentManager?.privacyOptionsRequirementStatus) {
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED -> {
                        if (cmpUtils.requiredShowCMPDialog()) {
                            if (cmpUtils.isCheckGDPR) {
                                googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                    loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                                }, onDisableAds = {
                                    callback(TypeShowAds.CANCEL)
                                }, isDebug = BuildConfig.DEBUG, timeout = 0)
                            } else {
                                gdprPermissionsDialog?.dismiss()
                                gdprPermissionsDialog = DialogUtils.initGdprPermissionDialog(this, callback = { granted ->
                                    if (granted) {
                                        googleMobileAdsConsentManager?.gatherConsent(this, onCanShowAds = {
                                            loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                                        }, onDisableAds = {
                                            callback(TypeShowAds.CANCEL)
                                        }, isDebug = BuildConfig.DEBUG, timeout = 0)
                                    } else {
                                        callback(TypeShowAds.CANCEL)
                                    }
                                })
                                gdprPermissionsDialog?.show()
                                dialogLayout(gdprPermissionsDialog)
                            }
                        } else {
                            loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                        }
                    }
    
                    ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED -> {
                        loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                    }
    
                    else -> {
                        // mục đích chỉ check 1 lần không được thì thôi
                        if (requireCheck) {
                            googleMobileAdsConsentManager?.requestPrivacyOptionsRequirementStatus(this, isDebug = BuildConfig.DEBUG, callback = { _ ->
                                checkShowRewardedAds(callback, isRewardedInterstitialAds, requireCheck = false)
                            })
                        } else {
                            loadAndShowRewardedAds(isRewardedInterstitialAds = isRewardedInterstitialAds, callback = callback)
                        }
                    }
                }
            }, doException = { networkError ->
                callback(TypeShowAds.TIMEOUT)
                when (networkError) {
                    NetworkUtils.NetworkError.SSL_HANDSHAKE -> {
                        Toasty.showToast(this, R.string.text_please_check_time, Toasty.WARNING)
                    }
    
                    else -> {
                        Toasty.showToast(this, R.string.check_network_connection, Toasty.WARNING)
                    }
                }
            }, context = this)
        }

        private fun loadAndShowRewardedAds(isRewardedInterstitialAds: Boolean, callback: (typeShowAds: TypeShowAds) -> Unit) {
            val check = AtomicBoolean(true)
            val adPlaceName = if (isRewardedInterstitialAds) AdPlaceNameConfig.AD_PLACE_NAME_REWARDED_INTERSTITIAL else AdPlaceNameConfig.AD_PLACE_NAME_REWARDED
    
            AdGsManager.instance.registerAdsListener(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
                override fun onAdClose(isFailed: Boolean) {
                    if (isFailed) {
                        callback(TypeShowAds.FAILED)
                        check.set(false)
                    } else {
                        callback(TypeShowAds.CANCEL)
                        check.set(false)
                        AdGsManager.instance.removeAdsListener(adPlaceName = adPlaceName)
                    }
                }
    
                override fun onShowFinishSuccess() {
                    callback(TypeShowAds.SUCCESS)
                    check.set(false)
                }
    
                override fun onAdShowing() {
                    check.set(false)
                }
            })
    
            AdGsManager.instance.showAd(adPlaceName = adPlaceName, callbackShow = { adShowStatus ->
                when (adShowStatus) {
                    AdShowStatus.ERROR_WEB_VIEW -> {
                        Toasty.showToast(this, "Điện thoại không bật Android System WebView. Vui lòng kiểm tra Cài dặt -> Ứng dụng -> Android System WebView", Toasty.WARNING)
                    }
    
                    AdShowStatus.ERROR_VIP -> {
                        Toasty.showToast(this, "Bạn đã là vip", Toasty.WARNING)
                    }
    
                    else -> {
    
                    }
                }
            })
    
            NetworkUtils.hasInternetAccessCheck(doTask = {
                // nothing
            }, doException = { networkError ->
                if (!check.get()) return@hasInternetAccessCheck
                callback(TypeShowAds.TIMEOUT)
                AdGsManager.instance.removeAdsListener(adPlaceName = adPlaceName)
                when (networkError) {
                    NetworkUtils.NetworkError.SSL_HANDSHAKE -> {
                        Toasty.showToast(this, R.string.text_please_check_time, Toasty.WARNING)
                    }
    
                    else -> {
                        Toasty.showToast(this, R.string.check_network_connection, Toasty.WARNING)
                    }
                }
            }, this)
        }
    
        enum class TypeShowAds {
            SUCCESS,
            FAILED,
            TIMEOUT,
            CANCEL,
        }
```

**Quảng cáo app open**
Gồm 2 loại :

- Quảng cáo 1 lần khi mở ứng dụng

Hướng dẫn chi tiết ở [SplashActivity](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/ui/activity/splash/SplashActivity.kt)

- Quảng cáo khi trở lại ứng dụng

Hướng dẫn chi tiết ở [TestApplication](https://github.com/vtabk2/GsAdmob/blob/main/app/src/main/java/com/example/gsadmob/TestApplication.kt)

Trong hàm initConfig() là các tạo và đăng ký quảng cáo

# BaseWithAdsAdapter Adapter chứa quảng cáo native

# Lịch sử cập nhật

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



