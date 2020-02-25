package com.xw.lib_coremodel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey
    val keywords: String,
    val time: Long = System.currentTimeMillis()
)