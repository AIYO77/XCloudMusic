package com.xw.lib_coremodel.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.utils.SEARCH_TYPE_FILENAME
import kotlinx.coroutines.coroutineScope

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(SEARCH_TYPE_FILENAME)
                .use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val searchType = object : TypeToken<List<SearchType>>() {}.type
                        val database = AppDatabase.getInstance(applicationContext)
                        val list =
                            CoreApplication.GSON.fromJson<List<SearchType>>(jsonReader, searchType)
                        database.searchTypeDao().insertAll(list)
                        Result.success()
                    }
                }
        } catch (e: Exception) {
            Logger.e(e.localizedMessage)
            Result.failure()
        }
    }

}