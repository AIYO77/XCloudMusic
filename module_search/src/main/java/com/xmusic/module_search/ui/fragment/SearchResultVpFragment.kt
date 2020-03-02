package com.xmusic.module_search.ui.fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.databinding.FragmentSearchResultVpBinding
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.utils.DataHolder
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.search.SearchResultViewModel
import kotlinx.android.synthetic.main.fragment_search_result_vp.*
import java.util.concurrent.Executors

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class SearchResultVpFragment<T> :
    BaseModelFragment<FragmentSearchResultVpBinding, SearchResultViewModel<T>>() {

    override val viewModel: SearchResultViewModel<T> by viewModels {
        InjectorUtils.provideSearchResultSquareViewModelFactory<T>(requireContext(), NETWORK_IO)
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_result_vp

    abstract val adapter: BaseSearchAdapter<T>

    override fun initView() {
        super.initView()
        resultVpRv.setLayoutManager(LinearLayoutManager(requireContext()))
        resultVpRv.setAdapter(adapter)
    }

    override fun initData() {
        super.initData()
        val data = arguments?.getSerializable(SEARCH_TYPE) as? SearchType
        data?.let { viewModel.showPlayList(it) }

    }

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.keywords = viewModel.getKeywords()
            adapter.submitList(it){
                val layoutManager = (resultVpRv.getRecycleView().layoutManager as LinearLayoutManager)
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    resultVpRv.getRecycleView().scrollToPosition(position)
                }
            }
        })
    }

    companion object {
        const val SEARCH_TYPE = "search_type"
        @Suppress("PrivatePropertyName")
        private val NETWORK_IO = Executors.newFixedThreadPool(5)
    }
}