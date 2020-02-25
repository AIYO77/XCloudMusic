package com.xw.lib_coremodel.model.repository.home

import android.content.Context
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.PlayListData
import com.xw.lib_coremodel.model.bean.home.PlayListHotResponse
import com.xw.lib_coremodel.model.bean.home.SongDetailResponse
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListRepository(context: Context) : BaseRepository(context) {

    suspend fun getPlayList(id: String, time: String): PlayListData {
        return apiCall { MusicRetrofitClient.service.getPlayList(id, time) }
    }

    suspend fun subscribePlayList(id: String, t: Int): BaseHttpResponse {
        return apiCall { MusicRetrofitClient.service.subscribePlayList(id, t) }
    }

    suspend fun getSongDetail(ids: String): SongDetailResponse {
        return apiCall { MusicRetrofitClient.service.getSongDetail(ids) }
    }

    suspend fun getHotCatList(): PlayListHotResponse {
        return apiCall { MusicRetrofitClient.service.getPlaylistHot() }
    }

    val myPLCatDao by lazy {
        AppDatabase.getInstance(context).myPlayListCatDao()
    }

    fun savePlayListCat(cats: List<PlayListCat>) {
        myPLCatDao.addAll(cats)
    }

    companion object {
        @Volatile
        private var instance: PlayListRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PlayListRepository(context).also { instance = it }
            }
    }
}