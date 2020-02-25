package com.xw.lib_coremodel.model.bean.home

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class TopListItem(
    val id: String = "",
    val tracks: List<TopListTracks>? = null,
    val updateFrequency: String = "",
    val description: String = "",
    val coverImgUrl: String = "",
    val playCount: Long = 0,
    val updateTime: String = "",
    val createTime: String = "",
    val name: String = "",
    val isTitle: Boolean = false
) : Serializable


