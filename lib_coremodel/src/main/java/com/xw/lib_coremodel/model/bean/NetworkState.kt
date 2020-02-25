package com.xw.lib_coremodel.model.bean

import com.xw.lib_coremodel.ext.goLogin

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    NEEDLOGIN
}

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
        fun needLogin(msg: String?): NetworkState {
            goLogin()
            return NetworkState(Status.NEEDLOGIN, msg)
        }
    }
}