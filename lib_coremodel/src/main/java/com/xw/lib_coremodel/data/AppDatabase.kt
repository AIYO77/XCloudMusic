package com.xw.lib_coremodel.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.utils.DATABASE_NAME
import com.xw.lib_coremodel.workers.SeedDatabaseWorker

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@Database(
    entities = [PlaybackList::class, PlaybackHistory::class, RecentHistory::class,
        SongLrc::class, LoginUserInfo::class, PlayListCat::class, SearchHistory::class,
        SearchType::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playbackListDao(): PlaybackListDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun recentDao(): RecentHistoryDao
    abstract fun urlAndLrc(): SongUrlAndLrcDao
    abstract fun loginUserDao(): LoginUserInfoDao
    abstract fun myPlayListCatDao(): MyPlayListCatDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun searchTypeDao(): SearchTypeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                        WorkManager.getInstance(context).enqueue(request)
//                    }
//                }
//                )
            .build()

        }
    }

}