package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.home.TopListItem
import com.xw.lib_coremodel.model.repository.home.RankRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RankViewModel internal constructor(private val rankRepository: RankRepository) :
    BaseViewModel(rankRepository) {


    val mTopList: MutableLiveData<List<TopListItem>> = MutableLiveData()

    fun getTopList() {
        launch {
            val data = withContext(Dispatchers.IO) { rankRepository.getTopList() }
            executeResponse(data, { mTopList.postValue(data.list) }, {})
        }
    }
}