package com.xmusic.module_login.ui


import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentEmailLoginBinding
import com.xw.lib_common.ext.toast
import kotlinx.android.synthetic.main.fragment_email_login.*

/**
 * 邮箱登录
 */
class EmailLoginFragment : LoginBaseFragment<FragmentEmailLoginBinding>() {


    override fun initView() {
        nextBtn.setOnClickListener {
            val email = inputEmail.text.toString()
            val pwd = inputPwd.text.toString()
            if (email.isEmpty()) {
                toast(getString(R.string.data_please_input_email))
                return@setOnClickListener
            } else if (pwd.isEmpty()) {
                toast(getString(R.string.data_please_input_pwd))
                return@setOnClickListener
            }
            showLoading()
            viewModel.emailLogin(email,pwd)
        }
    }

    override fun startObserve() {
        super.startObserve()

    }

    override fun initData() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_email_login


}
