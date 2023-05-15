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

package xyz.fycz.dynamic.fix

import me.fycz.maple.MapleBridge
import me.fycz.maple.MapleUtils
import me.fycz.maple.MethodReplacement
import zhy.hongyuan.model.sourceAnalyzer.BookSourceManager
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil
import zhy.hongyuan.webapi.crawler.base.ReadCrawler

/**
 * @author  hongyuan
 * @date 2022/5/11 9:31
 */
@AppFix([243, 244], ["修复搜索时当前分组不存在时无法搜索的问题"], "2022-05-11")
class App244Fix2 : AppFixHandle{
    override fun onFix(key: String): BooleanArray {
        return handleFix(
            key,
            "getEnableReadCrawlers" to { fixGetEnableReadCrawlers() },
        )
    }

    private fun fixGetEnableReadCrawlers() {
        MapleUtils.findAndHookMethod(
            ReadCrawlerUtil::class.java,
            "getEnableReadCrawlers",
            String::class.java,
            object : MethodReplacement() {
                override fun replaceHookedMethod(param: MapleBridge.MethodHookParam): Any {
                    return getEnableReadCrawlers(param.args[0] as String)
                }
            }
        )
    }

    fun getEnableReadCrawlers(group: String?): List<ReadCrawler> {
        val crawlers = ArrayList<ReadCrawler>()
        var sources =
            if (group.isNullOrEmpty())
                BookSourceManager.getEnabledBookSource()
            else BookSourceManager.getEnableSourceByGroup(group)
        if (sources.size == 0) {
            sources = BookSourceManager.getEnabledBookSource()
        }
        for (source in sources) {
            crawlers.add(ReadCrawlerUtil.getReadCrawler(source))
        }
        return crawlers
    }
}