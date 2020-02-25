package com.xw.lib_coremodel.model.repository.home

import android.content.Context
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.PlayListCatListResponse
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListTagsRepository(context: Context) : BaseRepository(context) {

    suspend fun getAllTags(): PlayListCatListResponse {
        return apiCall { MusicRetrofitClient.service.getPlaylistCatList() }
    }

    private val catDao by lazy {
        AppDatabase.getInstance(context).myPlayListCatDao()
    }

    fun saveMyTag(tags: List<PlayListCat>) {
        catDao.deleteAll()
        catDao.addAll(tags)
    }

    companion object {
        @Volatile
        private var instance: PlayListTagsRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PlayListTagsRepository(context).also { instance = it }
            }

    }
}