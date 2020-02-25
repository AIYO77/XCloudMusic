package com.xw.lib_coremodel.viewmodel.home

import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.repository.home.PlayListRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListSquareViewModel internal constructor(private val repository: PlayListRepository) :
    BaseViewModel(repository) {

    val myPLCat = repository.myPLCatDao.getMyPLCat()

    fun getHotCatList() {
        launch {
            val hotResponse = withContext(Dispatchers.IO) { repository.getHotCatList() }

            if (hotResponse.isSuccess()) {
                hotResponse.tags.apply {
                    add(0, PlayListCat(name = "精品", category = -1, isResident = true))
                }
                withContext(Dispatchers.IO) { repository.savePlayListCat(hotResponse.tags) }
            }
        }
    }

    fun saveMyCats(cats: List<PlayListCat>) {
        launch {
            withContext(Dispatchers.IO) { repository.myPLCatDao.addAll(cats) }
        }
    }
}