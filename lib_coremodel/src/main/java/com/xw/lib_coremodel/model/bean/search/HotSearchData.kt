package com.xw.lib_coremodel.model.bean.search

import com.xw.lib_coremodel.data.SearchHistory
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class HotSearchData(
    val searchWord: String = "",
    val score: String = "",
    val content: String = "",
    val source: Int = 0,
    val iconUrl: String = "",
    val url: String = "",
    val type: Int = 1,
    val alg: String = "",
    val history: List<SearchHistory>? = null
) : Serializable