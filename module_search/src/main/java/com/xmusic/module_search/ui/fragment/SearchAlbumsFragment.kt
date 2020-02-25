package com.xmusic.module_search.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchAlbumsAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：专辑
 */
class SearchAlbumsFragment : SearchResultVpFragment<AlbumItemInfo>() {


    override val adapter: BaseSearchAdapter<AlbumItemInfo>
        get() = SearchAlbumsAdapter()

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.keywords = viewModel.getKeywords()
            adapter.submitList(it)
        })
    }

    companion object {
        fun newInstance(searchType: SearchType): SearchAlbumsFragment {
            DataHolder.getInstance().setData(SEARCH_TYPE, searchType)
            return SearchAlbumsFragment()
        }
    }

}