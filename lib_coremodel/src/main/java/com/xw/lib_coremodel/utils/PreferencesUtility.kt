package com.xw.lib_coremodel.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PreferencesUtility(context: Context) {

    private val mPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val TOKEN = "TOKEN"
        private const val UID = "userId"

        @Volatile
        private var instance: PreferencesUtility? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: PreferencesUtility(context).also { instance = it }
        }
    }

    fun setPlayLink(id: Long, link: String?) {
        if (link.isNullOrEmpty()) return
        mPreferences.edit().apply {
            putString(id.toString() + "", link)
            apply()
        }
    }

    fun getPlayLink(id: Long): String? {
        return mPreferences.getString(id.toString() + "", null)
    }

    fun getToken(): String {
        return mPreferences.getString(TOKEN, "") ?: ""
    }

    fun setToken(token: String) {
        mPreferences.edit().apply {
            putString(TOKEN, token)
            apply()
        }
    }

    fun setUserId(userId: String) {
        mPreferences.edit().apply {
            putString(UID, userId)
            apply()
        }
    }

    fun getUserId(): String {
        return mPreferences.getString(UID, "") ?: ""
    }

    fun clearToken() {
        mPreferences.edit().remove(TOKEN).apply()
    }

    fun getPlayListNeedNewRequest(id: String): String {
        return mPreferences.getString(id, "") ?: ""
    }

    fun setPlayListNeedNewRequest(id: String, time: String) {
        mPreferences.edit().apply {
            putString(id, time)
            apply()
        }
    }

    fun clearKey(key: String) {
        mPreferences.edit().remove(key).apply()
    }


}