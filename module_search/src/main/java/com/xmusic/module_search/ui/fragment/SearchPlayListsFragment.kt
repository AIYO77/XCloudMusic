package com.xmusic.module_search.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchPlayListAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：单曲
 */
class SearchPlayListsFragment : SearchResultVpFragment<PlayList>() {

    override val adapter: BaseSearchAdapter<PlayList>
        get() = SearchPlayListAdapter()

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.keywords = viewModel.getKeywords()
            adapter.submitList(it)
        })
    }

    companion object {
        fun newInstance(searchType: SearchType): SearchPlayListsFragment {
            DataHolder.getInstance().setData(SEARCH_TYPE, searchType)
            return SearchPlayListsFragment()
        }
    }
}