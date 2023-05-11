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

package zhy.hongyuan.webapi.crawler.read;

import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;

import java.util.ArrayList;

@Deprecated
public class ZuoPinReadCrawler extends BaseReadCrawler implements BookInfoCrawler {
    public static final String NAME_SPACE = "http://zuopinj.com";
    public static final String NOVEL_SEARCH = "http://so.zuopinj.com/search/index.php,tbname=bookname&show=title&tempid=3&keyboard={key}";
    public static final String CHARSET = "UTF-8";
    public static final String SEARCH_CHARSET = "UTF-8";

    @Override
    public String getSearchLink() {
        return NOVEL_SEARCH;
    }

    @Override
    public String getCharset() {
        return CHARSET;
    }

    @Override
    public String getNameSpace() {
        return NAME_SPACE;
    }

    @Override
    public Boolean isPost() {
        return true;
    }

    @Override
    public String getSearchCharset() {
        return SEARCH_CHARSET;
    }

    /**
     * 从html中获取章节正文
     *
     * @param html
     * @return
     */
    public String getContentFormHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element divContent = doc.getElementById("htmlContent");
        String content = Html.fromHtml(divContent.html()).toString();
        char c = 160;
        String spaec = "" + c;
        content = content.replace(spaec, "  ");
        return content;
    }

    /**
     * 从html中获取章节列表
     *
     * @param html
     * @return
     */
    public ArrayList<Chapter> getChaptersFromHtml(String html) {
        ArrayList<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element divList = doc.getElementsByClass("book_list").first();
        Elements elementsByTag = divList.getElementsByTag("a");
        int i = 0;
        for (Element a : elementsByTag) {
            String title = a.text();
            String url = a.attr("href");
            Chapter chapter = new Chapter();
            chapter.setNumber(i++);
            chapter.setTitle(title);
            chapter.setUrl(url);
            chapters.add(chapter);
        }
        return chapters;
    }

    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return
     */
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementById("J_TableContainer");
        Elements elementsByClass = div.getElementsByClass("search-bookele");
        for (Element element : elementsByClass) {
            Book book = new Book();
            book.setName(element.getElementsByTag("h3").get(0).text());
            book.setChapterUrl(element.getElementsByTag("a").attr("href"));
            book.setAuthor("");
            book.setNewestChapterTitle("");
            book.setImgUrl(element.getElementsByTag("img").first().attr("src"));
            book.setDesc(element.getElementsByTag("p").first().text());
            book.setSource(LocalBookSource.zuopin.toString());
            SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
            books.add(sbb, book);
        }
        return books;
    }

    @Override
    public Book getBookInfo(String html, Book book) {
        Document doc = Jsoup.parse(html);
        Element info = doc.getElementsByClass("infos").first();
        String author = info.getElementsByTag("span").get(0).text();
        author = author.substring(author.indexOf("作者：") + 3, author.indexOf("最后"));
        book.setAuthor(author);
        String newestChapter = doc.getElementsByClass("upd").first().getElementsByTag("a").first().text();
        book.setNewestChapterTitle(newestChapter);
        book.setDesc(doc.getElementsByTag("p").first().text());
        return book;
    }

}
