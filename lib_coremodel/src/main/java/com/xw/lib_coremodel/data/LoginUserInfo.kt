package com.xw.lib_coremodel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Entity(tableName = "login_user")
data class LoginUserInfo(
    @PrimaryKey
    val userId: String,
    var nickname: String,
    val token: String,
    val city: Int,
    val avatarUrl: String,
    val backgroundUrl: String,
    val signature: String,
    val followeds: Long,
    val follows: Long,
    val playlistCount: Int,
    val playlistBeSubscribedCount: Int
)