package com.xw.lib_coremodel.model.bean.search

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.UserInfo
import com.xw.lib_coremodel.model.bean.dj.DjRadioInfo
import com.xw.lib_coremodel.model.bean.home.*
import com.xw.lib_coremodel.model.bean.video.SearchVideoItemInfo
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class SearchCompositeResponse(val result: Composite) : BaseHttpResponse()

data class Composite(
    val song: CompositeSong,
    val playList: CompositePlayList,
    val artist: CompositeArtist,
    val album: CompositeAlbum,
    val video: CompositeVideo,
    val sim_query: CompositeSimQuery,
    val djRadio: CompositeDjRadio,
    val user: CompositeUser
) : Serializable

data class CompositeSong(val songs: List<SongInfo>) : CompositeCommon()

data class CompositePlayList(val playLists: List<PlayListSimpleInfo>) : CompositeCommon()

data class CompositeArtist(val artists: List<ArtistInfo>) : CompositeCommon()

data class CompositeAlbum(val albums: List<AlbumItemInfo>) : CompositeCommon()

data class CompositeVideo(val videos: List<SearchVideoItemInfo>) : CompositeCommon()

data class CompositeSimQuery(val sim_querys: List<SuggestSearchData>) : CompositeCommon()

data class CompositeDjRadio(val djRadios: List<DjRadioInfo>) : CompositeCommon()

data class CompositeUser(val users: List<UserInfo>) : CompositeCommon()

open class CompositeCommon :
    Serializable {
    val moreText: String = ""
    val more: Boolean = false
    val resourceIds: List<Long>? = null
}

