package com.xmusic.module_login.ui


import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentEnterPwdBinding
import com.xw.lib_common.ext.no
import com.xw.lib_common.ext.toast
import com.xw.lib_common.ext.yes
import kotlinx.android.synthetic.main.fragment_enter_pwd.*

/**
 * 输入密码
 */
class EnterPwdFragment : LoginBaseFragment<FragmentEnterPwdBinding>() {

    private val args: EnterPwdFragmentArgs by navArgs()

    override fun initView() {
        nextBtn.setOnClickListener {
            if (pwdEt.text.toString().isEmpty()) {
                toast(getString(R.string.data_please_input_pwd))
                return@setOnClickListener
            }
            showLoading()
            viewModel.phoneLogin(args.phone, pwdEt.text.toString())
        }

        forgetPwdTv.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.enter_pwd_fragment)
                findNavController().navigate(
                    EnterPwdFragmentDirections.actionEnterPwdFragmentToForgetPwdFragment(
                        args.phone, args.nikeName
                    )
                )
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.phoneLoginResult.observe(this, Observer {
            cancelLoading()
            it.result.yes {
                requireActivity().finish()
            }.no {
                toast(it.msg)
            }
        })
    }

    override fun initData() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_enter_pwd
}
