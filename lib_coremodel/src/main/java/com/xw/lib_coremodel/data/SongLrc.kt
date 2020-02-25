package com.xw.lib_coremodel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 歌曲的播放地址和歌词
 */
@Entity(tableName = "song_lrc")
data class SongLrc(
    @PrimaryKey @ColumnInfo(name = "id") val songId: Long = -1,
    var lrc: String? = "", //歌词
    var tlyric: String? = "" //翻译
)