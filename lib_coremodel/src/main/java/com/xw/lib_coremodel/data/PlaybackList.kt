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
@Entity(tableName = "playback_list")
data class PlaybackList(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "trackId")
    var trackId: Long = -1,
    var sourcePosition: Int = 0
)