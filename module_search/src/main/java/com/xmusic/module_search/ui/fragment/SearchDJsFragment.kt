package com.xmusic.module_search.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchDJsAdapter
import com.xmusic.module_search.adapter.SearchSingersAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.dj.DjRadioInfo
import com.xw.lib_coremodel.model.bean.home.ArtistInfo
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：单曲
 */
class SearchDJsFragment : SearchResultVpFragment<DjRadioInfo>() {

    override val adapter: BaseSearchAdapter<DjRadioInfo>
        get() = SearchDJsAdapter()

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.keywords = viewModel.getKeywords()
            adapter.submitList(it)
        })
    }

    companion object {
        fun newInstance(searchType: SearchType): SearchDJsFragment {
            DataHolder.getInstance().setData(SEARCH_TYPE, searchType)
            return SearchDJsFragment()
        }
    }
}