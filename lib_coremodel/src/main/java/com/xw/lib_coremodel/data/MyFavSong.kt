package com.xw.lib_coremodel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 我喜欢的音乐
 */
@Entity(tableName = "my_fav_song")
data class MyFavSong(
    @PrimaryKey @ColumnInfo(name = "id") val songId: Long,
    val songName: String

)