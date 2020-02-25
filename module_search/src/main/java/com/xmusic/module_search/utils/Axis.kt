package com.xmusic.module_search.utils

import android.util.DisplayMetrics
import com.xw.lib_common.base.BaseApplication
import kotlin.math.roundToInt

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object Axis {
    private var screenWidth = 0
    private var screenHeight = 0
    private var scaledDensity = 0f
    private const val defaultHeight = 1080f

    init {
        val metrics: DisplayMetrics =
            BaseApplication.CONTEXT.resources.displayMetrics
        scaledDensity = metrics.scaledDensity
        screenWidth = metrics.widthPixels
        screenHeight =
            if (metrics.heightPixels == 672) 720 else if (metrics.heightPixels == 1008) 1080 else metrics.heightPixels
    }

    fun scale(y: Int): Int {
        var scaleY = (y * 1.0f * screenHeight / defaultHeight).roundToInt()
        if (scaleY == 0 && y != 0) {
            scaleY = if (y < 0) -1 else 1
        }
        return scaleY
    }
}