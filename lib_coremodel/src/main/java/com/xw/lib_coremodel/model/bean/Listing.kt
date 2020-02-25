package com.xw.lib_coremodel.model.bean

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>,
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)