package com.xmusic.module_search.utils

import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import androidx.core.text.color
import com.xmusic.module_search.R
import com.xw.lib_common.ext.getColor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun getKeywordsSpanner(
    content: String,
    keywords: String,
    defaultColor: ColorStateList,
    keywordsColor: ColorStateList,
    txtSize: Int
): SpannableStringBuilder {
    return SpannableStringBuilder(content).apply {
        if (content.contentEquals(keywords)) {
            val nameIndexKey = content.indexOf(keywords)
            setSpan(
                TextAppearanceSpan(null, 0, Axis.scale(txtSize), defaultColor, null),
                0,
                nameIndexKey,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                TextAppearanceSpan(null, 0, Axis.scale(txtSize), keywordsColor, null),
                nameIndexKey,
                keywords.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                TextAppearanceSpan(null, 0, Axis.scale(txtSize), defaultColor, null),
                nameIndexKey + keywords.length,
                content.length,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        } else {
            color(defaultColor.defaultColor) {}
        }

    }
}

val blackColor =
    ColorStateList.valueOf(getColor(R.color.color_363636))
val grayColor =
    ColorStateList.valueOf(getColor(R.color.black_979797))
val keywordsColor =
    ColorStateList.valueOf(getColor(R.color.banner_5784ad))