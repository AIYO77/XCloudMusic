package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.viewholder.SearchVideoViewHolder
import com.xmusic.module_search.databinding.ItemSearchVideoBinding
import com.xw.lib_coremodel.model.bean.video.SearchVideoItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchVideosAdapter : BaseSearchAdapter<SearchVideoItemInfo>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchVideoViewHolder(
            ItemSearchVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchVideoViewHolder) {
            getItem(position)?.let {
                bind(it, keywords)
            }
            itemView.setOnClickListener {

            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SearchVideoItemInfo>() {
            override fun areItemsTheSame(oldItem: SearchVideoItemInfo, newItem: SearchVideoItemInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SearchVideoItemInfo,
                newItem: SearchVideoItemInfo
            ): Boolean {
                return oldItem.vid == newItem.vid
            }

        }
    }

}