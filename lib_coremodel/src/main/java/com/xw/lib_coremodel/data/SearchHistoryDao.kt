package com.xw.lib_coremodel.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history order by time desc limit 0,10 ")
    fun getAllHistory(): LiveData<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history")
    fun deleteAllHistory()
}