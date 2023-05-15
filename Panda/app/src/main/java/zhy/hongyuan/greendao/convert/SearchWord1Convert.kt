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

package zhy.hongyuan.greendao.convert

import org.greenrobot.greendao.converter.PropertyConverter
import zhy.hongyuan.greendao.entity.search.SearchWord1
import zhy.hongyuan.util.utils.GSON
import zhy.hongyuan.util.utils.fromJsonArray

/**
 * @author  hongyuan
 * @date 2021/12/7 8:14
 */
class SearchWord1Convert: PropertyConverter<List<SearchWord1>, String> {
    override fun convertToEntityProperty(databaseValue: String?): List<SearchWord1>? {
        return GSON.fromJsonArray(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: List<SearchWord1>?): String {
        return GSON.toJson(entityProperty)
    }
}