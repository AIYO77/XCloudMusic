package com.xmusic.module_video.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_video.R
import com.xmusic.module_video.ui.adapter.viewholder.VideoItemViewHolder
import com.xmusic.module_video.ui.video.PlayerVideoView
import com.xw.lib_common.adapter.viewholder.NetworkStateItemViewHolder
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.video.VideoItemData
import com.xw.lib_coremodel.model.bean.video.VideoItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoListAdapter(
    private val retryCallback: () -> Unit,
    private var callback: (VideoItemData, PlayerVideoView) -> Unit
) :
    PagedListAdapter<VideoItemInfo, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_video_item -> VideoItemViewHolder.create(parent)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_video_item -> (holder as VideoItemViewHolder).binding(
                getItem(position),
                position,
                callback
            )
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState
            )
        }
    }


    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_video_item
        }
    }


    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<VideoItemInfo>() {
            override fun areContentsTheSame(
                oldItem: VideoItemInfo,
                newItem: VideoItemInfo
            ): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: VideoItemInfo, newItem: VideoItemInfo): Boolean =
                oldItem.data.vid == newItem.data.vid

        }
    }

}