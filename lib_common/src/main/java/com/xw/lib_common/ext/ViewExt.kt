package com.xw.lib_common.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */


fun View.gone() {
    if (this.isShow())
        this.visibility = View.GONE
}

fun View.invisible() {
    if (this.isShow()) {
        this.visibility = View.INVISIBLE
    }
}

fun View.show() {
    if (this.isShow().not())
        this.visibility = View.VISIBLE
}

fun View.isShow(): Boolean {
    return this.visibility == View.VISIBLE
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }
    })
}

fun RecyclerView.addOnScrollStateChanged(onScrollStateChanged: (RecyclerView, Int) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            onScrollStateChanged.invoke(recyclerView, newState)
        }
    })
}

fun RecyclerView.onScrolled(onScrolled: (RecyclerView, Int, Int) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScrolled.invoke(recyclerView, dx, dy)
        }
    })
}

fun RecyclerView.Adapter<RecyclerView.ViewHolder>.adapterDataChangeObserver(change: () -> Unit) {
    this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            change()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            change()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            change()
        }
    })

}

fun TabLayout.setTabWidth(padding: Int) {
    post {
        try {
            //拿到tabLayout的mTabStrip属性
            val mTabStrip = this.getChildAt(0) as LinearLayout
            mTabStrip.children.forEach { tabView ->
                //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                val mTextViewField = tabView.javaClass.getDeclaredField("mTextView");
                mTextViewField.isAccessible = true
                val mTextView = mTextViewField.get(tabView) as TextView

                tabView.setPadding(0, 0, 0, 0)

                //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                val params = tabView.layoutParams as LinearLayout.LayoutParams
                params.width = width
                params.leftMargin = padding
                params.rightMargin = padding
                tabView.layoutParams = params

                tabView.invalidate()
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}
