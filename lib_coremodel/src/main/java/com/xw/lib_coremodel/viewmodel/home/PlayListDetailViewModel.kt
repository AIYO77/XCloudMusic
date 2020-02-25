package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.home.PlayListData
import com.xw.lib_coremodel.model.bean.home.SongDetailResponse
import com.xw.lib_coremodel.model.bean.login.ResponseResult
import com.xw.lib_coremodel.model.repository.home.PlayListRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListDetailViewModel internal constructor(private val repository: PlayListRepository) :
    BaseViewModel(repository) {

    val playList: MutableLiveData<PlayListData> = MutableLiveData()
    val songDetails: MutableLiveData<SongDetailResponse> = MutableLiveData()
    val collectResult = MutableLiveData<ResponseResult>()

    fun getPlayList(id: String, time: String = "") {
        launch {
            val data = withContext(Dispatchers.IO) { repository.getPlayList(id, time) }
            executeResponse(data, { playList.postValue(data) }, {})
        }
    }

    fun getSongDetail(ids: String) {
        launch {
            val songDetailResponse = withContext(Dispatchers.IO) { repository.getSongDetail(ids) }
            executeResponse(songDetailResponse, { songDetails.postValue(songDetailResponse) }, {})
        }
    }

    /**
     * id 歌单ID
     * t 类型,1:收藏,2:取消收藏
     */
    fun subscribePlayList(id: String, t: Int) {
        launch {
            val response =
                withContext(Dispatchers.IO) { repository.subscribePlayList(id, t) }
            executeResponse(
                response,
                { collectResult.postValue(ResponseResult(true, "$t")) },
                { collectResult.postValue(ResponseResult(false, response.getResponseMsg())) })
        }
    }

}