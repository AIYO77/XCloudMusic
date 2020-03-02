package com.xw.lib_coremodel.model.repository

import android.content.Context

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MainRepository(context: Context) : BaseRepository(context) {


    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: MainRepository(context).also { instance = it }
        }
    }
}