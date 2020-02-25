package com.xw.lib_coremodel.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface PlaybackHistoryDao {
    @Query("SELECT * FROM playback_history")
    fun getHistory(): List<PlaybackHistory>

    @Insert
    fun insertItem(history: PlaybackHistory)

    @Delete
    fun deleteAll(history: List<PlaybackHistory>)
}