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
data class SongLrc(val lrc: Lrc?, val tlyric: TlyRic?) : BaseHttpResponse()

data class Lrc(val version: Int, val lyric: String) : Serializable

data class TlyRic(val version: Int, val lyric: String) : Serializable

@Parcelize
data class LrcAdnTlyRic(val id: Long = -1, val lrc: String? = "", val lyric: String? = "") :
    Parcelable