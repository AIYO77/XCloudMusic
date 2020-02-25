package com.xw.lib_coremodel.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface SongUrlAndLrcDao {
    @Query("SELECT * FROM song_lrc WHERE id = :id")
    fun getSong(id: Long): SongLrc?

    @Insert
    fun insert(songLrc: SongLrc)

    @Update
    fun updateSong(songLrc: SongLrc)
}