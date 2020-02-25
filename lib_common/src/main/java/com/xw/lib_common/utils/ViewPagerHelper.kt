package com.xw.lib_common.utils

import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class ViewPagerHelper(viewPager: ViewPager) {

    var scroller: MyScroller = MyScroller(viewPager.context)

    init {
        try {
            val mField = ViewPager::class.java.getDeclaredField("mScroller")
            mField.isAccessible = true
            mField.set(viewPager, scroller)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}