package com.xw.lib_coremodel.model.bean.home

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.UserInfo
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class PlayListData(val playlist: PlayList, val privileges: List<Privilege>) :
    BaseHttpResponse()

data class Privilege(
    val id: String,
    val fee: Int,// 1:VIP 8:独家
    val maxbr: Int // == 999000 SQ 音质
) : Serializable

@Parcelize
data class TrackId(val id: String) : Parcelable

