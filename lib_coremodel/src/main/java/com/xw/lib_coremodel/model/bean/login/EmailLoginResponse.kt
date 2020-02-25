package com.xw.lib_coremodel.model.bean.login

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import java.io.Serializable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class EmailLoginResponse(val token: String, val account: EmailAccount) : BaseHttpResponse()

data class EmailAccount(val id: Long, val userName: String) : Serializable