package com.xmusic.module_search.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.FragmentSearchCompositeBinding
import com.xmusic.module_search.ui.fragment.SearchResultVpFragment.Companion.SEARCH_TYPE
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.ext.toListComposite
import com.xw.lib_coremodel.model.bean.search.CompositeCommon
import com.xw.lib_coremodel.utils.DataHolder
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.search.SearchCompositeViewModel

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果: 综合
 */
class SearchCompositeFragment :
    BaseModelFragment<FragmentSearchCompositeBinding, SearchCompositeViewModel>() {

    private val datas = mutableListOf<CompositeCommon>()
    override val viewModel: SearchCompositeViewModel by viewModels {
        InjectorUtils.provideSearchCompositeModelFactory(requireContext())
    }

    override val layoutId: Int
        get() = R.layout.fragment_search_composite

    override fun initData() {
        super.initData()
        val data = DataHolder.getInstance().getData(SEARCH_TYPE) as? SearchType
        data?.let {
            viewModel.getSearchComposite(it.keywords)
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.composite.observe(this, Observer {
//            it.toListComposite()
        })
    }

    companion object {
        fun newInstance(searchType: SearchType): SearchCompositeFragment {
            DataHolder.getInstance().setData(SEARCH_TYPE, searchType)
            return SearchCompositeFragment()
        }
    }

}