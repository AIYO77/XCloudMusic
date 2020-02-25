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
interface SearchTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(types: List<SearchType>?)

    @Query("SELECT * FROM search_type")
    fun getSearchTypes(): LiveData<List<SearchType>>
}