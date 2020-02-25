package com.xw.lib_coremodel.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xw.lib_coremodel.model.bean.home.PlayListCat

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface MyPlayListCatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(cats: List<PlayListCat>)

    @Query("SELECT * FROM my_playList_cat")
    fun getMyPLCat(): LiveData<List<PlayListCat>>

    @Delete
    fun deleteMyPLCat(cats: List<PlayListCat>)

    @Query("DELETE FROM my_playList_cat")
    fun deleteAll()

    @Update
    fun updateMyPLCat(cats: List<PlayListCat>)

}