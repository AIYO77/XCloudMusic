package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.viewholder.SearchPlayListViewHolder
import com.xmusic.module_search.databinding.ItemSearchPlaylistBinding
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchPlayListAdapter : BaseSearchAdapter<PlayList>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchPlayListViewHolder(
            ItemSearchPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchPlayListViewHolder) {
            getItem(position)?.let {
                bind(it, keywords)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PlayList>() {
            override fun areItemsTheSame(
                oldItem: PlayList,
                newItem: PlayList
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PlayList,
                newItem: PlayList
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}