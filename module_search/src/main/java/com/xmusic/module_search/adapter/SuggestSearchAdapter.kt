package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemSuggestSearchBinding
import com.xw.lib_common.ext.getColor
import com.xw.lib_coremodel.model.bean.search.SuggestSearchData

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SuggestSearchAdapter(private val onItemClick: (SuggestSearchData) -> Unit) :
    ListAdapter<SuggestSearchData, SuggestSearchAdapter.SuggestSearchViewHolder>(ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestSearchViewHolder {
        return SuggestSearchViewHolder(
            ItemSuggestSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SuggestSearchViewHolder, position: Int) {
        holder.bind(currentList[position], View.OnClickListener {
            onItemClick.invoke(currentList[position])
        })
    }

    class SuggestSearchViewHolder(private val bind: ItemSuggestSearchBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(suggestData: SuggestSearchData, onClickListener: View.OnClickListener) {
            with(bind) {
                suggest = suggestData
                if (suggestData.type < 0) {
                    keywordsTv.apply {
                        setTextColor(getColor(R.color.banner_5784ad))
                        text = suggestData.showKeyWord
                        textSize = 16f
                    }
                } else {
                    keywordsTv.apply {
                        setTextColor(getColor(R.color.black_sixty_percent))
                        text = suggestData.keyword
                        textSize = 15f
                    }
                }
                onClick = onClickListener
                executePendingBindings()
            }
        }
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<SuggestSearchData>() {
            override fun areItemsTheSame(
                oldItem: SuggestSearchData,
                newItem: SuggestSearchData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SuggestSearchData,
                newItem: SuggestSearchData
            ): Boolean {
                return oldItem.keyword == newItem.keyword
            }
        }
    }

}