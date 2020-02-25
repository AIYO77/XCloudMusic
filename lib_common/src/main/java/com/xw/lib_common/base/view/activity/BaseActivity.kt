package com.xw.lib_common.base.view.activity

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import com.xw.lib_common.utils.NetWorkState
import com.xw.lib_coremodel.ext.onNetError
import com.xw.lib_coremodel.viewmodel.BaseViewModel


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : BaseBindingActivity<VB>() {

    abstract val viewModel: VM?

    private val netWorkState: NetWorkState by lazy {
        NetWorkState(this)
    }

    override fun startObserve() {
        viewModel?.apply {
            mException.observe(this@BaseActivity, Observer { it?.let { onError(it) } })
        }
//        netWorkState.observe(this, Observer {
//            toast(it.yes { "联网成功" }.no { "未联网" })
//        })
    }


    open fun showNetError() {
//        bindingView.root.gone()
    }

    open fun onError(e: Throwable) {
        onNetError(e) {
            showNetError()
        }
    }
}

