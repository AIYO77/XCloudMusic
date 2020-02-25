package com.xw.lib_coremodel.model.repository.home

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.home.RecdDailyData
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RecdDailyRepository(context: Context) : BaseRepository(context) {

    suspend fun getRecdDaily(): RecdDailyData {
        return apiCall { MusicRetrofitClient.service.getRecdDaily() }
    }

    companion object {
        @Volatile
        private var instance: RecdDailyRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: RecdDailyRepository(context).also { instance = it }
            }

    }
}