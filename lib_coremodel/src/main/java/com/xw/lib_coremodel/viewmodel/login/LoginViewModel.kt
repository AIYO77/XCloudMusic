package com.xw.lib_coremodel.viewmodel.login

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.bean.login.ResponseResult
import com.xw.lib_coremodel.model.repository.login.LoginRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LoginViewModel internal constructor(private val loginRepository: LoginRepository) :
    BaseViewModel() {
    val checkPhoneResult: MutableLiveData<ResponseResult> = MutableLiveData()
    val sendCaptchaResult: MutableLiveData<ResponseResult> = MutableLiveData()
    val verifyCaptchaResult: MutableLiveData<ResponseResult> = MutableLiveData()
    val phoneLoginResult: MutableLiveData<ResponseResult> = MutableLiveData()
    val registerResult: MutableLiveData<ResponseResult> = MutableLiveData()

    fun checkPhone(phone: String) {
        launch {
            val phoneExist = withContext(Dispatchers.IO) { loginRepository.checkPhone(phone) }
            executeResponse(
                phoneExist,
                {
                    if ((phoneExist.exist == 1)) {
                        checkPhoneResult.postValue(
                            ResponseResult(
                                result = true,
                                msg = phoneExist.nickname
                            )
                        )
                    } else {
                        sendCaptcha(phone)
                    }
                },
                { checkPhoneResult.postValue(ResponseResult(msg = phoneExist.message)) })
        }
    }

    fun sendCaptcha(phone: String) {
        launch {
            val send = withContext(Dispatchers.IO) { loginRepository.sendCaptcha(phone) }
            executeResponse(send, { sendCaptchaResult.postValue(ResponseResult(result = true)) }, {
                sendCaptchaResult.postValue(
                    ResponseResult(msg = send.message)
                )
            })
        }
    }

    fun verifyCode(phone: String, captcha: String) {
        launch {
            val response =
                withContext(Dispatchers.IO) { loginRepository.verifyCaptcha(phone, captcha) }
            executeResponse(
                response,
                { verifyCaptchaResult.postValue(ResponseResult(result = true)) },
                {
                    verifyCaptchaResult.postValue(ResponseResult(msg = response.message))
                })

        }
    }

    fun phoneLogin(phone: String, password: String) {
        launch {
            val loginResponse =
                withContext(Dispatchers.IO) { loginRepository.phoneLogin(phone, password) }
            executeResponse(loginResponse, {
                if (loginResponse.loginType == 1) {
                    loginRepository.saveLoginUser(loginResponse)
                    phoneLoginResult.postValue(ResponseResult(result = true))
                } else {
                    phoneLoginResult.postValue(ResponseResult(result = false))
                }
            }, {
                phoneLoginResult.postValue(ResponseResult(msg = loginResponse.message))
            })
        }
    }

    fun emailLogin(email: String, password: String) {
        launch {
            val response =
                withContext(Dispatchers.IO) { loginRepository.emailLogin(email, password) }
            executeResponse(response, {

            }, {

            })
        }
    }

    fun registerOrChangePwd(phone: String, password: String, captcha: String, nickName: String? = null) {
        launch {
            val response = withContext(Dispatchers.IO) {
                loginRepository.registerWithPhone(
                    phone,
                    password,
                    captcha,
                    nickName
                )
            }
            executeResponse(response, {
                if (response.loginType == 1) {
                    loginRepository.saveLoginUser(response)
                    registerResult.postValue(ResponseResult(result = true))
                } else {
                    registerResult.postValue(ResponseResult(result = false))
                }
            }, {
                registerResult.postValue(ResponseResult(msg = response.message))
            })
        }
    }

}