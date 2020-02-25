package com.xmusic.module_login.ui

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.login.LoginViewModel

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class LoginBaseFragment<VB : ViewDataBinding> : BaseModelFragment<VB, LoginViewModel>() {
    override val viewModel: LoginViewModel by viewModels {
        InjectorUtils.provideLoginViewModelFactory(requireActivity())
    }

}