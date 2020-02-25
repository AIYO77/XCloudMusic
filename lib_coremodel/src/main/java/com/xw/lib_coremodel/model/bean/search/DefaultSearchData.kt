package com.xw.lib_coremodel.model.bean.search

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class DefaultSearchData(
    val showKeyword: String,
    val action: Int,
    val realkeyword: String,
    val searchType: Int,
    val alg: String,
    val gap: Int
) : Serializable