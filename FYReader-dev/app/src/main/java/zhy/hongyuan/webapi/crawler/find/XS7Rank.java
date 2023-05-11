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

package zhy.hongyuan.webapi.crawler.find;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import zhy.hongyuan.entity.bookstore.BookType;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.webapi.crawler.base.FindCrawler3;

/**
 * @author fengyue
 * @date 2020/11/28 22:43
 * 已失效
 */
@Deprecated
public class XS7Rank extends FindCrawler3 {
    private FindCrawler3 xs7 = new XS7FindCrawler();
    public static final String CHARSET = "GBK";
    public static final String FIND_NAME = "排行榜[小说旗]";
    public static final String FIND_URL = "https://www.xs7.la/top/lastupdate/1.html";
    @Override
    public String getCharset() {
        return xs7.getCharset();
    }

    @Override
    public String getFindName() {
        return FIND_NAME;
    }

    @Override
    public String getFindUrl() {
        return FIND_URL;
    }

    @Override
    public boolean hasImg() {
        return true;
    }

    @Override
    public boolean needSearch() {
        return false;
    }

    @Override
    public List<BookType> getBookTypes(String html) {
        List<BookType> bookTypes = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementsByClass("toplist").first();
        Elements as = div.getElementsByTag("a");
        for (Element a : as){
            BookType bookType = new BookType();
            bookType.setUrl(a.attr("href"));
            bookType.setTypeName(a.text());
            bookTypes.add(bookType);
        }
        return bookTypes;
    }

    @Override
    public List<Book> getFindBooks(String html, BookType bookType) {
        return xs7.getFindBooks(html, bookType);
    }

    @Override
    public boolean getTypePage(BookType curType, int page) {
        if (page != 1 && page > curType.getPageSize()){
            return true;
        }
        curType.setUrl(curType.getUrl().substring(0, curType.getUrl().lastIndexOf("/") + 1) + page + ".html");
        return false;
    }
}
