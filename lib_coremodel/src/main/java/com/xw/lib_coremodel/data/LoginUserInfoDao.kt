package com.xw.lib_coremodel.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Dao
interface LoginUserInfoDao {

    @Query("SELECT * FROM login_user limit 0,1 ")
    fun getLoginUser(): LiveData<LoginUserInfo?>

    @Update
    fun updateLoginUser(loginUserInfo: LoginUserInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(loginUserInfo: LoginUserInfo)

    @Query("DELETE FROM login_user")
    fun deleteUser()
}