package com.xmusic.module_search.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemSearchSongBinding
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.show
import com.xw.lib_coremodel.model.bean.Song

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchSongViewHolder(private val binding: ItemSearchSongBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        song: Song,
        keywords: String,
        videoClickListener: View.OnClickListener,
        moreClickListener: View.OnClickListener
    ) {
        with(binding) {
            setSong(song)
            onVideoClick = videoClickListener
            onMoreClick = moreClickListener

            val nameBuilder: SpannableStringBuilder =
                if (song.name.contentEquals(keywords)) {
                    getKeywordsSpanner(song.name, keywords, blackColor, keywordsColor, 15)
                } else {
                    SpannableStringBuilder().color(getColor(R.color.black_2d2d2d)) { append(song.name) }
                }
            var aliaBuilder: SpannableStringBuilder? = null
            if (song.alia.isNullOrEmpty().not()) {
                aliaBuilder = getAliaSpanner(song.alia, keywords, 15, R.color.black_999999)
            }

            val arSpanner = song.ar?.map { it.name }?.reduce { acc, s -> "$acc/$s" }?.let {
                if (it.contentEquals(keywords)) {
                    getKeywordsSpanner(it, keywords, grayColor, keywordsColor, 13)
                } else {
                    SpannableStringBuilder().color(getColor(R.color.black_979797)) { append(it) }
                }
            }
            val alSpanner = song.al?.name?.let {
                if (it.contentEquals(keywords)) {
                    getKeywordsSpanner(it, keywords, lightGrayColor, keywordsColor, 13)
                } else {
                    SpannableStringBuilder().color(getColor(getColor(R.color.black_979797))) {
                        append(
                            it
                        )
                    }
                }
            }
            if (aliaBuilder == null) {
                name.text = nameBuilder
            } else {
                name.text = nameBuilder.append(aliaBuilder)
            }

            arAndAl.text = arSpanner?.append("-")?.append(alSpanner)
            if (song.alia.isNullOrEmpty().not()) {
                alia.show()
                alia.text = getAliaSpanner(song.alia, keywords, 13, R.color.black_979797)
            } else {
                alia.gone()
            }
            executePendingBindings()
        }
    }

    private fun getAliaSpanner(
        alia: List<String>?,
        keywords: String,
        textSize: Int,
        @ColorRes defaultColor: Int
    ): SpannableStringBuilder {
        val mAlia = "(${alia?.reduce { acc, s -> "$acc/$s" }})"
        return if (mAlia.contentEquals(keywords)) {

            getKeywordsSpanner(
                mAlia,
                keywords,
                ColorStateList.valueOf(getColor(defaultColor)),
                keywordsColor,
                textSize
            )
        } else {
            SpannableStringBuilder().color(getColor(defaultColor)) { append(mAlia) }
        }
    }

    companion object {
        private val blackColor =
            ColorStateList.valueOf(getColor(R.color.black_2d2d2d))
        private val grayColor =
            ColorStateList.valueOf(getColor(R.color.black_979797))
        private val lightGrayColor =
            ColorStateList.valueOf(getColor(R.color.black_999999))
        private val keywordsColor =
            ColorStateList.valueOf(getColor(R.color.banner_5784ad))
    }
}