package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.home.RecdDailyRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

class RecdDailyModelFactory constructor(private val recdDailyRepository: RecdDailyRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecdDailyViewModel(recdDailyRepository) as T
    }
}