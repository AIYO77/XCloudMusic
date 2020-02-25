package com.xw.lib_coremodel.model.repository.search

import android.content.Context
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.Listing
import com.xw.lib_coremodel.model.repository.BaseRepository
import com.xw.lib_coremodel.viewmodel.search.SearchResultDataSourceFactory
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultRepository(
    context: Context,
    private val networkExecutor: Executor
) : BaseRepository(context) {

    fun <T> postsOfSearchResult(searchType: SearchType, pageSize: Int): Listing<T> {
        val sourceFactory = SearchResultDataSourceFactory<T>(networkExecutor, searchType)

        val livePagedList = sourceFactory.toLiveData(
            config = Config(
                pageSize = pageSize,
                initialLoadSizeHint = pageSize,
                enablePlaceholders = false,
                prefetchDistance = pageSize
            ),
            fetchExecutor = networkExecutor
        )
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState
        )
    }
}