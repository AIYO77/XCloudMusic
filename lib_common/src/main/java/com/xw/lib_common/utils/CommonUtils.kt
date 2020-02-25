package com.xw.lib_common.utils

import android.content.Context
import android.util.DisplayMetrics

import android.view.WindowManager




/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object CommonUtils {

    private var screenWidth = 0
    private var screenHeight = 0

    @JvmStatic
    fun dip2px(contex: Context, dpValue: Float): Int {
        val scale = contex.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


    fun getScreenWidth(context: Context): Int {
        if (screenWidth <= 0) {
            readScreenInfo(context)
        }
        return screenWidth
    }

    fun getScreenHeight(context: Context): Int {
        if (screenHeight <= 0) {
            readScreenInfo(context)
        }
        return screenHeight
    }


   private fun readScreenInfo(context: Context) {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        screenHeight = outMetrics.heightPixels
        screenWidth = outMetrics.widthPixels
    }
}