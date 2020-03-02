package com.xw.lib_coremodel.model.repository.home

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.model.bean.Listing
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.viewmodel.home.PlayListDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListFragmentRepository(
    private val networkExecutor: Executor
) {
    fun postsOfPlayList(playListCat: PlayListCat, pageSize: Int): Listing<PlayList> {
        val sourceFactory = PlayListDataSourceFactory( networkExecutor, playListCat)

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

