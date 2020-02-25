package com.xw.lib_coremodel.model.bean.home

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

data class Banner(
    val pic: String,
    val titleColor: String,
    val typeTitle: String,
    val targetId: String,
    val targetType: Int,
    val url: String?,
    val song: SongInfo?
) : Serializable