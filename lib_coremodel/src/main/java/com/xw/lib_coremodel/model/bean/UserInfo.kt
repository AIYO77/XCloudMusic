package com.xw.lib_coremodel.model.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Parcelize
data class UserInfo(
    val followed: Boolean,
    val avatarUrl: String,
    val gender: Int,
    val province: Int,
    val city: Int,
    val birthday: String,
    val userId: String,
    val userType: Int,
    val nickname: String,
    val signature: String,
    val description: String,
    val detailDescription: String,
    val backgroundUrl: String,
    val followeds: Long,
    val follows: Long,
    val playlistCount: Int,
    val playlistBeSubscribedCount: Int
) : Parcelable