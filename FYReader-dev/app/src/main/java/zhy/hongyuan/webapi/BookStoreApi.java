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


import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import zhy.hongyuan.base.observer.MySingleObserver;
import zhy.hongyuan.entity.bookstore.BookType;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.webapi.crawler.base.FindCrawler3;

public class BookStoreApi {


    /**
     * 获取书城小说分类列表
     * @param findCrawler3
     * @param callback
     */
    public static void getBookTypeList(FindCrawler3 findCrawler3, final ResultCallback callback){
        Single.create((SingleOnSubscribe<List<BookType>>) emitter -> {
            String html = OkHttpUtils.getHtml(findCrawler3.getFindUrl(), findCrawler3.getCharset());
            emitter.onSuccess(findCrawler3.getBookTypes(html));
        }).compose(RxUtils::toSimpleSingle).subscribe(new MySingleObserver<List<BookType>>() {
            @Override
            public void onSuccess(@NotNull List<BookType> bookTypes) {
                callback.onFinish(bookTypes, 0);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError((Exception) e);
            }
        });
    }


    /**
     * 获取某一分类小说排行榜列表
     * @param findCrawler3
     * @param callback
     */
    public static void getBookRankList(BookType bookType, FindCrawler3 findCrawler3, final ResultCallback callback){
        Single.create((SingleOnSubscribe<List<Book>>) emitter -> {
            String html = OkHttpUtils.getHtml(bookType.getUrl(), findCrawler3.getCharset());
            emitter.onSuccess(findCrawler3.getFindBooks(html, bookType));
        }).compose(RxUtils::toSimpleSingle).subscribe(new MySingleObserver<List<Book>>() {
            @Override
            public void onSuccess(@NotNull List<Book> books) {
                callback.onFinish(books, 0);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError((Exception) e);
            }
        });
    }

}
