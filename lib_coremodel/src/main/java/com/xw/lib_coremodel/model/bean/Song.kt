package com.xw.lib_coremodel.model.bean

import android.os.Parcelable
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo
import com.xw.lib_coremodel.model.bean.home.ArtistInfo
import com.xw.lib_coremodel.model.bean.home.Privilege
import kotlinx.android.parcel.Parcelize

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Parcelize
data class Song(
    val name: String = "",
    val id: Long = -1,
    val ar: List<ArtistInfo>?,
    val al: AlbumItemInfo?,
    val alia: List<String>?,
    val publishTime: String = "",
    val mv: Long = 0,
    val privilege: Privilege? = null
) : Parcelable

