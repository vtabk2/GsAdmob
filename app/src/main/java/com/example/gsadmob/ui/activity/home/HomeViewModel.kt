package com.example.gsadmob.ui.activity.home

import androidx.lifecycle.ViewModel
import com.core.gsadmob.model.ItemAds
import com.example.gsadmob.TestApplication
import com.example.gsadmob.model.ImageModel
import com.example.gsadmob.utils.LoadSavedUtils
import com.gs.core.utils.livedata.SingleLiveEvent
import java.util.Calendar

class HomeViewModel : ViewModel() {
    val imageListLiveData = SingleLiveEvent<MutableList<Any>>()

    fun loadData() {
        val imageList = mutableListOf<Any>()
        imageList.add("" + Calendar.getInstance().time)
        LoadSavedUtils.listAssetFiles(TestApplication.applicationContext(), "image").forEach { path ->
            imageList.add(ImageModel(path = "file:///android_asset/$path"))
        }
        imageList.add(4, ItemAds())
        imageListLiveData.postValue(imageList)
    }
}