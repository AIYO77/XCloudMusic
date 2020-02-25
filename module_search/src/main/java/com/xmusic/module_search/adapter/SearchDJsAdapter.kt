package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.viewholder.SearchDJViewHolder
import com.xmusic.module_search.databinding.ItemSearchDjBinding
import com.xw.lib_coremodel.model.bean.dj.DJInfo
import com.xw.lib_coremodel.model.bean.dj.DjRadioInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchDJsAdapter : BaseSearchAdapter<DjRadioInfo>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchDJViewHolder(
            ItemSearchDjBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchDJViewHolder) {
            bind(getItem(position), keywords)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DjRadioInfo>() {
            override fun areItemsTheSame(oldItem: DjRadioInfo, newItem: DjRadioInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DjRadioInfo, newItem: DjRadioInfo): Boolean {
                return oldItem.id == newItem.id
            }


        }
    }

}