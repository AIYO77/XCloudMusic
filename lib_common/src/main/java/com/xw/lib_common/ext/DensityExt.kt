package com.xw.lib_common.ext

import com.xw.lib_common.base.BaseApplication

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun Float.dip2px(): Int {
    val scale = BaseApplication.CONTEXT.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Float.dip2pxF(): Float {
    val scale = BaseApplication.CONTEXT.resources.displayMetrics.density
    return this * scale + 0.5f
}