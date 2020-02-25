package com.xw.lib_coremodel.model.bean.home

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class TagsEntry(val category: Int, val name: String, val tags: List<PlayListCat>) :
    Serializable
