package com.xmusic.module_search.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemSearchSingerBinding
import com.xmusic.module_search.utils.blackColor
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xmusic.module_search.utils.grayColor
import com.xmusic.module_search.utils.keywordsColor
import com.xw.lib_common.ext.specifyLoad
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.model.bean.home.ArtistInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchSingerViewHolder(private val binding: ItemSearchSingerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(artistInfo: ArtistInfo, keywords: String) {
        with(binding) {
            singer = artistInfo
            singerName.text = if (artistInfo.alias.isNullOrEmpty().not()){
                getKeywordsSpanner(artistInfo.name, keywords, blackColor, keywordsColor, 13).append(
                    getKeywordsSpanner(
                        "(${artistInfo.alias.reduce { acc, s -> "$acc/$s" }})", keywords, grayColor,
                        keywordsColor, 13
                    )
                )
            }else{
                getKeywordsSpanner(artistInfo.name, keywords, blackColor, keywordsColor, 13)
            }

            GlideUtils.loadImageCircleCrop(
                binding.root.context, singerLogo, artistInfo.picUrl.specifyLoad(50, 50),
                R.drawable.icon_user_circle
            )
            executePendingBindings()
        }
    }
}