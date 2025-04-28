package com.example.gsadmob.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.core.gsadmob.adapter.BaseWithAdsAdapter
import com.example.gsadmob.R
import com.example.gsadmob.databinding.ItemImageBinding
import com.example.gsadmob.model.ImageModel

class ImageAdapter(val context: Context) : BaseWithAdsAdapter(context) {
    override val nativeAdLayoutId: Int = R.layout.item_ads_image

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(ItemImageBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(itemList[position] as ImageModel)
    }

    inner class ItemViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageModel) {
            Glide.with(context)
                .load(item.path)
                .into(binding.imageIcon)
        }
    }
}