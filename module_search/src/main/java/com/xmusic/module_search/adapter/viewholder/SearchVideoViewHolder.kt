package com.xmusic.module_search.adapter.viewholder

import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemSearchVideoBinding
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.model.bean.video.SearchVideoItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchVideoViewHolder(private val binding: ItemSearchVideoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(videoItemInfo: SearchVideoItemInfo, keywords: String) {
        with(binding) {
            video = videoItemInfo
            val titleSpanner = videoItemInfo.title.contentEquals(keywords).yes {
                getKeywordsSpanner(videoItemInfo.title, keywords, blackColor, keywordsColor, 14)
            }.no {
                SpannableStringBuilder().color(getColor(R.color.color_363636)) {
                    append(
                        videoItemInfo.title
                    )
                }
            }
            title.text = titleSpanner
            val timeString = videoItemInfo.durationms.makeTimeString()
            val creator = videoItemInfo.creator.map { it.userName }.reduce { acc, s -> "$acc/$s" }
            val timeAndCreatorSpanner =
                if (videoItemInfo.isMV()) {
                    title.setCompoundDrawables(getDrawable(R.drawable.icon_mv), null, null, null)
                    title.compoundDrawablePadding = 4f.dip2px()
                    getKeywordsSpanner(
                        "$timeString $creator", keywords, grayColor,
                        keywordsColor, 13
                    )
                } else {
                    title.setCompoundDrawables(null, null, null, null)
                    getKeywordsSpanner(
                        "$timeString by $creator", keywords, grayColor,
                        keywordsColor, 13
                    )
                }

            timeAndCreator.text = timeAndCreatorSpanner
            executePendingBindings()
        }
    }

    companion object {
        private val blackColor =
            ColorStateList.valueOf(getColor(R.color.color_363636))
        private val grayColor =
            ColorStateList.valueOf(getColor(R.color.black_979797))
        private val keywordsColor =
            ColorStateList.valueOf(getColor(R.color.banner_5784ad))
    }
}