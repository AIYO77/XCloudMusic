package com.xw.lib_common.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import com.xw.lib_common.utils.ViewPagerHelper
import kotlin.math.abs


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
open class SuperViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private val helper: ViewPagerHelper = ViewPagerHelper(this)

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val scroller = helper.scroller
        if (abs(currentItem - item) > 1) {
            scroller.noDuration = true
            super.setCurrentItem(item, smoothScroll)
            scroller.noDuration = false
        } else {
            scroller.noDuration = false
            super.setCurrentItem(item, smoothScroll)
        }
    }
}