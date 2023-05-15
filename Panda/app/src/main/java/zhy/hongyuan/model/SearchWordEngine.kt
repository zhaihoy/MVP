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

package zhy.hongyuan.model

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import xyz.fycz.myreader.R
import zhy.hongyuan.application.App
import zhy.hongyuan.greendao.entity.search.SearchWord1
import zhy.hongyuan.greendao.entity.search.SearchWord2
import zhy.hongyuan.greendao.entity.Book
import zhy.hongyuan.greendao.entity.Chapter
import zhy.hongyuan.greendao.service.ChapterService
import zhy.hongyuan.util.SharedPreUtils
import zhy.hongyuan.util.ToastUtils
import zhy.hongyuan.util.help.ChapterContentHelp
import zhy.hongyuan.widget.page.PageLoader
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author  hongyuan
 * @date 2021/12/5 21:17
 */
class SearchWordEngine(
    private val book: Book,
    private val chapters: List<Chapter>,
    private val pageLoader: PageLoader
) {
    private val TAG = "SearchWordEngine"

    //线程池
    private var executorService: ExecutorService

    private var scheduler: Scheduler
    private var compositeDisposable: CompositeDisposable
    private lateinit var searchListener: OnSearchListener
    private val threadsNum =
        SharedPreUtils.getInstance().getInt(App.getmContext().getString(R.string.threadNum), 8)
    private var searchSiteIndex = 0
    private var searchSuccessNum = 0
    private var searchFinishNum = 0
    private var isLocalBook = false

    fun setOnSearchListener(searchListener: OnSearchListener) {
        this.searchListener = searchListener
    }

    init {
        executorService = Executors.newFixedThreadPool(threadsNum)
        scheduler = Schedulers.from(executorService)
        compositeDisposable = CompositeDisposable()
    }

    fun stopSearch() {
        compositeDisposable.dispose()
        compositeDisposable = CompositeDisposable()
        searchListener.loadFinish(searchSuccessNum == 0)
    }

    /**
     * 关闭引擎
     */
    fun closeSearchEngine() {
        executorService.shutdown()
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    /**
     * 搜索关键字(模糊搜索)
     *
     * @param keyword
     */
    fun search(keyword: String) {
        if ("本地书籍" == book.type) {
            isLocalBook = true
            if (!File(book.chapterUrl).exists()) {
                ToastUtils.showWarring("当前书籍源文件不存在，无法搜索！")
                searchListener.loadFinish(true)
                return
            }
        }
        if (chapters.isEmpty()) {
            ToastUtils.showWarring("当前书籍章节目录为空，无法搜索！")
            searchListener.loadFinish(true)
            return
        }
        searchSuccessNum = 0
        searchSiteIndex = -1
        searchFinishNum = 0
        for (i in 0 until Math.min(threadsNum, chapters.size)) {
            searchOnEngine(keyword)
        }
    }

    @Synchronized
    private fun searchOnEngine(keyword: String) {
        searchSiteIndex++
        if (searchSiteIndex < chapters.size) {
            val chapterNum = searchSiteIndex
            val chapter = chapters[chapterNum]
            Observable.create(ObservableOnSubscribe<SearchWord1> { emitter ->
                val searchWord1 = SearchWord1(
                    bookId = book.id,
                    chapterNum = chapterNum,
                    chapterTitle = chapter.title,
                    searchWord2List = mutableListOf()
                )
                if (!isLocalBook && !ChapterService.isChapterCached(chapter)) {
                    emitter.onNext(searchWord1)
                    return@ObservableOnSubscribe
                }
                var content = chapter.title + "\n" + pageLoader.getChapterReader(chapter)
                content = pageLoader.contentHelper.replaceContent(
                    book.name + "-" + book.author,
                    book.source,
                    content,
                    true
                )
                if (book.reSeg) {
                    content = ChapterContentHelp.LightNovelParagraph2(content, chapter.title)
                }
                val allLine: List<String> = content.split("\n")
                var count = 0
                var blockPos = 0
                allLine.forEach {
                    var index: Int = -1
                    while (it.indexOf(keyword, index + 1).also { index = it } != -1) {
                        blockPos++
                        var leftI = 0
                        var rightI = it.length
                        var leftS = ""
                        var rightS = ""
                        if (leftI < index - 20) {
                            leftI = index - 20
                            leftS = "..."
                        }
                        if (rightI > index + keyword.length + 20) {
                            rightI = index + keyword.length + 20
                            rightS = "..."
                        }
                        val str = leftS + it.substring(leftI, rightI) + rightS
                        val searchWord2 =
                            SearchWord2(
                                keyword,
                                chapterNum,
                                str,
                                index - leftI + leftS.length,
                                index,
                                count
                            )
                        searchWord1.searchWord2List.add(searchWord2)
                        count++
                        //当添加的block太多的时候，执行GC
                        if (blockPos % 15 == 0) {
                            System.gc()
                            System.runFinalization()
                        }
                    }
                }
                emitter.onNext(searchWord1)
                emitter.onComplete()
            }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<SearchWord1?> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(searchWord1: SearchWord1) {
                        searchFinishNum++
                        if (searchWord1.searchWord2List.isNotEmpty()) {
                            searchSuccessNum++
                            searchListener.loadMore(searchWord1)
                        }
                        searchOnEngine(keyword)
                    }

                    override fun onError(e: Throwable) {
                        searchFinishNum++
                        searchOnEngine(keyword)
                        if (App.isDebug()) e.printStackTrace()
                    }

                    override fun onComplete() {
                    }

                })
        } else {
            if (searchFinishNum == chapters.size) {
                if (searchSuccessNum == 0) {
                    ToastUtils.showWarring("搜索结果为空")
                    searchListener.loadFinish(true)
                } else {
                    searchListener.loadFinish(false)
                }
            }
        }
    }

    interface OnSearchListener {
        fun loadFinish(isEmpty: Boolean)
        fun loadMore(item: SearchWord1)
    }
}