package com.xw.lib_coremodel.data

import androidx.room.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface PlaybackListDao {
    @Query("SELECT * FROM playback_list")
    fun getPlaybackList(): List<PlaybackList>

    @Delete
    fun deleteItem(play: PlaybackList)

    @Delete
    fun deleteAll(play: List<PlaybackList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(play: PlaybackList)
}