package com.xmusic.module_search.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.databinding.ItemSearchAlbumBinding
import com.xmusic.module_search.utils.blackColor
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xmusic.module_search.utils.grayColor
import com.xmusic.module_search.utils.keywordsColor
import com.xw.lib_common.ext.format
import com.xw.lib_common.ext.no
import com.xw.lib_common.ext.yes
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchAlbumViewHolder(private val binding: ItemSearchAlbumBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(albumItemInfo: AlbumItemInfo, keywords: String) {
        with(binding) {
            album = albumItemInfo
            val alia = albumItemInfo.alias?.reduce { acc, s -> "$acc/$s" } ?: ""
            albumTitle.text = getKeywordsSpanner(
                albumItemInfo.name,
                keywords,
                blackColor,
                keywordsColor,
                13
            ).append(getKeywordsSpanner(alia, keywords, grayColor, keywordsColor, 13))

            val artist =
                albumItemInfo.artists?.map { it.name }?.reduce { acc, s -> "$acc/$s" } ?: ""

            val timeOrSong = albumItemInfo.containedSong.isNotEmpty().yes {
                "包含歌曲 ：${albumItemInfo.containedSong}"
            }.no {
                albumItemInfo.publishTime.format("yyyy.MM.dd")
            }
            albumAr.text =
                getKeywordsSpanner(artist, keywords, grayColor, keywordsColor, 10).append(
                    getKeywordsSpanner(timeOrSong, keywords, grayColor, keywordsColor, 10)
                )

            executePendingBindings()
        }
    }
}