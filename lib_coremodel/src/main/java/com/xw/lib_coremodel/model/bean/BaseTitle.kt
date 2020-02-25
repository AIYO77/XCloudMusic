package com.xw.lib_coremodel.model.bean

import androidx.annotation.ColorInt
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class BaseTitle(
    val title: String,
    @ColorInt val titleColor: Int = 0x000000,
    val titleSize: Float = 18f,
    val showMoreIcon: Boolean = false
) : Serializable