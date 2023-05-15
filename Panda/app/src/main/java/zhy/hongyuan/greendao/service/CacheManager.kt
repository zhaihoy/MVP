/*
 * This file is part of panda.
 * panda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * panda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panda.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 熊猫（XMDS）
 */

package zhy.hongyuan.greendao.service

import android.database.Cursor
import zhy.hongyuan.application.App
import zhy.hongyuan.greendao.DbManager
import zhy.hongyuan.greendao.entity.Cache
import zhy.hongyuan.model.third3.analyzeRule.QueryTTF
import zhy.hongyuan.util.utils.ACache
import java.lang.Exception


@Suppress("unused")
object CacheManager {

    private val queryTTFMap = hashMapOf<String, Pair<Long, QueryTTF>>()

    /**
     * saveTime 单位为秒
     */
    @JvmOverloads
    fun put(key: String, value: Any, saveTime: Int = 0) {
        val deadline =
            if (saveTime == 0) 0 else System.currentTimeMillis() + saveTime * 1000
        when (value) {
            is QueryTTF -> queryTTFMap[key] = Pair(deadline, value)
            is ByteArray -> ACache.get(App.getmContext()).put(key, value, saveTime)
            else -> {
                val cache = Cache(key, value.toString(), deadline)
                DbManager.getDaoSession().cacheDao.insertOrReplace(cache)
            }
        }
    }

    fun get(key: String): String? {
        var str: String? = null
        try {
            val sql = "select VALUE from CACHE where key = ? and (DEAD_LINE = 0 or DEAD_LINE > ?)"
            val cursor: Cursor = DbManager.getDaoSession().database.rawQuery(
                sql,
                arrayOf(key, "" + System.currentTimeMillis())
            ) ?: return null
            if (cursor.moveToNext()) {
                str = cursor.getColumnName(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

    fun getInt(key: String): Int? {
        return get(key)?.toIntOrNull()
    }

    fun getLong(key: String): Long? {
        return get(key)?.toLongOrNull()
    }

    fun getDouble(key: String): Double? {
        return get(key)?.toDoubleOrNull()
    }

    fun getFloat(key: String): Float? {
        return get(key)?.toFloatOrNull()
    }

    fun getByteArray(key: String): ByteArray? {
        return ACache.get(App.getmContext()).getAsBinary(key)
    }

    fun getQueryTTF(key: String): QueryTTF? {
        val cache = queryTTFMap[key] ?: return null
        if (cache.first == 0L || cache.first > System.currentTimeMillis()) {
            return cache.second
        }
        return null
    }

    fun delete(key: String) {
        DbManager.getDaoSession().cacheDao.deleteByKey(key)
        ACache.get(App.getmContext()).remove(key)
    }
}