package com.xw.lib_coremodel.model.bean.login

import com.xw.lib_coremodel.model.bean.BaseHttpResponse

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
data class PhoneExist(val exist: Int, val nickname: String) : BaseHttpResponse()