package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.work.ListenableWorker
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.search.DefaultSearchData
import com.xw.lib_coremodel.model.bean.search.HotSearchData
import com.xw.lib_coremodel.model.bean.search.SuggestSearchData
import com.xw.lib_coremodel.model.repository.search.SearchRepository
import com.xw.lib_coremodel.utils.SEARCH_TYPE_FILENAME
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchViewModel internal constructor(private val repository: SearchRepository) :
    BaseViewModel(repository) {

    val defaultSearch = MutableLiveData<DefaultSearchData>()

    val suggestSearchList = MutableLiveData<List<SuggestSearchData>>()

    val hotSearchList = MutableLiveData<MutableList<HotSearchData>>()

    val searchHistory = repository.getHistory()

    val searchTypes = MutableLiveData<List<SearchType>>()

    fun getSearchTypes() {
        launch {
            try {
                CoreApplication.CONTEXT.assets.open(SEARCH_TYPE_FILENAME)
                    .use { inputStream ->
                        JsonReader(inputStream.reader()).use { jsonReader ->
                            val searchType = object : TypeToken<List<SearchType>>() {}.type
                            val list =
                                CoreApplication.GSON.fromJson<List<SearchType>>(
                                    jsonReader,
                                    searchType
                                )
                            searchTypes.postValue(list)
                        }
                    }
            } catch (e: Exception) {
                Logger.e(e.localizedMessage)
            }
        }
    }

    fun getDefaultSearch() {
        launch {
            val defaultSearchData = withContext(Dispatchers.IO) { repository.getDefaultSearch() }
            executeResponse(
                defaultSearchData,
                { defaultSearch.postValue(defaultSearchData.data) },
                {})
        }
    }


    fun getSuggestSearch(keywords: String) {
        launch {
            val response = withContext(Dispatchers.IO) { repository.getSuggestSearch(keywords) }
            executeResponse(response, {
                if (response.result.allMatch.isNullOrEmpty()) {
                    response.result.allMatch = mutableListOf<SuggestSearchData>().apply {
                        add(
                            SuggestSearchData(keywords, "搜索\"$keywords\"", -1)
                        )
                    }
                } else {
                    response.result.allMatch.apply {
                        add(0, SuggestSearchData(keywords, "搜索\"$keywords\"", -1))
                    }
                }
                suggestSearchList.postValue(response.result.allMatch)
            }, {})
        }
    }

    fun getHotSearchList() {
        launch {
            val response = withContext(Dispatchers.IO) { repository.getHotSearchList() }
            executeResponse(response, { hotSearchList.postValue(response.data) }, {})
        }
    }

    fun saveSearchHistory(keywords: String) {
        launch {
            withContext(Dispatchers.IO) { repository.saveHistory(keywords) }
        }
    }

    fun clearHistory() {
        launch {
            withContext(Dispatchers.IO) { repository.clearHistury() }
        }
    }

}