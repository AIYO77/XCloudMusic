package com.xw.lib_coremodel.model.bean.home

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.UserInfo
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class RecommendPlayList(
    val result: List<PlayListSimpleInfo>?,
    val recommend: List<PlayListSimpleInfo>?
) : BaseHttpResponse()

data class PlayListSimpleInfo(
    val id: String,
    val type: Int = -1,
    val name: String = "",
    val copywriter: String = "",
    val picUrl: String,
    val canDislike: Boolean = false,
    var playCount: Long = 0,
    val playcount: Long = 0,
    val trackCount: Long = 0,
    val highQuality: Boolean = false,
    val alg: String = "",
    val creator: UserInfo? = null // 创建者
) : Serializable
