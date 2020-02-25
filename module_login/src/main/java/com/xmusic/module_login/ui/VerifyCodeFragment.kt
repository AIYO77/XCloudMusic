package com.xmusic.module_login.ui


import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentVerifyCodeBinding
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.no
import com.xw.lib_common.ext.toast
import com.xw.lib_common.ext.yes
import com.xw.lib_common.listener.VerifyAction
import kotlinx.android.synthetic.main.fragment_verify_code.*

/**
 * 手机验证码验证
 */
class VerifyCodeFragment : LoginBaseFragment<FragmentVerifyCodeBinding>(),
    VerifyAction.OnVerificationCodeChangedListener {

    private val args: VerifyCodeFragmentArgs by navArgs()

    private var timer: MyCountDownTimer? = null

    override val layoutId: Int
        get() = R.layout.fragment_verify_code

    override fun initView() {
        verifyCodeView.setOnVerificationCodeChangedListener(this)
        phoneTv.text = "+86 ${args.phone.replaceRange(3, 7, " **** ")}"
        timer = MyCountDownTimer(60000, 1000)
        countDownTv.setOnClickListener {
            showLoading()
            viewModel.sendCaptcha(args.phone)
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.verifyCaptchaResult.observe(this, Observer {
            cancelLoading()
            it.result.yes {
                args.password.isNullOrEmpty().yes {
                    if (findNavController().currentDestination?.id == R.id.enter_code_fragment)
                        findNavController().navigate(
                            VerifyCodeFragmentDirections.actionEnterCodeFragmentToSettingPwdFragment(
                                args.phone,
                                verifyCodeView.text.toString()
                            )
                        )
                }.no {
                    //忘记密码过来的
                    requireActivity().finish()
                }

            }.no {
                toast(it.msg)
            }
        })
        viewModel.sendCaptchaResult.observe(this, Observer {
            cancelLoading()
            if (isResumed) {
                it.result.yes {
                    timer?.start()
                }.no {
                    toast(it.msg)
                }
            }
        })
        if (args.password.isNullOrEmpty().not())
            viewModel.registerResult.observe(this, Observer {
                cancelLoading()
                it.result.yes {
                    toast(getString(R.string.data_change_pwd_suc))
                    requireActivity().finish()
                }.no {
                    toast(it.msg)
                }
            })
    }

    override fun initData() {
        timer?.start()
    }

    override fun onDestroyView() {
        timer?.cancel()
        timer = null
        super.onDestroyView()
    }

    private inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            countDownTv.apply {
                isClickable = true
                setTextColor(getColor(R.color.banner_5784ad))
                text = getString(R.string.label_captcha_replay)
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            countDownTv.apply {
                isClickable = false
                setTextColor(getColor(R.color.black_979797))
                text = "${millisUntilFinished / 1000}s"
            }
        }

    }

    override fun onVerCodeChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun onInputCompleted(s: CharSequence) {
        showLoading()
        args.password.isNullOrEmpty().yes {
            //注册新账号 验证验证码
            viewModel.verifyCode(phone = args.phone, captcha = s.toString())
        }.no {
            //忘记密码过来的 修改密码
            viewModel.registerOrChangePwd(args.phone, args.password!!, s.toString())
        }
    }
}
