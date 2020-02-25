package com.xw.lib_common.base.view.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.ext.onNetError

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class BaseFragment<VB : ViewDataBinding> : StateListenerFragment() {

    protected lateinit var bindingView: VB

    private var mRootView: View? = null

    // fragment是否显示了
    protected var mIsVisible = false
    // 是否准备好了
    protected var mIsPrepared = false
    // 加载中
    private var progressBar: ProgressBar? = null
    // 上一次加载数据的时间
    private var lastTimeLoadData: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_base, null)

        bindingView = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bindingView.root.layoutParams = params
        val mContainer = mRootView!!.findViewById<RelativeLayout>(R.id.container)
        mContainer.addView(bindingView.root)

        return mRootView!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsPrepared = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initData()

        super.onViewCreated(view, savedInstanceState)
    }

    open fun initView() {}

    open fun initData() {}

    abstract val layoutId: Int

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInvisible()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("${this::class.java.simpleName} onCreate")
    }

    open fun onVisible() {
        Logger.d("${this::class.java.simpleName} onVisible")
        (lastTimeLoadData != null).yes {
            // 相差一分钟
            val betweenTime =
               differentBetweenTime(lastTimeLoadData!!, System.currentTimeMillis())
            if (betweenTime >= 60) {
                loadData()
            }
        }.no {
            loadData()
        }
    }

    override fun onPause() {
        super.onPause()
        Logger.d("${this::class.java.simpleName} onPause")
        requireActivity().hideSoftInput()
    }

    open fun onInvisible() {
        Logger.d("${this::class.java.simpleName} onInvisible")

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("${this::class.java.simpleName} onDestroy")

    }

    override fun onResume() {
        super.onResume()
        Logger.d("${this::class.java.simpleName} onResume")
    }


    protected fun showLoading() {
        if (progressBar == null) {
            val view = mRootView!!.findViewById<ViewStub>(R.id.vs_loading).inflate()
            progressBar = view!!.findViewById(R.id.loading)
        }
        activity?.apply { hideSoftInput() }
        progressBar?.show()
    }

    protected fun cancelLoading() {
        progressBar?.gone()
    }

    protected fun isLoading(): Boolean {
        return progressBar?.isVisible ?: false
    }

    /**
     * 加载完成的状态
     */
    protected fun showContentView() {
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected fun showError() {
    }

    open fun loadData() {
        lastTimeLoadData = System.currentTimeMillis()
        Logger.d(lastTimeLoadData)
    }


    open fun onError(e: Throwable) {
        cancelLoading()
        context?.let {
            (it as Activity).onNetError(e) {
                showError()
            }
        }
    }

    override fun onDestroyView() {
        bindingView.unbind()
        mRootView = null
        super.onDestroyView()
        Logger.d("${this::class.java.simpleName} onDestroyView")

    }

}