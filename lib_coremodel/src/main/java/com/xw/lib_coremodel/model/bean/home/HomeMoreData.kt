package com.xw.lib_coremodel.model.bean.home

import android.os.Parcelable
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class HomeMoreItem(
    val type: Int,
    val id: String,
    val playListSimpleInfo: PlayListSimpleInfo? = null,
    val albumAndSong: AlbumsAndSongs? = null
) : Serializable

data class AlbumsAndSongs(val albums: List<AlbumItemInfo>, val newSong: List<SongItem>) :
    Serializable


data class SongItem(
    val id: String,
    val name: String,
    val canDislike: Boolean,
    val song: SongInfo
) : Serializable

@Parcelize
data class SongInfo(
    val id: String,
    val exclusive: Boolean,
    val name: String,
    val status: Int,
    val fee: Int,
    val album: AlbumItemInfo,
    val artists: List<ArtistInfo>,
    val alias: List<String>,
    val mvid: String,
    val duration: Double,
    val privilege: Privilege
) : Parcelable