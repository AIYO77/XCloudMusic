package com.xw.lib_coremodel.model.repository

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.api.MusicService
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.LikeListResponse

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayingRepository(context: Context) : BaseRepository(context) {

    suspend fun getLikeIds(): LikeListResponse {
        return apiCall { MusicRetrofitClient.service.getLikeIds() }
    }

    suspend fun likeMusic(id: String, isLike: Boolean): BaseHttpResponse {
        return apiCall { MusicRetrofitClient.service.likeMusic(id, isLike) }
    }

    companion object {

        @Volatile
        private var instance: PlayingRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PlayingRepository(context).also { instance = it }
            }
    }
}