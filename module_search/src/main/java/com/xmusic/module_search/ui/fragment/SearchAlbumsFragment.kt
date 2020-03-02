package com.xmusic.module_search.ui.fragment

import android.os.Bundle
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchAlbumsAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：专辑
 */
class SearchAlbumsFragment : SearchResultVpFragment<AlbumItemInfo>() {

    override val adapter: BaseSearchAdapter<AlbumItemInfo> = SearchAlbumsAdapter()

    companion object {
        fun newInstance(searchType: SearchType): SearchAlbumsFragment {
            return SearchAlbumsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(SEARCH_TYPE,searchType)
                }
            }
        }
    }

}