package com.xmusic.module_home.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.xmusic.module_home.R
import com.xmusic.module_home.adapter.viewholder.PlayListDetailViewHolder
import com.xmusic.module_home.ui.activity.PlayListActivity
import com.xw.lib_common.adapter.viewholder.NetworkStateItemViewHolder
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.home.PlayList


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListSquareAdapter(
    private val retryCallback: () -> Unit
) : PagedListAdapter<PlayList, RecyclerView.ViewHolder>(PLAYLIST_DATA) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_play_list_detail -> PlayListDetailViewHolder.create(parent)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_play_list_detail -> {
                val playList = getItem(position)
                (holder as PlayListDetailViewHolder).bind(
                    playList, createItemClick(getItem(position))
                )
                if (position == 0) {
                    LiveEventBus.get("url").post(playList?.coverImgUrl)
                }
            }
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState
            )
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_play_list_detail
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (getItemViewType(position)) {
                        R.layout.item_play_list_detail -> 1
                        else -> 3
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    private fun createItemClick(item: PlayList?): View.OnClickListener {
        return View.OnClickListener {
            item?.let { playlist ->
                PlayListActivity.lunch(it.context, playlist)
            }
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

    companion object {

        val PLAYLIST_DATA = object : DiffUtil.ItemCallback<PlayList>() {
            override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}