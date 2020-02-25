package com.xw.lib_coremodel.model.bean.video

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class VideoTypeResponse(val data: List<VideoType>) : BaseHttpResponse()

data class VideoType(
    val id: Long,
    val name: String,
    val url: String,
    val selectTab: Boolean
) : Serializable