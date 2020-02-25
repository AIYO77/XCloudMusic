package com.xw.lib_coremodel.model.bean

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class PlayUrlData(val data: MutableList<PlayUrl>) : BaseHttpResponse()

data class PlayUrl(val id: Int, val url: String, val payed: Int) : Serializable