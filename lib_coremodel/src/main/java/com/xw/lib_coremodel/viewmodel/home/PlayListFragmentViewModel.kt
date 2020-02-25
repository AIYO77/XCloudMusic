package com.xw.lib_coremodel.viewmodel.home

import androidx.lifecycle.MutableLiveData
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.repository.home.PlayListFragmentRepository
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListFragmentViewModel(private val repository: PlayListFragmentRepository) :
    BaseViewModel() {
    private val playListCat = MutableLiveData<PlayListCat>()

    private val repoResult = map(playListCat) {
        repository.postsOfPlayList(it, 30)
    }

    val posts = switchMap(repoResult) { it.pagedList }
    val networkState = switchMap(repoResult) { it.networkState }

    fun showPlayList(cat: PlayListCat): Boolean {
        if (playListCat.value == cat) {
            return false
        }
        playListCat.value = cat
        return true
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }
}