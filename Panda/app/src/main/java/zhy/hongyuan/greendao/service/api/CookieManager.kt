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

package zhy.hongyuan.greendao.service.api

interface CookieManager {

    /**
     * 保存cookie
     */
    fun setCookie(url: String, cookie: String?)

    /**
     * 替换cookie
     */
    fun replaceCookie(url: String, cookie: String)

    /**
     * 获取cookie
     */
    fun getCookie(url: String): String

    /**
     * 移除cookie
     */
    fun removeCookie(url: String)

    fun cookieToMap(cookie: String): MutableMap<String, String>

    fun mapToCookie(cookieMap: Map<String, String>?): String?
}