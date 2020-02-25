package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.home.SongInfo
import com.xw.lib_coremodel.model.repository.home.RecdDailyRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RecdDailyViewModel internal constructor(private val recdDailyRepository: RecdDailyRepository) :
    BaseViewModel(recdDailyRepository) {

    val recommend = MutableLiveData<List<SongInfo>>()

    fun getRecdDaily() {
        launch {
            val dailyData = withContext(Dispatchers.IO) { recdDailyRepository.getRecdDaily() }
            executeResponse(dailyData, { recommend.postValue(dailyData.recommend) }, {})
        }
    }
}