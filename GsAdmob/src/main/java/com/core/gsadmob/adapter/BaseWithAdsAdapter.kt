package com.core.gsadmob.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.core.gsadmob.R
import com.core.gsadmob.model.ItemAds
import com.core.gsadmob.natives.view.NativeGsAdView
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.visible
import com.google.android.gms.ads.nativead.NativeAd

abstract class BaseWithAdsAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var itemList: MutableList<Any> = mutableListOf()

    /**
     * Dùng cho trường hợp dữ liệu lấy được quá chậm sau khi khởi tải được quảng cáo rồi
     */
    private var isStartShimmer: Boolean = false

    /**
     * Layout id native quảng cáo
     */
    open val nativeAdLayoutId: Int = R.layout.ad_item_native

    /**
     * Id của NativeGsAdView
     */
    open val nativeAdId: Int = R.id.nativeAd

    /**
     * canCheckUpdateCallActionButton = true -> quảng cáo native có thay đổi trạng thái nút dựa trên item trước nó
     * canCheckUpdateCallActionButton = false -> quảng cáo native không có thay đổi trạng thái nút dựa trên item trước nó
     */
    open val canCheckUpdateCallActionButton = false

    /**
     * Nó quyết định xem 2 đối tượng có cùng items hay là không?
     */
    open fun areItemsTheSameDiff(oldItem: Any, newItem: Any): Boolean = true

    /**
     * Nó quyết định xem 2 items có cùng dữ liệu hay là không?. Phương thức này chỉ được gọi khi areItemsTheSameDiff() trả về true.
     */
    open fun areContentsTheSameDiff(oldItem: Any, newItem: Any): Boolean = true

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is ItemAds -> ADS
            else -> ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADS -> onCreateAdViewHolder(parent)
            else -> onCreateItemViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NativeAdHolder -> onBindAdViewHolder(holder, position)
            else -> onBindItemViewHolder(holder, position)
        }
    }

    /**
     * Hàm khởi tạo item quảng cáo native
     */
    open fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NativeAdHolder(layoutInflater.inflate(nativeAdLayoutId, parent, false))
    }

    /**
     * Hàm cập nhật dữ liệu quảng cáo native
     */
    open fun onBindAdViewHolder(holder: NativeAdHolder, position: Int) {
        holder.bind(itemList[position] as ItemAds)
    }

    /**
     * Hàm khởi tạo các item khác ItemAds
     */
    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    /**
     * Hàm cập nhật dữ liệu vào item
     */
    abstract fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemCount(): Int {
        return itemList.size
    }

    open fun setData(list: MutableList<Any>) {
        if (!isStartShimmer) {
            // Lấy dữ liệu quảng cáo đã có trong danh sách cũ
            val adsList = itemList.filter { it is ItemAds && it.nativeAd != null }

            // Cập nhật dữ liệu quảng cáo đã có vào danh sách mới
            list.filterIsInstance<ItemAds>().forEachIndexed { index, item ->
                adsList.getOrNull(index)?.let {
                    if (it is ItemAds) {
                        item.nativeAd = it.nativeAd
                        item.isLoading = it.isLoading
                    }
                }
            }
        }

        itemList.clear()
        itemList.addAll(list)
        if (isStartShimmer) {
            itemList.forEach { item ->
                if (item is ItemAds) {
                    item.isLoading = true
                }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * Thêm dữ liệu và có kiểm tra sự khác biệt dữ liệu
     */
    open fun setDataWithCalculateDiff(list: MutableList<Any>) {
        if (isStartShimmer) {
            list.forEach { item ->
                if (item is ItemAds) {
                    item.isLoading = true
                }
            }
        } else {
            // Lấy dữ liệu quảng cáo đã có trong danh sách cũ
            val adsList = itemList.filter { it is ItemAds && it.nativeAd != null }

            // Cập nhật dữ liệu quảng cáo đã có vào danh sách mới
            list.filterIsInstance<ItemAds>().forEachIndexed { index, item ->
                adsList.getOrNull(index)?.let {
                    if (it is ItemAds) {
                        item.nativeAd = it.nativeAd
                        item.isLoading = it.isLoading
                    }
                }
            }
        }
        calculateDiff(list)
        itemList.clear()
        itemList.addAll(list)
    }

    /**
     * Xác định xem danh sách dữ liệu có thay đổi không?
     */
    open fun calculateDiff(newList: MutableList<Any>) {
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtil(oldList = itemList, newList = newList))
        diffResult.dispatchUpdatesTo(this)
    }

    open fun setupItemAds(nativeAd: NativeAd?, isStartShimmer: Boolean) {
        if (isStartShimmer && nativeAd == null) {
            startShimmer()
        } else {
            stopShimmer(nativeAd)
        }
    }

    /**
     * Hiển thị shimmer
     */
    fun startShimmer() {
        isStartShimmer = true
        itemList.forEachIndexed { index, item ->
            if (item is ItemAds) {
                item.isLoading = true
                notifyItemChanged(index)
            }
        }
    }

    /**
     * Ẩn shimmer
     */
    fun stopShimmer(nativeAd: NativeAd?) {
        isStartShimmer = false
        itemList.forEachIndexed { index, item ->
            if (item is ItemAds) {
                item.isLoading = false
                item.nativeAd = nativeAd
                notifyItemChanged(index)
            }
        }
    }

    open fun getBackgroundResourceCallActionButton(position: Int): Int {
        return 0
    }

    /**
     * Kiểm tra xem có dữ liệu nào khác ItemAds không?
     */
    fun isEmpty(): Boolean {
        return itemList.none { it !is ItemAds }
    }

    /**
     * Kiểm tra xem item có position này có phải là ItemAds không?
     */
    fun isAds(position: Int): Boolean {
        return itemList[position] is ItemAds
    }

    /**
     * Phần xử lý quảng cáo native
     */
    inner class NativeAdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nativeGsAdView: NativeGsAdView? = itemView.findViewById(nativeAdId)

        fun bind(itemAds: ItemAds) {
            nativeGsAdView?.let {
                it.setNativeAd(nativeAd = itemAds.nativeAd, isStartShimmer = itemAds.isLoading)

                itemAds.nativeAd?.let { _ ->
                    if (canCheckUpdateCallActionButton) {
                        it.updateCallActionButton(getBackgroundResourceCallActionButton(adapterPosition))
                    }

                    itemView.visible()
                    itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                } ?: run {
                    if (itemAds.isLoading) {
                        itemView.visible()
                        itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    } else {
                        itemView.gone()
                        itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    }
                }
            } ?: run {
                itemView.gone()
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
    }

    inner class BaseDiffUtil(private val oldList: MutableList<Any>, private val newList: MutableList<Any>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSameDiff(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSameDiff(oldList[oldItemPosition], newList[newItemPosition])
        }
    }

    companion object {
        const val ITEM = 0
        const val ADS = 1
    }
}