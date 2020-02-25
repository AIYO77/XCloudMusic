package com.xw.lib_coremodel.model.bean.dj

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class DjRadioInfo(
    val id: Long,
    val dj: DJInfo,
    val name: String,
    val picUrl: String,
    val desc: String,
    val subCount: Long,
    val programCount: Long,
    val createTime: Long,
    val categoryId: Int,
    val category: String,
    val radioFeeType: Int,
    val feeScope: Int,
    val buyed: Boolean,
    val finished: Boolean,
    val underShelf: Boolean,
    val purchaseCount: Long,
    val price: Int,
    val lastProgramCreateTime: Long,
    val lastProgramName: String,
    val picId: Long,
    val rcmdText: String,
    val shareCount: Long,
    val composeVideo: Boolean,
    val rcmdtext: String,
    val likedCount: Long,
    val commentCount: Long
) : Serializable

data class DJInfo(
    val defaultAvatar: Boolean,
    val province: Long,
    val authStatus: Int,
    val followed: Boolean,
    val avatarUrl: String,
    val accountStatus: Int,
    val gender: Int,
    val city: Long,
    val birthday: Long,
    val userId: Long,
    val userType: Int,
    val nickname: String,
    val signature: String,
    val description: String,
    val detailDescription: String,
    val avatarImgId: Long,
    val backgroundImgId: Long,
    val backgroundUrl: String,
    val authority: Int,
    val mutual: Boolean,
    val djStatus: Int,
    val vipType: Int,
    val anchor: Boolean
) : Serializable