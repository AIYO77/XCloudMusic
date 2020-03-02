package com.xw.lib_coremodel.model.repository.video

import android.content.Context
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.Listing
import com.xw.lib_coremodel.model.bean.video.VideoItemInfo
import com.xw.lib_coremodel.model.bean.video.VideoTypeResponse
import com.xw.lib_coremodel.model.repository.BaseRepository
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoRepository(context: Context, private val networkExecutor: Executor) :
    BaseVideoRepository(context) {

    suspend fun getVideoType(): VideoTypeResponse {
        return apiCall { MusicRetrofitClient.service.getVideoType() }
    }

    fun postsOfVideoGroup(id: String, pageSize: Int): Listing<VideoItemInfo> {
        val sourceFactory = VideoDataSourceFactory(id, networkExecutor)
        val livePagedList = sourceFactory.toLiveData(
            config = Config(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSizeHint = pageSize * 2
            ), fetchExecutor = networkExecutor
        )
        val refreshState =
            Transformations.switchMap(sourceFactory.sourceLiveData) { it.initialLoad }
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


    companion object {
        @Volatile
        private var instance: VideoRepository? = null

        fun getInstance(context: Context, networkExecutor: Executor) =
            instance ?: synchronized(this) {
                instance ?: VideoRepository(context, networkExecutor).also { instance = it }
            }
    }
}