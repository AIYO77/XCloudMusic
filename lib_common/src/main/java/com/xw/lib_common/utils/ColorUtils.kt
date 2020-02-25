package com.xw.lib_common.utils

import com.xw.lib_common.R

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

object ColorUtils {

    fun getColorOfName(name: String?): Int {
        return when (name) {
            "red" -> {
                R.drawable.banner_title_color_red
            }
            "blue" -> {
                R.drawable.banner_title_color_blue
            }
            else -> {
                R.drawable.banner_title_color_red
            }
        }
    }

}