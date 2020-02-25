package com.xw.lib_coremodel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Entity(tableName = "recent_history")
data class RecentHistory(
    @PrimaryKey @ColumnInfo(name = "songId")
    val songId: Long
)