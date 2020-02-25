package com.xw.lib_coremodel.model.repository.home

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.NewSongData
import com.xw.lib_coremodel.model.bean.home.AlbumListData
import com.xw.lib_coremodel.model.bean.home.Banners
import com.xw.lib_coremodel.model.bean.home.RecommendPlayList
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HomeRepository(context: Context) : BaseRepository(context) {
    suspend fun getBanners(type: String): Banners {
        return apiCall { MusicRetrofitClient.service.getBanner(type) }
    }

    suspend fun getHomeRecommendPlayList(): RecommendPlayList {
        return apiCall { MusicRetrofitClient.service.getHomeRecommendPlayList(6) }
    }
    suspend fun getHomeRecommendPlayListLogin(): RecommendPlayList {
        return apiCall { MusicRetrofitClient.service.getRecommendPlayListNeedLogin() }
    }

    suspend fun getHomeRecommendAlbum(offset: Int, limit: Int): AlbumListData {
        return apiCall { MusicRetrofitClient.service.getHomeRecommendAlbum(offset, limit) }
    }

    suspend fun getNewSongRecommend(): NewSongData {
        return apiCall { MusicRetrofitClient.service.getRecommendNewSong() }
    }

    companion object {

        @Volatile
        private var instance: HomeRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(context).also { instance = it }
            }

    }
}