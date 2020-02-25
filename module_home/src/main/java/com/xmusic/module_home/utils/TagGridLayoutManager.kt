package com.xmusic.module_home.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TagGridLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}