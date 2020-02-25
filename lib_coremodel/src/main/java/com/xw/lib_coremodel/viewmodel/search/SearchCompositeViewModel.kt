package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.search.Composite
import com.xw.lib_coremodel.model.repository.search.SearchCompositeRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchCompositeViewModel(private val repository: SearchCompositeRepository) :
    BaseViewModel() {

    val composite = MutableLiveData<Composite>()
    fun getSearchComposite(keywords: String) {
        launch {
            val response = withContext(Dispatchers.IO) { repository.searchComposite(keywords) }
            executeResponse(response, { composite.postValue(response.result) }, {})
        }
    }

}