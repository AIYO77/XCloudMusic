package com.xw.lib_coremodel.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.search.SearchCompositeRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchCompositeFactory(private val repository: SearchCompositeRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchCompositeViewModel(repository) as T
    }
}