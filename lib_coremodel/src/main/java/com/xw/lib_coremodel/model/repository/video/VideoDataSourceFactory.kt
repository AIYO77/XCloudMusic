package com.xw.lib_coremodel.model.repository.video

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xw.lib_coremodel.model.bean.video.VideoItemInfo
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoDataSourceFactory(
    private val id: String,
    private val retryExecutor: Executor?
) : DataSource.Factory<String, VideoItemInfo>() {

    val sourceLiveData = MutableLiveData<ItemKeyedVideoItemDataSource>()

    override fun create(): DataSource<String, VideoItemInfo> {
        val source = ItemKeyedVideoItemDataSource(id, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}