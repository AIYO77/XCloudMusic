package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.viewholder.SearchSingerViewHolder
import com.xmusic.module_search.databinding.ItemSearchSingerBinding
import com.xw.lib_coremodel.model.bean.home.ArtistInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchSingersAdapter : BaseSearchAdapter<ArtistInfo>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchSingerViewHolder(
            ItemSearchSingerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchSingerViewHolder) {
            getItem(position)?.let {
                bind(it, keywords)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ArtistInfo>() {
            override fun areItemsTheSame(oldItem: ArtistInfo, newItem: ArtistInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArtistInfo, newItem: ArtistInfo): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}