package com.xw.lib_common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.*
import com.xw.lib_common.R
import com.xw.lib_common.base.view.activity.BaseBindingActivity
import com.xw.lib_common.ext.*
import com.xw.lib_opensource.recyclerview.MusicLoadingView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class StateRecycleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var recycleView: RecyclerView
    private var loadingView: MusicLoadingView
    private var netErrorView: AppCompatTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.state_recyclerview, this)
        recycleView = findViewById(R.id.recycleView)
        loadingView = findViewById(R.id.loadingView)
        netErrorView = findViewById(R.id.netError)
        loadingView.startLoading()
        loadingView.setMsg(R.string.loading)

        netErrorView.setOnClickListener {
            if (context is BaseBindingActivity<*>) {
                context.initData()
            }
        }
        if (context is BaseBindingActivity<*>) {
            if (context.isNeedBottomPlay()) {
                recycleView.addOnChildAttachStateChangeListener(object :
                    RecyclerView.OnChildAttachStateChangeListener {
                    override fun onChildViewDetachedFromWindow(view: View) {
                        val position = recycleView.getChildAdapterPosition(view)
                        val itemCount = recycleView.layoutManager?.itemCount ?: 0
                        if (context.bottomPlayIsShow() && position == itemCount - 1) {
                            changeLastItemMarginBottom(view, 0)
                        }
                    }

                    override fun onChildViewAttachedToWindow(view: View) {
                        val position = recycleView.getChildAdapterPosition(view)
                        val itemCount = recycleView.layoutManager?.itemCount ?: 0
                        if (context.bottomPlayIsShow() && position == itemCount - 1) {
                            changeLastItemMarginBottom(view, 55f.dip2px())
                        }
                    }
                })
            }
        }
    }

    private fun changeLastItemMarginBottom(view: View, marginBottom: Int) {
        val layoutParams = view.layoutParams
        if (layoutParams is MarginLayoutParams) {
            layoutParams.setMargins(0, 0, 0, marginBottom)
            view.requestLayout()
        }
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            check()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            check()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            check()
        }

    }

    private fun check() {
        if (recycleView.adapter != null && recycleView.adapter!!.itemCount > 0) {
            loadingView.stopLoading()
            recycleView.show()
            loadingView.gone()
            netErrorView.gone()

        }
    }

    fun setLayoutManager(@Nullable layout: RecyclerView.LayoutManager) {
        recycleView.layoutManager = layout
    }

    fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        recycleView.adapter = adapter
        adapter.registerAdapterDataObserver(observer)
    }

    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        recycleView.addItemDecoration(itemDecoration)
    }

    fun getRecycleView(): RecyclerView {
        return recycleView
    }

    fun netError() {
        loadingView.stopLoading()
        loadingView.gone()
        if (recycleView.adapter != null && recycleView.adapter!!.itemCount > 0) {
            recycleView.show()
            netErrorView.gone()
        } else {
            recycleView.gone()
            netErrorView.show()
        }


    }
}