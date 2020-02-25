package com.xw.lib_coremodel.model.bean.login

import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.UserInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class LoginResponse(val loginType: Int, val token: String, val profile: UserInfo) :
    BaseHttpResponse()