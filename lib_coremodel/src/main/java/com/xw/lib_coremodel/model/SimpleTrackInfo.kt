package com.xw.lib_coremodel.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Parcelize
data class SimpleTrackInfo(
    val id: Long = -1,
    val name: String = "",
    val ar: String = "",
    val logo: String = ""
) : Parcelable