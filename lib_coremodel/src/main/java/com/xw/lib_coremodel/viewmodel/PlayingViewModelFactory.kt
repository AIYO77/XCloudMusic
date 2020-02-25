package com.xw.lib_coremodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.PlayingRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

class PlayingViewModelFactory(private val playingRepository: PlayingRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayingViewModel(playingRepository) as T
    }

}