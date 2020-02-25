package com.xmusic.module_search.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.logger.Logger
import com.xmusic.module_search.R
import com.xmusic.module_search.adapter.HotSearchAdapter
import com.xmusic.module_search.databinding.FragmentHotSearchBinding
import com.xmusic.module_search.ui.activity.SearchActivity
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_common.ext.toast
import com.xw.lib_coremodel.data.SearchHistory
import com.xw.lib_coremodel.model.bean.search.HotSearchData
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.search.SearchViewModel
import kotlinx.android.synthetic.main.fragment_hot_search.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HotSearchFragment : BaseModelFragment<FragmentHotSearchBinding, SearchViewModel>(),
    HotSearchAdapter.OnItemClick {

    private lateinit var hotSearchAdapter: HotSearchAdapter

    override val viewModel: SearchViewModel by viewModels {
        InjectorUtils.provideSearchViewModelFactory(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_hot_search

    override fun initView() {
        super.initView()
        hotSearchRv.setLayoutManager(LinearLayoutManager(requireActivity()))
        hotSearchAdapter = HotSearchAdapter(this)
        hotSearchRv.setAdapter(hotSearchAdapter)
    }

    override fun initData() {
        super.initData()
        viewModel.getHotSearchList()
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.hotSearchList.observe(this, Observer {
            it.add(0, HotSearchData(type = HotSearchAdapter.VIEW_TYPE_HISTORY))
            hotSearchAdapter.currentList.addAll(it)
            hotSearchAdapter.notifyDataSetChanged()
        })

        viewModel.searchHistory.observe(this, Observer {
            if (it.isNullOrEmpty().not()) {
                hotSearchAdapter.searchHistoryList.clear()
                hotSearchAdapter.searchHistoryList.addAll(it)
                if (hotSearchAdapter.currentList.isNotEmpty()) {
                    hotSearchAdapter.notifyItemChanged(0)
                }
            }
        })
    }

    override fun onDeleteHistoryClick() {
        MaterialDialog(requireContext()).show {
            message(R.string.label_confirm_clear_history)
            positiveButton(R.string.label_clear) {
                viewModel.clearHistory()
            }
            negativeButton(R.string.label_cancel)
        }
    }

    override fun onHistoryClick(searchHistory: SearchHistory) {
        search(searchHistory.keywords)
    }

    override fun onHotSearchClick(hotSearchData: HotSearchData) {
        search(hotSearchData.searchWord)
    }

    private fun search(keywords:String){
        val activity = requireActivity()
        if (activity is SearchActivity){
            activity.search(keywords)
        }
    }

    companion object {
        fun newInstance() = HotSearchFragment()
    }
}