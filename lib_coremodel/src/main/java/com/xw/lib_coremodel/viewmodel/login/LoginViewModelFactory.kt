package com.xw.lib_coremodel.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xw.lib_coremodel.model.repository.login.LoginRepository

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LoginViewModelFactory(private val loginRepository: LoginRepository):
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository) as T
    }
}