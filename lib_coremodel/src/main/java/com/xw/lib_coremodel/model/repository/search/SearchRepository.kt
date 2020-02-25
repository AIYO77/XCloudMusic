package com.xw.lib_coremodel.model.repository.search

import android.content.Context
import androidx.lifecycle.LiveData
import com.xw.lib_coremodel.data.*
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.search.DefaultSearchResponse
import com.xw.lib_coremodel.model.bean.search.HotSearchResponse
import com.xw.lib_coremodel.model.bean.search.SuggestSearchResponse
import com.xw.lib_coremodel.model.repository.BaseRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchRepository(context: Context) : BaseRepository(context) {

    suspend fun getDefaultSearch(): DefaultSearchResponse {
        return apiCall { MusicRetrofitClient.service.getDefaultSearch() }
    }

    suspend fun search(keywords: String, type: Int, limit: Int, offset: Int) {
        return apiCall {
            MusicRetrofitClient.service.search(
                keywords = keywords,
                type = type, limit = limit, offset = offset
            )
        }
    }

    suspend fun getSuggestSearch(keywords: String): SuggestSearchResponse {
        return apiCall { MusicRetrofitClient.service.getSuggestSearch(keywords = keywords) }
    }

    suspend fun getHotSearchList(): HotSearchResponse {
        return apiCall { MusicRetrofitClient.service.getHotSearchDetail() }
    }

    fun getHistory(): LiveData<List<SearchHistory>> {
        return historyDao.getAllHistory()
    }

    fun saveHistory(keywords: String) {
        historyDao.insertHistory(SearchHistory(keywords = keywords))
    }

    fun clearHistury() {
        historyDao.deleteAllHistory()
    }

    private val historyDao: SearchHistoryDao by lazy {
        AppDatabase.getInstance(context).searchHistoryDao()
    }

    companion object {
        @Volatile
        private var instance: SearchRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SearchRepository(context).also { instance = it }
            }
    }
}