package com.xw.lib_coremodel.model.bean.video

import android.os.Parcelable
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.UserInfo
import com.xw.lib_coremodel.model.bean.home.SongInfo
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class VideoListResponse(val datas: List<VideoItemInfo>) : BaseHttpResponse()

@Parcelize
data class VideoItemInfo(
    val type: Int,
    val displayed: Boolean,
    var nothing: Boolean,
    val data: VideoItemData
) : Parcelable {
    fun change() {
        nothing = nothing.not()
    }
}

@Parcelize
data class VideoItemData(
    val vid: String,
    val durationms: Long,
    val praisedCount: Long,
    val playTime: Long,
    var praised: Boolean,
    var subscribed: Boolean,
    val coverUrl: String?,
    val height: Int,
    val width: Int,
    val title: String,
    val description: String,
    val commentCount: Long,
    val shareCount: Long,
    val creator: UserInfo,
    val urlInfo: VideoUrlInfo,
    val videoGroup: List<VideoGroup>,
    val relateSong: List<Song>
) : Parcelable {
    fun haveSong(): Boolean = relateSong.isNullOrEmpty().not()

    fun getSongPic(): String = if (haveSong()) {
        relateSong[0].al?.picUrl?:""
    } else ""
}

data class VideoUrlInfo(
    val id: String,
    val url: String,
    val size: Long,
    val validityTime: Double,
    val needPay: Boolean,
    val r: Int
) : Serializable

data class VideoGroup(val id: Long, val name: String, val alg: String) : Serializable