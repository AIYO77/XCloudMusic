package com.xw.lib_coremodel.model.bean

import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 *
 * Desc:
 */
open class BaseHttpResponse : Serializable {
    var code: Int = 0
    val message: String = ""
    val msg: String = ""

    fun isSuccess(): Boolean {
        return code in 200..299
    }

    fun getResponseMsg(): String {
        return if (message.isNotEmpty()) {
            message
        } else {
            msg
        }
    }
}
