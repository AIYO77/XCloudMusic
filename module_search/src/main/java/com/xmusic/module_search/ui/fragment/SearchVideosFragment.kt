package com.xmusic.module_search.ui.fragment

import androidx.lifecycle.Observer
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchVideosAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.video.SearchVideoItemInfo
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：单曲
 */
class SearchVideosFragment : SearchResultVpFragment<SearchVideoItemInfo>() {

    override val adapter: BaseSearchAdapter<SearchVideoItemInfo>
        get() = SearchVideosAdapter()

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.keywords = viewModel.getKeywords()
            adapter.submitList(it)
        })
    }

    companion object {
        fun newInstance(searchType: SearchType): SearchVideosFragment {
            DataHolder.getInstance().setData(SEARCH_TYPE, searchType)
            return SearchVideosFragment()
        }
    }
}