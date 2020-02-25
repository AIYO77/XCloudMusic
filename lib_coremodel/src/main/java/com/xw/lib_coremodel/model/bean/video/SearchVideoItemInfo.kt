package com.xw.lib_coremodel.model.bean.video

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class SearchVideoItemInfo(
    val coverUrl: String,
    val title: String,
    val durationms: Long,
    val playTime: Long,
    val type: Int, //0:MV 1:视频
    val creator: List<SimpleUserInfo>,
    val aliaName: String? = null,
    val transName: String? = null,
    val vid: String
) : Serializable {
    fun isMV(): Boolean {
        return type == 0
    }
}

data class SimpleUserInfo(val userId: Long, val userName: String) : Serializable