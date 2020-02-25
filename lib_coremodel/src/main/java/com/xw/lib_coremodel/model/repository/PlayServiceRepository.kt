package com.xw.lib_coremodel.model.repository

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.PlayUrlData
import com.xw.lib_coremodel.model.bean.SongLrc

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayServiceRepository(context: Context) : BaseRepository(context) {

    suspend fun getPlayUrl(ids: String): PlayUrlData {
        return apiCall { MusicRetrofitClient.service.getPlayUrl(ids) }
    }

    suspend fun getLrc(id: String): SongLrc {
        return apiCall { MusicRetrofitClient.service.getLrc(id) }
    }
}