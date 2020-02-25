package com.xw.lib_opensource.recyclerview

import android.content.Context
import android.util.AttributeSet
import com.xw.lib_opensource.R
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 自定义刷新头部
 */
class MusicRefreshHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SimpleComponent(context, attrs, defStyleAttr) {

    private var loadingView: MusicLoadingView = MusicLoadingView(context)

    init {
        addView(loadingView)
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        if (success) {
            loadingView.stopLoading()
            loadingView.setMsg(R.string.refresh_done)
        } else {
            loadingView.stopLoading()
            loadingView.setMsg(R.string.refresh_failed)
        }
        super.onFinish(refreshLayout, success)
        return 500 //延迟500毫秒之后再弹回
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.PullDownToRefresh -> {
                //下拉过程
                loadingView.stopLoading()
                loadingView.setMsg(R.string.listview_header_hint_normal)
            }
            RefreshState.ReleaseToRefresh -> {
                //松开刷新
                loadingView.startLoading()
                loadingView.setMsg(R.string.listview_header_hint_release)
            }
            RefreshState.Loading -> {
                // loading
                loadingView.startLoading()
                loadingView.setMsg(R.string.refreshing)
            }
            else -> {

            }
        }
    }
}