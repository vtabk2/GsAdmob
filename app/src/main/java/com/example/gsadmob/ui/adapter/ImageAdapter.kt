package com.example.gsadmob.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.core.gsadmob.adapter.BaseWithAdsAdapter
import com.core.gsadmob.model.ItemAds
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.visible
import com.example.gsadmob.databinding.ItemAdsImageBinding
import com.example.gsadmob.databinding.ItemImageBinding
import com.example.gsadmob.model.ImageModel

class ImageAdapter(val context: Context) : BaseWithAdsAdapter(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADS -> NativeAdHolder(ItemAdsImageBinding.inflate(layoutInflater, parent, false))
            else -> ItemViewHolder(ItemImageBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        when (getItemViewType(position)) {
            ADS -> {
                (holder as? NativeAdHolder)?.let { adsHolder ->
                    (item as? ItemAds)?.let {
                        if (it.isLoading) {
                            adsHolder.nativeAd.startShimmer()
                        } else {
                            adsHolder.nativeAd.stopShimmer()
                        }
                        it.nativeAd?.let { nativeAd ->
                            adsHolder.nativeAd.setNativeAd(nativeAd)
                            adsHolder.root.visible()
                            adsHolder.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        } ?: run {
                            if (it.isLoading) {
                                adsHolder.root.visible()
                                adsHolder.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            } else {
                                adsHolder.root.gone()
                                adsHolder.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                            }
                        }
                    }
                }
            }

            else -> {
                (holder as? ItemViewHolder)?.let { itemViewHolder ->
                    (item as? ImageModel)?.let { imageModel ->
                        Glide.with(context)
                            .load(imageModel.path)
                            .into(itemViewHolder.imageIcon)
                    }
                }
            }
        }
    }

    class NativeAdHolder(itemAdsImageBinding: ItemAdsImageBinding) : RecyclerView.ViewHolder(itemAdsImageBinding.root) {
        val root = itemAdsImageBinding.root
        val nativeAd = itemAdsImageBinding.nativeAd
    }

    class ItemViewHolder(itemImageBinding: ItemImageBinding) : RecyclerView.ViewHolder(itemImageBinding.root) {
        val imageIcon = itemImageBinding.imageIcon
    }
}