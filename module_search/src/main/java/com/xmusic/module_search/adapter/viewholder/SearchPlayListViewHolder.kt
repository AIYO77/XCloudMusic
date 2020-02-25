package com.xmusic.module_search.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.databinding.ItemSearchPlaylistBinding
import com.xmusic.module_search.utils.blackColor
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xmusic.module_search.utils.grayColor
import com.xmusic.module_search.utils.keywordsColor
import com.xw.lib_common.ext.formatting
import com.xw.lib_coremodel.model.bean.home.PlayList

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchPlayListViewHolder(private val binding: ItemSearchPlaylistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(mPlayList: PlayList, keywords: String) {
        with(binding) {
            playlist = mPlayList
            plTitle.text =
                getKeywordsSpanner(mPlayList.name, keywords, blackColor, keywordsColor, 13)
            val des =
                "${mPlayList.trackCount}首 by ${mPlayList.creator.nickname}，播放${mPlayList.playCount.formatting()}次"
            plAr.text = getKeywordsSpanner(des, keywords, grayColor, keywordsColor, 10)
            executePendingBindings()
        }
    }
}