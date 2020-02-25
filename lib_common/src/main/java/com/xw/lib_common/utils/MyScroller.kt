package com.xw.lib_common.utils

import android.content.Context
import android.view.animation.LinearInterpolator
import android.widget.Scroller

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MyScroller(context: Context) :
    Scroller(context, LinearInterpolator()) {

    var noDuration: Boolean = false

    companion object {
        private const val VIEWPAGER_SCROLL_TIME = 390
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        if (noDuration) {
            super.startScroll(startX, startY, dx, dy, 0)
        } else {
            super.startScroll(startX, startY, dx, dy, VIEWPAGER_SCROLL_TIME)
        }
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        if (noDuration) {
            super.startScroll(startX, startY, dx, dy, 0)
        } else {
            super.startScroll(startX, startY, dx, dy, VIEWPAGER_SCROLL_TIME)
        }

    }
}