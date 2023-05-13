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
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package xyz.fycz.dynamic.fix

import me.fycz.maple.MapleUtils
import xyz.fycz.dynamic.AppLoadImpl.Companion.spu
import zhy.hongyuan.util.utils.AdUtils

/**
 * @author  hongyuan
 * @date 2022/4/25 21:49
 */
interface AppFixHandle {

    fun onFix(key: String): BooleanArray

    fun handleFix(key: String, vararg fix: Pair<String, () -> Unit>): BooleanArray {
        val results = mutableListOf<Boolean>()
        fix.forEach {
            val result = try {
                it.second()
                true
            } catch (e: Exception) {
                MapleUtils.log(e)
                false
            }
            results.add(result)
            fixResult(key, it.first, result)
        }
        return results.toBooleanArray()
    }

    fun fixResult(key: String, name: String, success: Boolean) {
        val res = if (success) "Success" else "Failed"
        if (!spu.getBoolean("$key-$name-$res", false)) {
            AdUtils.adRecord(name, "fx$res")
            spu.edit().run {
                putBoolean("$key-$name-$res", true)
                apply()
            }
        }
    }
}