package com.xw.lib_common.base.view.activity

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.databinding.ActivityBaseBinding
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.hideSoftInput
import com.xw.lib_common.ext.isShow
import com.xw.lib_common.ext.show
import com.xw.lib_common.play.QuickControlsFragment
import com.xw.lib_common.service.MusicPlayer

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class BaseBindingActivity<VB : ViewDataBinding> : AutoServerActivity() {

    protected lateinit var bindingView: VB

    private var quickControlsFragment: QuickControlsFragment? = null
    private var progressBar: ProgressBar? = null


    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initView()
        startObserve()
        initData()
    }

    abstract fun initView()
    abstract fun initData()
    open fun startObserve() {}

    override fun setContentView(layoutResID: Int) {
        val mBaseBinding: ActivityBaseBinding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false)
        bindingView = DataBindingUtil.inflate(layoutInflater, layoutResID, null, false)

        // content
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bindingView.root.layoutParams = params

        val mContainer = mBaseBinding.root.findViewById(R.id.container) as RelativeLayout
        mContainer.addView(bindingView.root, 0)
        super.setContentView(mBaseBinding.root)


        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * 显示/隐藏 底部播放控制栏
     */
    private fun showQuickControl() {
        val transaction = supportFragmentManager.beginTransaction()
        val queueSize = MusicPlayer.getQueueSize()
        Logger.i("queueSize : $queueSize  isNeedBottomPlay: ${isNeedBottomPlay()}")
        if (isNeedBottomPlay() && queueSize > 0) {
            findViewById<View>(R.id.bottom_container)?.show()
            if (quickControlsFragment == null) {
                quickControlsFragment = QuickControlsFragment.newInstance()
                transaction.add(R.id.bottom_container, quickControlsFragment!!)
                    .commitAllowingStateLoss()
            } else {
                transaction.show(quickControlsFragment!!).commitAllowingStateLoss()
            }
        } else {
            if (quickControlsFragment != null) {
                transaction.hide(quickControlsFragment!!).commitAllowingStateLoss()
            }
            if (isNeedBottomPlay()) {
                findViewById<View>(R.id.bottom_container)?.gone()
            }
        }
    }

    /**
     * 是否需要底部播放快捷栏目
     *
     * @return 默认为true
     */
    open fun isNeedBottomPlay(): Boolean {
        return true
    }

    fun bottomPlayIsShow(): Boolean {
        return findViewById<View>(R.id.bottom_container)?.isShow() ?: false
    }

    override fun updateQueue() {
        super.updateQueue()
        showQuickControl()
    }

    protected fun showLoading() {
        if (progressBar == null) {
            val view = (findViewById<ViewStub>(R.id.vs_loading)).inflate()
            progressBar = view!!.findViewById(R.id.loading)
        }
        this.apply { hideSoftInput() }
        progressBar?.show()
    }

    protected fun cancelLoading() {
        progressBar?.gone()
    }


}