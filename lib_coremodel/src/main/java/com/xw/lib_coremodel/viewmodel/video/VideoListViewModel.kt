package com.xw.lib_coremodel.viewmodel.video

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.video.VideoType
import com.xw.lib_coremodel.model.repository.video.VideoRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoListViewModel(private val videoRepository: VideoRepository) : BaseVideoViewModel(videoRepository) {
    val videoTypes = MutableLiveData<List<VideoType>>()

    fun getVideoType() {
        launch {
            val typeResponse = withContext(Dispatchers.IO) { videoRepository.getVideoType() }
            executeResponse(typeResponse, {
                val list = typeResponse.data.toMutableList()
                    .subList(0, if (typeResponse.data.size > 15) 15 else typeResponse.data.size)
                videoTypes.postValue(list)
            }, {})
        }
    }

    private val videoGroupId = MutableLiveData<String>()
    private val repoResult = Transformations.map(videoGroupId) {
        videoRepository.postsOfVideoGroup(it, 8)
    }
    val posts = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showVideoGroup(id: String): Boolean {
        if (videoGroupId.value == id) {
            return false
        }
        videoGroupId.value = id
        return true
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }
}