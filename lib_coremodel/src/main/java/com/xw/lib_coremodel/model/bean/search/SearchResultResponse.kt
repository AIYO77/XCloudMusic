package com.xw.lib_coremodel.model.bean.search

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.UserInfo
import com.xw.lib_coremodel.model.bean.dj.DjRadioInfo
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo
import com.xw.lib_coremodel.model.bean.home.ArtistInfo
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.video.SearchVideoItemInfo
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class SearchSongsResultResponse(val result: SongsResult) : BaseHttpResponse()

data class SearchAlbumsResultResponse(val result: AlbumsResult) : BaseHttpResponse()
data class SearchSingerResultResponse(val result: SingerResult) : BaseHttpResponse()
data class SearchPlayListResultResponse(val result: PlayListResult) : BaseHttpResponse()
data class SearchUsersResultResponse(val result: UsersResult) : BaseHttpResponse()
data class SearchDjResultResponse(val result: DJsResult) : BaseHttpResponse()
data class SearchVideosResultResponse(val result: VideosResult) : BaseHttpResponse()

data class SongsResult(val songs: List<Song>, val songCount: Int) : Serializable
data class AlbumsResult(val albums: List<AlbumItemInfo>, val albumCount: Int) : Serializable
data class SingerResult(val artists: List<ArtistInfo>, val artistCount: Int) : Serializable
data class PlayListResult(val playlists: List<PlayList>, val playlistCount: Int) : Serializable
data class UsersResult(val userprofiles: List<UserInfo>, val userprofileCount: Int) : Serializable
data class DJsResult(val djRadios: List<DjRadioInfo>, val djRadiosCount: Int) : Serializable
data class VideosResult(val videos: List<SearchVideoItemInfo>, val videoCount: Int) : Serializable
