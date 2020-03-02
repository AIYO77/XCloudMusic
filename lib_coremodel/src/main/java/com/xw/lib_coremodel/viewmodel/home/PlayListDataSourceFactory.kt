package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.repository.home.PlayListDataSource
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListDataSourceFactory(private val retryExecutor: Executor,
                                private val playListCat: PlayListCat) :
    DataSource.Factory<String, PlayList>() {
    val sourceLiveData = MutableLiveData<PlayListDataSource>()
    override fun create(): DataSource<String, PlayList> {
        val source = PlayListDataSource(retryExecutor,playListCat)
        sourceLiveData.postValue(source)
        return source
    }
}