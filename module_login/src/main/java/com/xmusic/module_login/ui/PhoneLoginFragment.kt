package com.xmusic.module_login.ui

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentPhoneLoginBinding
import com.xw.lib_common.ext.*
import kotlinx.android.synthetic.main.fragment_phone_login.*

/**
 * 手机登陆
 */
class PhoneLoginFragment : LoginBaseFragment<FragmentPhoneLoginBinding>() {

    override fun initView() {
        phoneEt.onTextChanged {
            if (it.isNotEmpty()) {
                areaCode.setTextColor(getColor(R.color.color_343434))
            } else {
                areaCode.setTextColor(getColor(R.color.color_cbcbcb))
            }
        }

        phoneNextBtn.setOnClickListener {
            if (isLoading().not()) {
                val phone = phoneEt.text.toString()
                if (phone.isEmpty()) {
                    toast("输入手机号")
                    return@setOnClickListener
                } else if (phone.length < 11) {
                    toast("请输入十一位数字的手机号")
                    return@setOnClickListener
                }
                showLoading()
                viewModel.checkPhone(phone)
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.checkPhoneResult.observe(this, Observer {
            cancelLoading()
            if (isResumed) {
                it.result.yes {
                    if (findNavController().currentDestination?.id == R.id.phone_login_fragment) {
                        val directions =
                            PhoneLoginFragmentDirections.actionPhoneLoginToEnterPwdFragment(
                                phoneEt.text.toString(),
                                it.msg
                            )
                        findNavController().navigate(directions)
                    }
                }.no {
                    toast(it.msg)
                }
            }
        })

        viewModel.sendCaptchaResult.observe(this, Observer {
            cancelLoading()
            if (isResumed) {
                it.result.yes {
                    if (findNavController().currentDestination?.id == R.id.phone_login_fragment) {
                        val directions =
                            PhoneLoginFragmentDirections.actionPhoneLoginToEnterCodeFragment(
                                phoneEt.text.toString(),
                                null
                            )
                        findNavController().navigate(directions)
                    }
                }.no {
                    toast(it.msg)
                }
            }
        })
    }
    override fun initData() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_phone_login

}
