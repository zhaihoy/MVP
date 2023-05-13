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

package zhy.hongyuan.widget.page;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.observer.MyObserver;
import zhy.hongyuan.entity.Setting;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.service.ChapterService;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.webapi.BookApi;
import zhy.hongyuan.webapi.crawler.base.ReadCrawler;

public class NetPageLoader extends PageLoader {
    private static final String TAG = "PageFactory";
    private ChapterService mChapterService;
    private ReadCrawler mReadCrawler;
    private List<Chapter> loadingChapters = new CopyOnWriteArrayList<>();

    public NetPageLoader(PageView pageView, Book collBook, ChapterService mChapterService,
                         ReadCrawler mReadCrawler, Setting setting) {
        super(pageView, collBook, setting);
        this.mChapterService = mChapterService;
        this.mReadCrawler = mReadCrawler;
    }


    @Override
    public void refreshChapterList() {
        List<Chapter> chapters = mChapterService.findBookAllChapterByBookId(mCollBook.getId());
        if (chapters != null && !chapters.isEmpty()) {
            mChapterList = chapters;
            isChapterListPrepare = true;

            // 目录加载完成，执行回调操作。
            if (mPageChangeListener != null) {
                mPageChangeListener.onCategoryFinish(mChapterList);
            }

            // 如果章节未打开
            if (!isChapterOpen()) {
                // 打开章节
                openChapter();
            }
            return;
        }
        mStatus = STATUS_LOADING_CHAPTER;
        BookApi.getBookChapters(mCollBook, mReadCrawler)
                .flatMap((Function<List<Chapter>, ObservableSource<List<Chapter>>>) newChapters -> Observable.create(emitter -> {
                   for (Chapter chapter : newChapters){
                       chapter.setId(StringHelper.getStringRandom(25));
                       chapter.setBookId(mCollBook.getId());
                   }
                   mChapterService.addChapters(newChapters);
                   emitter.onNext(newChapters);
                }))
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new MyObserver<List<Chapter>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mChapterDis = d;
                    }

                    @Override
                    public void onNext(@NotNull List<Chapter> chapters) {
                        mChapterDis = null;
                        isChapterListPrepare = true;
                        mChapterList = chapters;
                        //提示目录加载完成
                        if (mPageChangeListener != null) {
                            mPageChangeListener.onCategoryFinish(mChapterList);
                        }
                        // 加载并显示当前章节
                        openChapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        error(STATUS_CATEGORY_ERROR, e.getLocalizedMessage());
                        Log.d(TAG, "file load error:" + e.toString());
                    }
                });
    }

    @Override
    public String getChapterReader(Chapter chapter) throws FileNotFoundException {
        /*File file = new File(APPCONST.BOOK_CACHE_PATH + mCollBook.getId()
                + File.separator + chapter.getTitle() + FileUtils.SUFFIX_FY);
        if (!file.exists()) return null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        return br;*/
        return mChapterService.getChapterCatheContent(chapter);
    }

    @Override
    public boolean hasChapterData(Chapter chapter) {
        return ChapterService.isChapterCached(chapter);
    }

    // 装载上一章节的内容
    @Override
    boolean parsePrevChapter() {
        boolean isRight = super.parsePrevChapter();

        if (mStatus == STATUS_FINISH) {
            loadPrevChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载当前章内容。
    @Override
    boolean parseCurChapter() {
        boolean isRight = super.parseCurChapter();

        if (mStatus == STATUS_FINISH) {
            loadPrevChapter();
            loadNextChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载下一章节的内容
    @Override
    boolean parseNextChapter() {
        boolean isRight = super.parseNextChapter();

        if (mStatus == STATUS_FINISH) {
            loadNextChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }

        return isRight;
    }

    /**
     * 加载当前页的前面一个章节
     */
    private void loadPrevChapter() {
        if (mPageChangeListener != null) {
            int end = mCurChapterPos;
            int begin = end - 1;
            if (begin < 0) {
                begin = 0;
            }
            requestChapters(begin, end);
        }
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private void loadCurrentChapter() {
        if (mPageChangeListener != null) {
            int begin = mCurChapterPos;
            int end = mCurChapterPos;

            // 是否当前不是最后一章
            if (end < mChapterList.size()) {
                end = end + 1;
                if (end >= mChapterList.size()) {
                    end = mChapterList.size() - 1;
                }
            }

            // 如果当前不是第一章
            if (begin != 0) {
                begin = begin - 1;
                if (begin < 0) {
                    begin = 0;
                }
            }
            requestChapters(begin, end);
        }
    }

    /**
     * 加载当前页的后四个章节
     */
    private void loadNextChapter() {
        if (mPageChangeListener != null) {

            // 提示加载后四章
            int begin = mCurChapterPos + 1;
            int end = begin + 3;

            // 判断是否大于最后一章
            if (begin >= mChapterList.size()) {
                // 如果下一章超出目录了，就没有必要加载了
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }
            requestChapters(begin, end);
        }
    }

    private void requestChapters(int start, int end) {
        // 检验输入值
        if (start < 0) {
            start = 0;
        }

        if (end >= mChapterList.size()) {
            end = mChapterList.size() - 1;
        }


        List<Chapter> chapters = new ArrayList<>();

        // 过滤，哪些数据已经加载了/正在加载
        for (int i = start; i <= end; ++i) {
            Chapter txtChapter = mChapterList.get(i);
            if (!hasChapterData(txtChapter) && !loadingChapters.contains(txtChapter)) {
                chapters.add(txtChapter);
            }
        }

        if (!chapters.isEmpty()) {
            loadingChapters.addAll(chapters);
            for (Chapter chapter : chapters) {
                getChapterContent(chapter);
            }
        }
    }

    /**
     * 加载章节内容
     *
     * @param chapter
     */
    public void getChapterContent(Chapter chapter) {
        BookApi.getChapterContent(chapter, mCollBook, mReadCrawler).flatMap(s -> Observable.create(emitter -> {
            loadingChapters.remove(chapter);
            String content = StringHelper.isEmpty(s) ? "章节内容为空" : s;
            mChapterService.saveOrUpdateChapter(chapter, content);
            emitter.onNext(content);
            emitter.onComplete();
        })).compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<Object>() {
            @Override
            public void onNext(@NotNull Object o) {
                if (isClose()) return;
                if (getPageStatus() == PageLoader.STATUS_LOADING && mCurChapterPos == chapter.getNumber()) {
                    if (isPrev) {
                        openChapterInLastPage();
                    } else {
                        openChapter();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                loadingChapters.remove(chapter);
                if (isClose()) return;
                if (mCurChapterPos == chapter.getNumber())
                    chapterError("请尝试重新加载或换源\n" + e.getLocalizedMessage());
                if (App.isDebug()) e.printStackTrace();
            }
        });
    }

}

