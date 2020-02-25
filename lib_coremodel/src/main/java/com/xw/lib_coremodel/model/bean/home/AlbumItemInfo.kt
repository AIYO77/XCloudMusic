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
data class AlbumListData(val albums: List<AlbumItemInfo>) : BaseHttpResponse()

@Parcelize
data class AlbumItemInfo(
    val id: String = "-1",
    val name: String = "",
    val company: String = "",
    val copyrightId: String = "", //版权ID
    val publishTime: Long = 0L, //发行时间
    val artists: List<ArtistInfo>? = null,
    val artist: ArtistInfo? = null,
    val alias: List<String>? = null,
    val description: String = "",
    val info: AlbumHeaderInfo? = null,
    val subType: String = "", // 专辑类别
    val picUrl: String = "",
    val containedSong: String = "" //包含歌曲
) : Parcelable

@Parcelize
data class ArtistInfo(
    val id: String,
    val name: String,
    val albumSize: Int,
    val followed: Boolean,
    val briefDesc: String,
    val musicSize: Int,
    val img1v1Url: String,
    val picUrl: String,
    val trans: String,
    val mvSize: Int = 0,
    val alias: List<String>,
    val transNames: List<String>? = null,
    val accountId: Long? = null //不为空就是已入驻
) : Parcelable

data class AlbumHeaderInfo(
    val liked: Boolean,
    val commentCount: Long,
    val shareCount: Long,
    val likedCount: Long
) : Serializable