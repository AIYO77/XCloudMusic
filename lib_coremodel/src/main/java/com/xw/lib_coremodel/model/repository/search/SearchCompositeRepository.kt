package com.xw.lib_coremodel.model.repository.search

import android.content.Context
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.search.SearchCompositeResponse
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchCompositeRepository(context: Context) : BaseRepository(context) {

    suspend fun searchComposite(keywords: String): SearchCompositeResponse {
        return apiCall { MusicRetrofitClient.service.searchComposite(keywords) }
    }

    companion object {
        @Volatile
        private var instance: SearchCompositeRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SearchCompositeRepository(context).also { instance = it }
            }
    }

}