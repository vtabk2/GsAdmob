package com.example.gsadmob.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.core.gsadmob.adapter.BaseWithAdsAdapter
import com.core.gsadmob.model.ItemAds
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ItemImageBinding
import com.example.gsadmob.databinding.ItemTitleBinding
import com.example.gsadmob.model.ImageModel

class ImageAdapter(val context: Context) : BaseWithAdsAdapter(context) {
    override val nativeAdLayoutId: Int = R.layout.item_ads_image

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is String -> TITLE
            is ItemAds -> ADS
            else -> ITEM
        }
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLE -> TitleViewHolder(ItemTitleBinding.inflate(layoutInflater, parent, false))
            else -> ItemViewHolder(ItemImageBinding.inflate(layoutInflater, parent, false))
        }

    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> {
                holder.bind(itemList[position] as String)
            }

            is ItemViewHolder -> {
                holder.bind(itemList[position] as ImageModel)
            }
        }
    }

    fun isTitle(position: Int): Boolean {
        return itemList[position] is String
    }

    inner class TitleViewHolder(private val binding: ItemTitleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.tvTitle.text = title
        }
    }

    inner class ItemViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageModel) {
            Glide.with(context)
                .load(item.path)
                .into(binding.imageIcon)
        }
    }

    companion object {
        const val TITLE = 2
    }
}