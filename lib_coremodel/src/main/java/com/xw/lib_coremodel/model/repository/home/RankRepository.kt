package com.xw.lib_coremodel.model.repository.home

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.home.TopList
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RankRepository (context: Context): BaseRepository(context) {

    suspend fun getTopList(): TopList {
        return apiCall { MusicRetrofitClient.service.getTopList() }
    }

    companion object {

        @Volatile
        private var instance: RankRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: RankRepository(context).also { instance = it }
            }

    }
}