package com.xw.lib_common.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import com.xw.lib_coremodel.viewmodel.BaseViewModel

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class BaseModelFragment<VB : ViewDataBinding, VM : BaseViewModel> : BaseFragment<VB>() {

    abstract val viewModel: VM?

    protected var isFirstInit = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startObserve()
        super.onViewCreated(view, savedInstanceState)
        isFirstInit = false
    }
    open fun startObserve() {
        viewModel?.apply {
            mException.observe(this@BaseModelFragment, Observer { onError(it) })
        }
    }

    override fun updateLrc(lrc: LrcAdnTlyRic) {
    }

    override fun updateTrackInfo() {
    }

    override fun updateQueue() {
    }
}