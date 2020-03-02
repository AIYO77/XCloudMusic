package com.xw.lib_coremodel.utils

import android.content.Context
import com.xw.lib_coremodel.model.repository.MainRepository
import com.xw.lib_coremodel.model.repository.PlayingRepository
import com.xw.lib_coremodel.model.repository.home.*
import com.xw.lib_coremodel.model.repository.login.LoginRepository
import com.xw.lib_coremodel.model.repository.search.SearchCompositeRepository
import com.xw.lib_coremodel.model.repository.search.SearchRepository
import com.xw.lib_coremodel.model.repository.search.SearchResultRepository
import com.xw.lib_coremodel.model.repository.video.VideoRepository
import com.xw.lib_coremodel.viewmodel.MainViewModelFactory
import com.xw.lib_coremodel.viewmodel.PlayingViewModelFactory
import com.xw.lib_coremodel.viewmodel.home.*
import com.xw.lib_coremodel.viewmodel.login.LoginViewModelFactory
import com.xw.lib_coremodel.viewmodel.search.SearchCompositeFactory
import com.xw.lib_coremodel.viewmodel.search.SearchResultFragmentViewModelFactory
import com.xw.lib_coremodel.viewmodel.search.SearchViewModelFatory
import com.xw.lib_coremodel.viewmodel.video.VideoViewModelFactory
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object InjectorUtils {

    private fun getmainRepository(context: Context): MainRepository {
        return MainRepository.getInstance(context.applicationContext)
    }

    private fun getHomeRepository(context: Context): HomeRepository {
        return HomeRepository.getInstance(context.applicationContext)
    }

    private fun getRankRepository(context: Context): RankRepository {
        return RankRepository.getInstance(context.applicationContext)
    }

    private fun getPlayListRepository(context: Context): PlayListRepository {
        return PlayListRepository.getInstance(context.applicationContext)
    }

    private fun getPlayingRepository(context: Context): PlayingRepository {
        return PlayingRepository.getInstance(context.applicationContext)
    }

    private fun getLoginRepository(context: Context): LoginRepository {
        return LoginRepository.getInstance(context.applicationContext)
    }

    private fun getRecdDailyRepository(context: Context): RecdDailyRepository {
        return RecdDailyRepository.getInstance(context.applicationContext)
    }

    private fun getPlayListTagsRepository(context: Context): PlayListTagsRepository {
        return PlayListTagsRepository.getInstance(context.applicationContext)
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepository.getInstance(context.applicationContext)
    }

    private fun getVideoRepository(context: Context, networkExecutor: Executor): VideoRepository {
        return VideoRepository.getInstance(context.applicationContext, networkExecutor)
    }

    private fun getSearchCommpositeRepository(context: Context): SearchCompositeRepository {
        return SearchCompositeRepository.getInstance(context.applicationContext)
    }

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        return MainViewModelFactory(getmainRepository(context))
    }

    fun provideHomeViewModelFactory(context: Context): HomeViewModelFactory {
        return HomeViewModelFactory(getHomeRepository(context))
    }

    fun provideRankViewModelFactory(context: Context): RankViewModelFactory {
        return RankViewModelFactory(getRankRepository(context))
    }

    fun providePlayListViewModelFactory(context: Context): PlayListViewModelFactory {
        return PlayListViewModelFactory(getPlayListRepository(context))
    }

    fun providePlayingViewModelFatory(context: Context): PlayingViewModelFactory {
        return PlayingViewModelFactory(getPlayingRepository(context))
    }

    fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
        return LoginViewModelFactory(getLoginRepository(context))
    }

    fun provideRecdDailyViewModelFactory(context: Context): RecdDailyModelFactory {
        return RecdDailyModelFactory(getRecdDailyRepository(context))
    }

    fun providePlayListSquareViewModelFactory(context: Context): PlayListSquareViewModelFactory {
        return PlayListSquareViewModelFactory(getPlayListRepository(context))
    }

    fun providePlayListFragmentViewModelFactory(
        networkExecutor: Executor
    ): PlayListFragmentViewModelFactory {
        return PlayListFragmentViewModelFactory(
            PlayListFragmentRepository(
                networkExecutor
            )
        )
    }

    fun providePlayListTagsViewModelFactory(context: Context): PlayListTagsViewModelFactory {
        return PlayListTagsViewModelFactory(getPlayListTagsRepository(context))
    }

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFatory {
        return SearchViewModelFatory(getSearchRepository(context))
    }

    fun <T> provideSearchResultSquareViewModelFactory(
        context: Context,
        networkExecutor: Executor
    ): SearchResultFragmentViewModelFactory<T> {
        return SearchResultFragmentViewModelFactory(
            SearchResultRepository(
                context,
                networkExecutor
            )
        )
    }

    fun provideSearchCompositeModelFactory(context: Context): SearchCompositeFactory {
        return SearchCompositeFactory(getSearchCommpositeRepository(context))
    }

    fun provideVideoModelFactory(
        context: Context,
        networkExecutor: Executor
    ): VideoViewModelFactory {
        return VideoViewModelFactory(getVideoRepository(context, networkExecutor))
    }
}