package com.xw.lib_coremodel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

@Entity(tableName = "search_type")
data class SearchType(
    @PrimaryKey
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "name")
    val name: String,
    var keywords: String = ""

)