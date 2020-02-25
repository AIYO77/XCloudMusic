package com.xmusic.module_home.ui.fragment

import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.HomeUserFragmentBinding

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HomeUserFragment : BaseModelFragment<HomeUserFragmentBinding,BaseViewModel>() {

    override fun initView() {

    }

    override fun initData() {
    }

    override val layoutId: Int
        get() = R.layout.home_user_fragment


    override val viewModel: BaseViewModel?
        get() = null

    companion object {
        fun newInstance() = HomeUserFragment()
    }
}