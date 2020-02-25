
package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.home.PlayListTagsRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListTagsViewModelFactory(private val repository: PlayListTagsRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayListTagsViewModel(repository) as T
    }
}