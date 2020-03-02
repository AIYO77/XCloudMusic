package com.xw.lib_common.ext

import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.home.ArtistInfo
import com.xw.lib_coremodel.model.bean.home.Privilege
import com.xw.lib_coremodel.model.bean.home.SongInfo
import com.xw.lib_coremodel.model.bean.info.MusicInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun Song.toMusicInfo(): MusicInfo {
    val info = MusicInfo()
    info.songId = this.id
    info.albumId = this.al?.id?.toLong()?:0
    info.albumName = this.al?.name
    info.albumPic = this.al?.picUrl
    info.artist = this.ar?.arToString()
    info.islocal = false
    info.musicName = this.name
    return info
}

fun List<ArtistInfo>.arToString(): String {
    return this.map { it.name }.reduce { acc, s -> "$acc/$s" }
}

fun List<SongInfo>.getSongAndPrivileges(): Array<Any?> {
    val arrayOf = arrayOfNulls<Any>(2)
    val list = mutableListOf<Song>()
    val privileges = mutableListOf<Privilege>()

    try {
        this.forEach {
            list.add(
                Song(
                    id = it.id.toLong(),
                    name = it.name,
                    ar = it.artists,
                    al = it.album,
                    alia = it.alias,
                    mv = it.mvid.toLong()
                )
            )
            privileges.add(it.privilege)
        }
        arrayOf[0] = list
        arrayOf[1] = privileges
    } catch (e: Exception) {
        Logger.e(e.toString())
    }
    return arrayOf
}

fun IntArray.findMax(): Int {
    var max = this[0]
    this.forEach {
        if (it > max) {
            max = it
        }
    }
    return max
}