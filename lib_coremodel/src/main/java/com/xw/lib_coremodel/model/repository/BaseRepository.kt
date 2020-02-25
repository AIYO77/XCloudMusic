package com.xw.lib_coremodel.model.repository

import android.content.Context
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.model.bean.BaseHttpResponse


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
open class BaseRepository(val context: Context) {

    protected val loginUserDao by lazy {
        AppDatabase.getInstance(context).loginUserDao()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> apiCall(call: suspend () -> BaseHttpResponse): T {
        return call.invoke() as T
    }

    fun getLoginUser() = loginUserDao.getLoginUser()

}