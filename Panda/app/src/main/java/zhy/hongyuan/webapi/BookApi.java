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

package zhy.hongyuan.webapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import zhy.hongyuan.entity.FindKind;
import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.entity.StrResponse;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;
import zhy.hongyuan.webapi.crawler.base.FindCrawler;
import zhy.hongyuan.webapi.crawler.base.ReadCrawler;
import zhy.hongyuan.webapi.crawler.read.TianLaiReadCrawler;
import zhy.hongyuan.webapi.crawler.source.ThirdCrawler;
import zhy.hongyuan.webapi.crawler.source.find.ThirdFindCrawler;

import static zhy.hongyuan.util.utils.OkHttpUtils.getCookies;


public class BookApi {


    /**
     * 搜索小说
     *
     * @param key
     */
    public static Observable<ConMVMap<SearchBookBean, Book>> search(String key, final ReadCrawler rc) {
        return search(key, rc, null);
    }

    public static Observable<ConMVMap<SearchBookBean, Book>> search(String key, final ReadCrawler rc, ExecutorService searchPool) {
        if (rc instanceof ThirdCrawler) {
            return ThirdSourceApi.searchByTC(key, (ThirdCrawler) rc, searchPool);
        }
        String charset = "utf-8";
        if (rc instanceof TianLaiReadCrawler) {
            charset = "utf-8";
        } else {
            charset = rc.getCharset();
        }
        if (rc.getSearchCharset() != null && rc.getSearchCharset().toLowerCase().equals("gbk")) {
            try {
                key = URLEncoder.encode(key, rc.getSearchCharset());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String finalCharset = charset;
        String finalKey = key;
        return Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
            Map<String, String> headers = rc.getHeaders();
            headers.putAll(getCookies(rc.getNameSpace()));
            if (rc.isPost()) {
                String url = rc.getSearchLink();
                String[] urlInfo = url.split(",");
                url = urlInfo[0];
                String body = makeSearchUrl(urlInfo[1], finalKey);
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody requestBody = RequestBody.create(mediaType, body);
                emitter.onNext(OkHttpUtils.getStrResponse(url, requestBody, finalCharset, headers));
            } else {
                emitter.onNext(OkHttpUtils.getStrResponse(makeSearchUrl(rc.getSearchLink(), finalKey), finalCharset, headers));
            }
            emitter.onComplete();
        }).flatMap(response -> OkHttpUtils.setCookie(response, rc.getNameSpace())).flatMap(rc::getBooksFromStrResponse);
    }

    public static String makeSearchUrl(String url, String key) {
        return url.replace("{key}", key);
    }


    /**
     * 获取小说详细信息
     *
     * @param book
     */
    public static Observable<Book> getBookInfo(final Book book, final BookInfoCrawler bic) {
        if (bic instanceof ThirdCrawler) {
            return ThirdSourceApi.getBookInfoByTC(book, (ThirdCrawler) bic);
        }
        String url;
        url = book.getInfoUrl();
        url = NetworkUtils.getAbsoluteURL(bic.getNameSpace(), url);
        String finalUrl = url;
        return Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
            Map<String, String> headers = ((ReadCrawler) bic).getHeaders();
            headers.putAll(getCookies(bic.getNameSpace()));
            emitter.onNext(OkHttpUtils.getStrResponse(finalUrl, bic.getCharset(), headers));
            emitter.onComplete();
        }).flatMap(response -> OkHttpUtils.setCookie(response, bic.getNameSpace())).flatMap(response -> bic.getBookInfo(response, book));
    }

    /**
     * 获取章节列表
     *
     * @param book
     */
    public static Observable<List<Chapter>> getBookChapters(Book book, final ReadCrawler rc) {
        if (rc instanceof ThirdCrawler) {
            if (StringHelper.isEmpty(book.getChapterUrl())) {
                return ThirdSourceApi.getBookInfoByTC(book, (ThirdCrawler) rc)
                        .flatMap(book1 -> ThirdSourceApi.getBookChaptersByTC(book, (ThirdCrawler) rc));
            } else {
                return ThirdSourceApi.getBookChaptersByTC(book, (ThirdCrawler) rc);
            }
        }
        String url = book.getChapterUrl();
        if (StringHelper.isEmpty(url)) url = book.getInfoUrl();
        String charset = rc.getCharset();
        url = NetworkUtils.getAbsoluteURL(rc.getNameSpace(), url);
        String finalUrl = url;
        return Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
            Map<String, String> headers = rc.getHeaders();
            headers.putAll(getCookies(rc.getNameSpace()));
            emitter.onNext(OkHttpUtils.getStrResponse(finalUrl, charset, headers));
            emitter.onComplete();
        }).flatMap(response -> OkHttpUtils.setCookie(response, rc.getNameSpace())).flatMap(rc::getChaptersFromStrResponse);
    }


    /**
     * 获取章节正文
     */
    public static Observable<String> getChapterContent(Chapter chapter, Book book, final ReadCrawler rc) {
        if (rc instanceof ThirdCrawler) {
            return ThirdSourceApi.getChapterContentByTC(chapter, book, (ThirdCrawler) rc);
        }
        String url = chapter.getUrl();
        String charset = rc.getCharset();
        url = NetworkUtils.getAbsoluteURL(rc.getNameSpace(), url);
        String finalUrl = url;
        return Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
            Map<String, String> headers = rc.getHeaders();
            headers.putAll(getCookies(rc.getNameSpace()));
            emitter.onNext(OkHttpUtils.getStrResponse(finalUrl, charset, headers));
            emitter.onComplete();
        }).flatMap(response -> OkHttpUtils.setCookie(response, rc.getNameSpace())).flatMap(rc::getContentFormStrResponse);
    }


    public static Observable<List<Book>> findBooks(FindKind kind, FindCrawler findCrawler, int page) {
        if (findCrawler instanceof ThirdFindCrawler) {
            return ThirdSourceApi.findBook(kind.getUrl(), (ThirdFindCrawler) findCrawler, page);
        }
        if (kind.getMaxPage() > 0 && page > kind.getMaxPage())
            return Observable.just(Collections.EMPTY_LIST);
        String url = kind.getUrl().replace("{page}", page + "");
        return Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
            emitter.onNext(OkHttpUtils.getStrResponse(url, null, null));
            emitter.onComplete();
        }).flatMap(response -> OkHttpUtils.setCookie(response, findCrawler.getTag())).flatMap((StrResponse strResponse) -> findCrawler.getFindBooks(strResponse, kind));
    }
}
