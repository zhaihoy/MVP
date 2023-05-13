/*
 * This file is part of FYReader.
 * FYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package zhy.hongyuan.webapi;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.model.third2.analyzeRule.AnalyzeUrl;
import zhy.hongyuan.model.third2.content.BookChapterList;
import zhy.hongyuan.model.third2.content.BookContent;
import zhy.hongyuan.model.third2.content.BookInfo;
import zhy.hongyuan.model.third2.content.BookList;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.webapi.crawler.source.Third3Crawler;
import zhy.hongyuan.webapi.crawler.source.ThirdCrawler;
import zhy.hongyuan.webapi.crawler.source.find.Third3FindCrawler;
import zhy.hongyuan.webapi.crawler.source.find.ThirdFindCrawler;

import static zhy.hongyuan.common.APPCONST.JS_PATTERN;
import static zhy.hongyuan.util.utils.OkHttpUtils.getCookies;

/**
 * @author  hongyuan
 * @date 2021/5/15 9:56
 */
public class ThirdSourceApi {

    /**
     * 第三方书源搜索
     *
     * @param key
     * @param rc
     * @return
     */
    protected static Observable<ConMVMap<SearchBookBean, Book>> searchByTC(String key, final ThirdCrawler rc) {
        return searchByTC(key, rc, null);
    }
    protected static Observable<ConMVMap<SearchBookBean, Book>> searchByTC(String key, final ThirdCrawler rc, ExecutorService searchPool) {
        if (rc instanceof Third3Crawler) {
            return Third3SourceApi.INSTANCE.searchByT3C(key, (Third3Crawler) rc, searchPool);
        }
        try {
            Map<String, String> headers = rc.getHeaders();
            headers.putAll(getCookies(rc.getNameSpace()));
            BookSource source = rc.getSource();
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(source.getSearchRule().getSearchUrl(),
                    key, 1, headers, source.getSourceUrl());
            BookList bookList = new BookList(source.getSourceUrl(), source.getSourceName(), source, false);
            return OkHttpUtils.getStrResponse(analyzeUrl).flatMap(bookList::analyzeSearchBook)
                    .flatMap((Function<List<Book>, ObservableSource<ConMVMap<SearchBookBean, Book>>>) books -> Observable.create((ObservableOnSubscribe<ConMVMap<SearchBookBean, Book>>) emitter -> {
                        emitter.onNext(rc.getBooks(books));
                        emitter.onComplete();
                    }));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    protected static Observable<Book> getBookInfoByTC(Book book, ThirdCrawler rc) {
        if (rc instanceof Third3Crawler) {
            return Third3SourceApi.INSTANCE.getBookInfoByT3C(book, (Third3Crawler) rc);
        }
        BookSource source = rc.getSource();
        BookInfo bookInfo = new BookInfo(source.getSourceUrl(), source.getSourceName(), source);
        if (!TextUtils.isEmpty(book.getCathe("BookInfoHtml"))) {
            return bookInfo.analyzeBookInfo(book.getCathe("BookInfoHtml"), book);
        }
        try {
            Map<String, String> headers = rc.getHeaders();
            headers.putAll(getCookies(rc.getNameSpace()));
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(book.getInfoUrl(), headers, source.getSourceUrl());
            return OkHttpUtils.getStrResponse(analyzeUrl)
                    .flatMap(response -> OkHttpUtils.setCookie(response, source.getSourceUrl()))
                    .flatMap(response -> bookInfo.analyzeBookInfo(response.body(), book));
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", book.getInfoUrl())));
        }
    }

    protected static Observable<List<Chapter>> getBookChaptersByTC(Book book, ThirdCrawler rc) {
        if (rc instanceof Third3Crawler) {
            return Third3SourceApi.INSTANCE.getBookChaptersByT3C(book, (Third3Crawler) rc);
        }
        BookSource source = rc.getSource();
        Map<String, String> headers = rc.getHeaders();
        headers.putAll(getCookies(rc.getNameSpace()));
        BookChapterList bookChapterList = new BookChapterList(source.getSourceUrl(), source, true);
        if (!TextUtils.isEmpty(book.getCathe("ChapterListHtml"))) {
            return bookChapterList.analyzeChapterList(book.getCathe("ChapterListHtml"), book, headers)
                    .flatMap(chapters -> Observable.create(emitter -> {
                        for (int i = 0; i < chapters.size(); i++) {
                            Chapter chapter = chapters.get(i);
                            chapter.setNumber(i);
                        }
                        emitter.onNext(chapters);
                        emitter.onComplete();
                    }));
        }
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(book.getChapterUrl(), headers, book.getInfoUrl());
            return OkHttpUtils.getStrResponse(analyzeUrl)
                    .flatMap(response -> OkHttpUtils.setCookie(response, source.getSourceUrl()))
                    .flatMap(response -> bookChapterList.analyzeChapterList(response.body(), book, headers))
                    .flatMap(chapters -> Observable.create(emitter -> {
                        for (int i = 0; i < chapters.size(); i++) {
                            Chapter chapter = chapters.get(i);
                            chapter.setNumber(i);
                        }
                        emitter.onNext(chapters);
                        emitter.onComplete();
                    }));
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", book.getChapterUrl())));
        }
    }

    protected static Observable<String> getChapterContentByTC(Chapter chapter, Book book, ThirdCrawler rc) {
        if (rc instanceof Third3Crawler) {
            return Third3SourceApi.INSTANCE.getChapterContentByT3C(chapter, book, (Third3Crawler) rc);
        }
        BookSource source = rc.getSource();
        Map<String, String> headers = rc.getHeaders();
        headers.putAll(getCookies(rc.getNameSpace()));
        BookContent bookContent = new BookContent(source.getSourceUrl(), source);
        if (Objects.equals(chapter.getUrl(), book.getChapterUrl())
                && !TextUtils.isEmpty(book.getCathe("ChapterListHtml"))) {
            return bookContent.analyzeBookContent(book.getCathe("ChapterListHtml"), chapter, null, book, headers);
        }
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(chapter.getUrl(), headers, book.getChapterUrl());
            String contentRule = source.getContentRule().getContent();
            if (contentRule.startsWith("$") && !contentRule.startsWith("$.")) {
                //动态网页第一个js放到webView里执行
                contentRule = contentRule.substring(1);
                String js = null;
                Matcher jsMatcher = JS_PATTERN.matcher(contentRule);
                if (jsMatcher.find()) {
                    js = jsMatcher.group();
                    if (js.startsWith("<js>")) {
                        js = js.substring(4, js.lastIndexOf("<"));
                    } else {
                        js = js.substring(4);
                    }
                }
                return OkHttpUtils.getAjaxString(analyzeUrl, source.getSourceUrl(), js)
                        .flatMap(response -> bookContent.analyzeBookContent(response, chapter, null, book, headers));
            } else {
                return OkHttpUtils.getStrResponse(analyzeUrl)
                        .flatMap(response -> OkHttpUtils.setCookie(response, source.getSourceUrl()))
                        .flatMap(response -> bookContent.analyzeBookContent(response, chapter, null, book, headers));
            }
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("url错误:%s", chapter.getUrl())));
        }
    }
    /**
     * 发现
     */
    public static Observable<List<Book>> findBook(String url, ThirdFindCrawler fc, int page) {
        if (fc instanceof Third3FindCrawler){
            return Third3SourceApi.INSTANCE.findBook(url, (Third3FindCrawler) fc, page);
        }
        BookSource source = fc.getSource();
        Map<String, String> headers = getCookies(fc.getTag());
        BookList bookList = new BookList(source.getSourceUrl(), source.getSourceName(), source, true);
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(url, null, page, headers, source.getSourceUrl());
            return OkHttpUtils.getStrResponse(analyzeUrl)
                    .flatMap(response -> OkHttpUtils.setCookie(response, source.getSourceUrl()))
                    .flatMap(bookList::analyzeSearchBook);
        } catch (Exception e) {
            return Observable.error(new Throwable(String.format("%s错误:%s", url, e.getLocalizedMessage())));
        }
    }
}
