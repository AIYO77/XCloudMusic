package com.xw.lib_coremodel.model.bean.home

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import kotlinx.android.parcel.Parcelize

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class PlayListCatListResponse(
    val sub: List<PlayListCat>,
    val categories: Map<Int, String>
) : BaseHttpResponse()

@Parcelize
@Entity(tableName = "my_playList_cat")
data class PlayListCat(
    @PrimaryKey
    val name: String,
    val resourceCount: Int = 0,
    val category: Int,
    val hot: Boolean = false,
    var isDisable: Boolean = false,
    //常驻的
    var isResident: Boolean = false
) : Parcelable
