package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.repository.search.SearchResultDataSource
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultDataSourceFactory<T>(private val retryExecutor: Executor,
                                    private val searchType: SearchType) :
    DataSource.Factory<String, T>() {
    val sourceLiveData = MutableLiveData<SearchResultDataSource<T>>()
    override fun create(): DataSource<String, T> {
        val source = SearchResultDataSource<T>(retryExecutor,searchType)
        sourceLiveData.postValue(source)
        return source
    }
}