package com.xw.lib_coremodel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Entity(tableName = "playback_history")
data class PlaybackHistory(
    @PrimaryKey
    @ColumnInfo(name = "position")
    var position: Int = 0
)
