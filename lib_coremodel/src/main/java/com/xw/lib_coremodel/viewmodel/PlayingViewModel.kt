package com.xw.lib_coremodel.viewmodel

import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.repository.PlayingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayingViewModel internal constructor(private val playingRepository: PlayingRepository) :
    BaseViewModel(playingRepository) {

    val likeList = MutableLiveData<LongArray>()
    val likeResult = MutableLiveData<Boolean>()
    fun getLikeList() {
        launch {
            val response = withContext(Dispatchers.IO) { playingRepository.getLikeIds() }
            executeResponse(response, { likeList.postValue(response.ids) }, {})
        }
    }

    /**
     * isLike:  true 即喜欢 , 若传 false, 则取消喜欢
     */
    fun likeMusic(id: String, isLike: Boolean) {
        launch {
            val response =
                withContext(Dispatchers.IO) { playingRepository.likeMusic(id, isLike) }
            executeResponse(response, { likeResult.postValue(response.isSuccess()) }, {})
        }
    }
}