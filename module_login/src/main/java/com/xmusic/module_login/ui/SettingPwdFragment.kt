package com.xmusic.module_login.ui


import androidx.navigation.fragment.navArgs
import com.xmusic.module_login.R
import com.xmusic.module_login.databinding.FragmentSettingPwdBinding
import com.xw.lib_common.ext.toast
import kotlinx.android.synthetic.main.fragment_setting_pwd.*


/**
 * setting pwd
 */
class SettingPwdFragment : LoginBaseFragment<FragmentSettingPwdBinding>() {

    private val args: SettingPwdFragmentArgs by navArgs()
    override fun initData() {
    }

    override fun initView() {
        nextBtn.setOnClickListener {
            val pwd = settingPwdEt.text.toString()
            if (pwd.isEmpty() || pwd.length < 6) {
                toast(getString(R.string.data_input_password))
                return@setOnClickListener
            }
            SettingPwdFragmentDirections.enterCodeFragmentToRegisterFragment(
                args.phone,
                args.code,
                pwd
            )
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_setting_pwd


}
