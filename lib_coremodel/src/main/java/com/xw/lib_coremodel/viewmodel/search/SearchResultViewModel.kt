package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.repository.search.SearchResultRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultViewModel<T>(private val repository: SearchResultRepository) :
    BaseViewModel() {

    private val searchType = MutableLiveData<SearchType>()

    private val repoResult = Transformations.map(searchType) {
        repository.postsOfSearchResult<T>(it, 30)
    }

    val posts = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }

    fun showPlayList(type: SearchType): Boolean {
        if (searchType.value == type) {
            return false
        }
        searchType.value = type
        return true
    }

    fun getKeywords(): String {
        return searchType.value?.keywords ?: ""
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

}