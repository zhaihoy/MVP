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

package zhy.hongyuan.model.third3.webBook

import android.util.Log
import zhy.hongyuan.model.third3.analyzeRule.AnalyzeRule
import zhy.hongyuan.model.third3.analyzeRule.AnalyzeUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import zhy.panda.myreader.R
import zhy.hongyuan.application.App
import zhy.hongyuan.greendao.entity.Book
import zhy.hongyuan.greendao.entity.Chapter
import zhy.hongyuan.greendao.entity.rule.BookSource
import zhy.hongyuan.greendao.entity.rule.ContentRule
import zhy.hongyuan.greendao.service.ChapterService
import zhy.hongyuan.model.third3.ContentEmptyException
import zhy.hongyuan.model.third3.NoStackTraceException
import zhy.hongyuan.util.utils.HtmlFormatter
import zhy.hongyuan.util.utils.NetworkUtils

/**
 * 获取正文
 */
object BookContent {

    @Throws(Exception::class)
    suspend fun analyzeContent(
        scope: CoroutineScope,
        bookSource: BookSource,
        book: Book,
        bookChapter: Chapter,
        redirectUrl: String,
        baseUrl: String,
        body: String?,
        nextChapterUrl: String? = null
    ): String {
        body ?: throw NoStackTraceException(
            App.getmContext().getString(R.string.error_get_web_content, baseUrl)
        )
        Log.d(bookSource.sourceUrl, "≡获取成功:${baseUrl}")
        Log.d(bookSource.sourceUrl, body)
        val mNextChapterUrl = if (!nextChapterUrl.isNullOrEmpty()) {
            nextChapterUrl
        } else {
            val chapters = ChapterService.getInstance().findBookAllChapterByBookId(book.id)
            if (chapters.size > bookChapter.number + 1) {
                chapters[bookChapter.number + 1].url
            } else {
                null
            }
        }
        val content = StringBuilder()
        val nextUrlList = arrayListOf(baseUrl)
        val contentRule = bookSource.contentRule
        val analyzeRule = AnalyzeRule(book, bookSource).setContent(body, baseUrl)
        analyzeRule.setRedirectUrl(baseUrl)
        analyzeRule.nextChapterUrl = mNextChapterUrl
        scope.ensureActive()
        var contentData = analyzeContent(
            book, baseUrl, redirectUrl, body, contentRule, bookChapter, bookSource, mNextChapterUrl
        )
        content.append(contentData.first)
        if (contentData.second.size == 1) {
            var nextUrl = contentData.second[0]
            while (nextUrl.isNotEmpty() && !nextUrlList.contains(nextUrl)) {
                if (!mNextChapterUrl.isNullOrEmpty()
                    && NetworkUtils.getAbsoluteURL(baseUrl, nextUrl)
                    == NetworkUtils.getAbsoluteURL(baseUrl, mNextChapterUrl)
                ) break
                nextUrlList.add(nextUrl)
                scope.ensureActive()
                val res = AnalyzeUrl(
                    mUrl = nextUrl,
                    source = bookSource,
                    ruleData = book,
                    headerMapF = bookSource.getHeaderMap()
                ).getStrResponseAwait()
                res.body?.let { nextBody ->
                    contentData = analyzeContent(
                        book, nextUrl, res.url, nextBody, contentRule,
                        bookChapter, bookSource, mNextChapterUrl, false
                    )
                    nextUrl =
                        if (contentData.second.isNotEmpty()) contentData.second[0] else ""
                    content.append("\n").append(contentData.first)
                }
            }
            Log.d(bookSource.sourceUrl, "◇本章总页数:${nextUrlList.size}")
        } else if (contentData.second.size > 1) {
            Log.d(bookSource.sourceUrl, "◇并发解析目录,总页数:${contentData.second.size}")
            withContext(IO) {
                val asyncArray = Array(contentData.second.size) {
                    async(IO) {
                        val urlStr = contentData.second[it]
                        val analyzeUrl = AnalyzeUrl(
                            mUrl = urlStr,
                            source = bookSource,
                            ruleData = book,
                            headerMapF = bookSource.getHeaderMap()
                        )
                        val res = analyzeUrl.getStrResponseAwait()
                        analyzeContent(
                            book, urlStr, res.url, res.body!!, contentRule,
                            bookChapter, bookSource, mNextChapterUrl, false
                        ).first
                    }
                }
                asyncArray.forEach { coroutine ->
                    scope.ensureActive()
                    content.append("\n").append(coroutine.await())
                }
            }
        }
        var contentStr = content.toString()
        val replaceRegex = contentRule.replaceRegex
        if (!replaceRegex.isNullOrEmpty()) {
            contentStr = analyzeRule.getString(replaceRegex, contentStr)
        }
        Log.d(bookSource.sourceUrl, "┌获取章节名称")
        Log.d(bookSource.sourceUrl, "└${bookChapter.title}")
        Log.d(bookSource.sourceUrl, "┌获取正文内容")
        Log.d(bookSource.sourceUrl, "└\n$contentStr")
        if (contentStr.isBlank()) {
            throw ContentEmptyException("内容为空")
        }
        //BookHelp.saveContent(bookSource, book, bookChapter, contentStr)
        return contentStr
    }

    @Throws(Exception::class)
    private fun analyzeContent(
        book: Book,
        baseUrl: String,
        redirectUrl: String,
        body: String,
        contentRule: ContentRule,
        chapter: Chapter,
        bookSource: BookSource,
        nextChapterUrl: String?,
        printLog: Boolean = true
    ): Pair<String, List<String>> {
        val analyzeRule = AnalyzeRule(book, bookSource)
        analyzeRule.setContent(body, baseUrl)
        val rUrl = analyzeRule.setRedirectUrl(redirectUrl)
        analyzeRule.nextChapterUrl = nextChapterUrl
        val nextUrlList = arrayListOf<String>()
        analyzeRule.chapter = chapter
        //获取正文
        var content = analyzeRule.getString(contentRule.content)
        //content = HtmlFormatter.formatKeepImg(content, rUrl)
        content = HtmlFormatter.format(content)
        //获取下一页链接
        val nextUrlRule = contentRule.contentUrlNext
        if (!nextUrlRule.isNullOrEmpty()) {
            if (printLog) Log.d(bookSource.sourceUrl, "┌获取正文下一页链接")
            analyzeRule.getStringList(nextUrlRule, isUrl = true)?.let {
                nextUrlList.addAll(it)
            }
            if (printLog) Log.d(bookSource.sourceUrl, "└" + nextUrlList.joinToString("，"))
        }
        return Pair(content, nextUrlList)
    }
}
