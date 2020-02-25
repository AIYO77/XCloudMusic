package com.xw.lib_coremodel.model.bean.home

import android.os.Parcelable
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.UserInfo
import kotlinx.android.parcel.Parcelize

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Parcelize
data class PlayList(
    val subscribers: List<UserInfo>, //收藏歌单的人
    val subscribed: Boolean,  // 我是否收藏
    val creator: UserInfo, // 创建者
    val tracks: MutableList<Song>, //歌曲列表
    val trackIds: List<TrackId>, //全部歌曲id
    val coverImgUrl: String, // logo
    val trackCount: Int, // 一共多少首歌
    val subscribedCount: Long, // 订阅量
    val updateTime: Long, // 更新时间
    val createTime: Long,//创建时间
    val description: String?, //描述
    val id: String,
    val commentCount: Long, // 评论数量
    val playCount: Long,
    val shareCount: Long, // 分享数量
    val name: String,
    val tags: List<String>, // 标签
    val copywriter: String = ""
) : Parcelable