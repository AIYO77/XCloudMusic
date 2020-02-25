package com.xw.lib_coremodel.model.bean.search

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class SuggestSearchResponse(val result: AllMatch) : BaseHttpResponse()

data class AllMatch(var allMatch: MutableList<SuggestSearchData>) : Serializable
data class SuggestSearchData(
    val keyword: String,
    var showKeyWord: String = "",
    val type: Int
) : Serializable