package com.xmusic.module_search.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.databinding.ItemSearchDjBinding
import com.xmusic.module_search.utils.blackColor
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xmusic.module_search.utils.grayColor
import com.xmusic.module_search.utils.keywordsColor
import com.xw.lib_coremodel.model.bean.dj.DjRadioInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchDJViewHolder(private val binding: ItemSearchDjBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(djRadioInfo: DjRadioInfo, keywords: String) {
        with(binding) {
            dj = djRadioInfo
            djTitle.text =
                getKeywordsSpanner(djRadioInfo.name, keywords, blackColor, keywordsColor, 13)
            djAr.text = getKeywordsSpanner(
                djRadioInfo.dj.nickname, keywords, grayColor,
                keywordsColor, 10
            )
            executePendingBindings()
        }
    }
}