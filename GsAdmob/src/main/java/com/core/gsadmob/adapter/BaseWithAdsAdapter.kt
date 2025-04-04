package com.core.gsadmob.adapter

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.core.gsadmob.model.ItemAds
import com.google.android.gms.ads.nativead.NativeAd

abstract class BaseWithAdsAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var itemList: MutableList<Any> = mutableListOf()
    var isStartShimmer: Boolean = false

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is ItemAds -> ADS
            else -> ITEM
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    open fun setData(list: MutableList<Any>) {
        itemList.clear()
        itemList.addAll(list)
        if (isStartShimmer) {
            itemList.filterIsInstance<ItemAds>().forEach {
                it.isLoading = true
            }
        }
        notifyDataSetChanged()
    }

    open fun setupItemAds(nativeAd: NativeAd?, isStartShimmer: Boolean) {
        if (isStartShimmer && nativeAd == null) {
            startShimmer()
        } else {
            stopShimmer(nativeAd)
        }
    }

    fun startShimmer() {
        isStartShimmer = true
        itemList.filterIsInstance<ItemAds>().forEach {
            val index = itemList.indexOf(it)
            it.isLoading = true
            notifyItemChanged(index)
        }
    }

    fun stopShimmer(nativeAd: NativeAd?) {
        isStartShimmer = false
        itemList.filterIsInstance<ItemAds>().forEach {
            val index = itemList.indexOf(it)
            it.isLoading = false
            it.nativeAd = nativeAd
            notifyItemChanged(index)
        }
    }

    fun isEmpty(): Boolean {
        return itemList.none { it !is ItemAds }
    }

    fun isAds(position: Int): Boolean {
        return itemList[position] is ItemAds
    }

    companion object {
        const val ITEM = 0
        const val ADS = 1
    }
}