package com.xmusic.module_login.ui


import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentForgetPwdBinding
import com.xw.lib_common.ext.no
import com.xw.lib_common.ext.toast
import com.xw.lib_common.ext.yes
import kotlinx.android.synthetic.main.fragment_forget_pwd.*

/**
 * 忘记密码
 */
class ForgetPwdFragment : LoginBaseFragment<FragmentForgetPwdBinding>() {

    private val args: ForgetPwdFragmentArgs by navArgs()

    override fun initView() {
        nextBtn.setOnClickListener {
            val pwd = settingPwdEt.text.toString()
            when {
                pwd.isEmpty() -> {
                    toast(getString(R.string.label_please_input_pwd))
                }
                pwd.length < 6 -> {
                    toast(getString(R.string.label_please_input_six_pwd))
                }
                else -> {
                    showLoading()
                    viewModel.sendCaptcha(args.phone)
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.sendCaptchaResult.observe(this, Observer {
            cancelLoading()
            if (isResumed) {
                it.result.yes {
                    if (findNavController().currentDestination?.id == R.id.forget_pwd_fragment)
                        findNavController().navigate(
                            ForgetPwdFragmentDirections.actionForgetPwdFragmentToEnterCodeFragment(
                                args.phone,
                                settingPwdEt.text.toString()
                            )
                        )
                }.no {
                    toast(it.msg)
                }
            }
        })
    }

    override fun initData() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_forget_pwd


}
