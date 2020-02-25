package com.xw.lib_coremodel.model.repository.login

import android.content.Context
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.LoginUserInfo
import com.xw.lib_coremodel.data.LoginUserInfoDao
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.login.EmailLoginResponse
import com.xw.lib_coremodel.model.bean.login.LoginResponse
import com.xw.lib_coremodel.model.bean.login.PhoneExist
import com.xw.lib_coremodel.model.repository.BaseRepository
import com.xw.lib_coremodel.utils.PreferencesUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LoginRepository private constructor(context: Context) :
    BaseRepository(context) {

    suspend fun checkPhone(phone: String): PhoneExist {
        return apiCall { MusicRetrofitClient.service.checkPhone(phone) }
    }

    suspend fun sendCaptcha(phone: String): BaseHttpResponse {
        return apiCall { MusicRetrofitClient.service.sendCaptcha(phone) }
    }

    suspend fun verifyCaptcha(phone: String, captcha: String): BaseHttpResponse {
        return apiCall { MusicRetrofitClient.service.verifyCaptcha(phone, captcha) }
    }

    suspend fun phoneLogin(phone: String, password: String): LoginResponse {
        return apiCall { MusicRetrofitClient.service.phoneLogin(phone, password) }
    }

    suspend fun emailLogin(email: String, password: String): EmailLoginResponse {
        return apiCall { MusicRetrofitClient.service.emailLogin(email, password) }
    }

    suspend fun registerWithPhone(
        phone: String,
        password: String,
        captcha: String,
        nickName: String?
    ): LoginResponse {
        return apiCall {
            MusicRetrofitClient.service.registerWithPhone(
                phone,
                password,
                captcha,
                nickName
            )
        }
    }

    suspend fun saveLoginUser(loginResponse: LoginResponse) {
        withContext(Dispatchers.IO) {
            val userInfo = loginResponse.profile
            context.apply { PreferencesUtility.getInstance(this).setUserId(userInfo.userId) }
            val loginUserInfo = LoginUserInfo(
                userId = userInfo.userId,
                nickname = userInfo.nickname,
                token = loginResponse.token,
                city = userInfo.city,
                avatarUrl = userInfo.avatarUrl,
                backgroundUrl = userInfo.backgroundUrl,
                signature = userInfo.signature,
                followeds = userInfo.followeds,
                follows = userInfo.follows,
                playlistCount = userInfo.playlistCount,
                playlistBeSubscribedCount = userInfo.playlistBeSubscribedCount
            )
            loginUserDao.deleteUser()
            loginUserDao.insertUser(loginUserInfo)
        }
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: LoginRepository(context).also { instance = it }
        }
    }
}