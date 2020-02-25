package com.xw.lib_coremodel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.model.repository.BaseRepository
import kotlinx.coroutines.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
open class BaseViewModel(baseRepository: BaseRepository? = null) : ViewModel() {

    val mException: MutableLiveData<Throwable> = MutableLiveData()

    val loginUser = baseRepository?.getLoginUser()

    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch { block() }
    }

    fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        launchOnUI {
            tryCatch(tryBlock)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                Logger.e(e.toString())
                if (e !is CancellationException) {
                    mException.value = e
                }
            }
        }
    }
}