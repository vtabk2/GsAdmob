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
                    implementation 'com.github.vtabk2:GsAdmob:1.2.14'
            }
```

***Cấu hình quảng cáo*** 
- Thay đổi cấu hình quảng cáo trong config_admob.xml

***BaseAdsActivity***

Các hàm cơ bản được dùng trong đây

- setupNative override để xử lý cho từng activity
- startNativeShimmer override để bắt đầu chạy shimmer load cho từng activity
- registerAds nếu không muốn load tất cả quảng cáo thì có thể override lại

***VipPreferences*** Nơi lưu trạng thái đã mua vip

- Khởi tạo ở Application

```css
        VipPreferences.instance.initVipPreferences(this, BuildConfig.APPLICATION_ID)
```

- Đăng ký thay đổi trạng thái mua vip ở Application (keyVipList là danh sách key vip của ứng dụng)

```css
        private val mainScope = MainScope()
        
        mainScope.launch {
            VipPreferences.instance.getVipChangeFlow(keyVipList)
                .stateIn(mainScope, SharingStarted.Eagerly, VipPreferences.instance.isFullVersion(keyVipList))
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


***Banner***

Chú ý adsShowType có các kiểu hiển thị khác nhau

-   showIfSuccess: Quảng cáo chỉ chiếm kích thước và hiển thị khi quảng cáo được tải thành công
-   alwaysShow: Quảng cáo luôn chiếm kích thước và hiển thị nếu quảng cáo đươc tải thành công
-   hide: Ẩn quảng cáo đi nhưng vẫn chiếm kích thước và không hiển thị ngay cả khi quảng cáo được tải thành công (được dùng khi đang show quảng cáo app open hiển thị thì tạm ẩn banner đi chẳng hạn)
-   gone: Ẩn quảng cáo đi không chiếm kích thước và không hiển thị ngày cả khi quảng cáo được tải thành công

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

**Native Ads**

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

**Hướng dẫn GDPR xem ở SplashActivity**

**Cách load quảng cáo**

Tạo cách AdPlaceName trước giống cấu trúc ở AdPlaceNameConfig

- Show quảng cáo xen kẽ nếu có

```css
        bindingView.tvInterstitial.setOnClickListener {
            startActivity(Intent(this, TestNativeActivity::class.java))
            AdGsManager.instance.showAd(adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL)
            // chuyển màn thì cần cancel tất cả các rewarded đi
            AdGsManager.instance.cancelAllRewardAd()
        }
```

- Quảng cáo banner

```css

        lifecycleScope.launch {
            async {
                AdGsManager.instance.isVipFlow.collect {
                    isVip = it
                    if (isVip) {
                        bindingView.tvActiveVip.text = "Vip Active"
                    } else {
                        bindingView.tvActiveVip.text = "Vip Inactive"
                    }
                }
            }

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

- Quảng cáo native

```css
        lifecycleScope.launch {
            async {
                AdGsManager.instance.isVipFlow.collect { isVip->
                    if (isVip) {
                        bindingView.tvActiveVip.text = "Vip Active"
                    } else {
                        bindingView.tvActiveVip.text = "Vip Inactive"
                    }
                }
            }

            async {
                AdGsManager.instance.adGsDataMapMutableStateFlow.collect {
                    it.forEach { adGsDataMap ->
                        when (adGsDataMap.key) {
                            AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                                if (adGsDataMap.value.isLoading) {
                                    bindingView.bannerView.startShimmer()
                                } else {
                                    bindingView.bannerView.setBannerAdView((adGsDataMap.value as? BannerAdGsData)?.bannerAdView)
                                }
                            }

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
                        AdPlaceNameConfig.AD_PLACE_NAME_BANNER -> {
                            bindingView.bannerView.startShimmer()
                        }

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
- Quảng cáo trả thưởng

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
