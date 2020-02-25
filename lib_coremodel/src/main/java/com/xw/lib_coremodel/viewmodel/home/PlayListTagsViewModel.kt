package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.PlayListCatListResponse
import com.xw.lib_coremodel.model.repository.home.PlayListTagsRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListTagsViewModel internal constructor(private val repository: PlayListTagsRepository) :
    BaseViewModel(repository) {

    val allTags = MutableLiveData<PlayListCatListResponse>()

    fun getAllTags() {
        launch {
            val listResponse = withContext(Dispatchers.IO) { repository.getAllTags() }
            executeResponse(listResponse, { allTags.postValue(listResponse) }, {})
        }
    }

    fun saveMyTag(tags: List<PlayListCat>) {
        launch {
            withContext(Dispatchers.IO) { repository.saveMyTag(tags) }
        }
    }

}