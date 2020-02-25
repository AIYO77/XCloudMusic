package com.xmusic.module_login.ui


import androidx.navigation.fragment.navArgs

import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentRegisterBinding
import com.xw.lib_common.ext.toast
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * register
 */
class RegisterFragment : LoginBaseFragment<FragmentRegisterBinding>() {

    private val args:RegisterFragmentArgs by navArgs()

    override fun initView() {
        nextBtn.setOnClickListener {
            val nickName = nikeNameEt.text.toString()
            if (nickName.isEmpty()){
                toast("请输入昵称!")
                return@setOnClickListener
            }
            viewModel.registerOrChangePwd(args.phone,args.password,args.code,nickName)
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        super.startObserve()

    }

    override val layoutId: Int
        get() = R.layout.fragment_register


}
