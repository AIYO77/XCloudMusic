package com.xmusic.module_search.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.orhanobut.logger.Logger
import com.xmusic.module_search.R
import com.xmusic.module_search.adapter.SuggestSearchAdapter
import com.xmusic.module_search.databinding.ActivitySerchBinding
import com.xmusic.module_search.ui.fragment.HotSearchFragment
import com.xmusic.module_search.ui.fragment.SearchResultFragment
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.ext.getColorExt
import com.xw.lib_common.ext.hideSoftInput
import com.xw.lib_common.ext.onTextChanged
import com.xw.lib_common.utils.LinearItemDecoration
import com.xw.lib_coremodel.provider.RouterPath
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.search.SearchViewModel
import kotlinx.android.synthetic.main.activity_serch.*


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Route(path = RouterPath.module_search.PATH_SEARCH)
class SearchActivity : BaseActivity<ActivitySerchBinding, SearchViewModel>() {

    private var popupWindow: PopupWindow? = null

    private var suggestAdapter: SuggestSearchAdapter? = null

    private var needRequestSuggest = true
    override val viewModel: SearchViewModel by viewModels {
        InjectorUtils.provideSearchViewModelFactory(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_serch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val int = savedInstanceState?.getInt(CUR_POSITION, 1) ?: 1
        showFragmentByPosition(int)
    }

    override fun initView() {
        setSupportActionBar(toolBar)
        toolBar.setNavigationOnClickListener { finish() }
        searchEt.onTextChanged {
            if (needRequestSuggest && it.isNotEmpty()) {
                Logger.e("请求建议")
                viewModel.getSuggestSearch(it.trim())
            } else {
                popupWindow?.dismiss()
            }
        }
        searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchEt.text.isNullOrEmpty()) {
                    searchEt.setText(viewModel.defaultSearch.value?.realkeyword)
                }
                search(searchEt.text.toString().trim())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        searchEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEt.text.toString().isNotEmpty() && needRequestSuggest) {
                Logger.e("请求建议")
                viewModel.getSuggestSearch(searchEt.text.toString().trim())
            }
        }

    }

    override fun initData() {
        viewModel.getDefaultSearch()
        searchEt.post {  }
    }

    private var curPosition = 1
    private var curFragment: Fragment? = null
    private var hotSearchFragment: HotSearchFragment? = null
    private var searchResultFragment: SearchResultFragment? = null

    private fun showFragmentByPosition(position: Int) {
        hideAllFragment()
        when (position) {
            1 -> {
                showHotSearchFragment()
            }
            2 -> {
                showSearchResultFragment()
            }
        }
        curPosition = position
    }

    private fun showSearchResultFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        if (searchResultFragment == null) {
            searchResultFragment = SearchResultFragment.newInstance(searchEt.text.toString().trim())
            transaction.add(R.id.fragmentContainer, searchResultFragment!!)
        } else {
            transaction.show(searchResultFragment!!)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun showHotSearchFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        if (hotSearchFragment == null) {
            hotSearchFragment = HotSearchFragment.newInstance()
            transaction.add(R.id.fragmentContainer, hotSearchFragment!!)
        } else {
            transaction.show(hotSearchFragment!!)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun hideAllFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        hotSearchFragment?.let {
            transaction.hide(it)
        }
        searchResultFragment?.let {
            transaction.hide(it)
        }
        transaction.commitAllowingStateLoss()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        curFragment = fragment
        Logger.d("fragment is $fragment")
        if (hotSearchFragment == null && fragment is HotSearchFragment) {
            Logger.d("onAttachFragment 赋值oneFragment")
            hotSearchFragment = fragment
        } else if (searchResultFragment == null && fragment is SearchResultFragment) {
            Logger.d("onAttachFragment 赋值twoFragment")
            searchResultFragment = fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CUR_POSITION, curPosition)
        super.onSaveInstanceState(outState)
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.defaultSearch.observe(this, Observer {
            searchEt.hint = it.showKeyword
        })

        viewModel.suggestSearchList.observe(this, Observer {
            showSuggestPop()
            if (it.isNullOrEmpty()) {
                suggestAdapter?.submitList(null)
            } else {
                suggestAdapter?.submitList(it)
            }
        })
    }

    override fun onBackPressed() {
        if (curFragment is SearchResultFragment) {
            showFragmentByPosition(1)
            return
        }
        super.onBackPressed()

    }

    private fun showSuggestPop() {
        if (popupWindow == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.pop_suggest_search, null)
            suggestAdapter = SuggestSearchAdapter {
                //search
                search(it.keyword)
            }
            view.findViewById<RecyclerView>(R.id.suggestRv).apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                addItemDecoration(
                    LinearItemDecoration(
                        getColorExt(R.color.color_e6e6e6),
                        0.8f
                    )
                )
                adapter = suggestAdapter
            }

            popupWindow = PopupWindow(
                view,
                searchEt.measuredWidth + 56f.dip2px(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
            ).apply {
                isOutsideTouchable = true
                setOnDismissListener {
                    suggestAdapter?.submitList(arrayListOf())
                }
            }
        }
        if (popupWindow!!.isShowing.not()) {
            popupWindow!!.showAsDropDown(searchEt, (-44f).dip2px(), 0)
        }
    }

    fun search(keywords: String) {
        needRequestSuggest = false
        searchEt.clearFocus()
        searchEt.setText(keywords)
        hideSoftInput()
        popupWindow?.dismiss()
        viewModel.saveSearchHistory(searchEt.text.toString().trim())
        showFragmentByPosition(2)
        needRequestSuggest = true
    }

    companion object {
        private const val CUR_POSITION = "cur_position"
    }
}