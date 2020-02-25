package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.viewholder.SearchAlbumViewHolder
import com.xmusic.module_search.databinding.ItemSearchAlbumBinding
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchAlbumsAdapter : BaseSearchAdapter<AlbumItemInfo>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchAlbumViewHolder(
            ItemSearchAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchAlbumViewHolder) {
            bind(getItem(position), keywords)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AlbumItemInfo>() {
            override fun areItemsTheSame(oldItem: AlbumItemInfo, newItem: AlbumItemInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: AlbumItemInfo,
                newItem: AlbumItemInfo
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}