package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.search.SearchResultRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultFragmentViewModelFactory<M>(private val repository: SearchResultRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultViewModel<M>(repository) as T
    }
}