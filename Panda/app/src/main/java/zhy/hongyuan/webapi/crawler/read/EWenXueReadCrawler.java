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

import java.util.ArrayList;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;

@Deprecated
public class EWenXueReadCrawler extends BaseReadCrawler implements BookInfoCrawler {
    public static final String NAME_SPACE = "http://ewenxue.org";
    public static final String NOVEL_SEARCH = "http://ewenxue.org/search.htm?keyword={key}";
    public static final String CHARSET = "GBK";
    public static final String SEARCH_CHARSET = "utf-8";

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
        return false;
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
        Element divContent = doc.getElementById("cContent");
        String content = Html.fromHtml(divContent.html()).toString();
        char c = 160;
        String spaec = "" + c;
        content = content.replace(spaec, "  ").replace("setFontSize();", "");
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
        String readUrl = doc.select(".breadcrumb").first()
                .select("a").last().attr("href");
        Element divList = doc.getElementById("chapters-list");
        String lastTile = null;
        int i = 0;
        Elements elementsByTag = divList.getElementsByTag("a");
        for (int j = 0; j < elementsByTag.size(); j++) {
            Element a = elementsByTag.get(j);
            String title = a.text();
            if (!StringHelper.isEmpty(lastTile) && title.equals(lastTile)) {
                continue;
            }
            Chapter chapter = new Chapter();
            chapter.setNumber(i++);
            chapter.setTitle(title);
            String url = readUrl + a.attr("href");
            chapter.setUrl(url);
            chapters.add(chapter);
            lastTile = title;
        }
        return chapters;
    }

    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return <li class="list-group-item clearfix">
     * 	<div class="col-xs-1"><i class="tag-blue">玄幻</i></div>
     * 	<div class="col-xs-3"><a href="/xs/163283/">大主宰</a></div>
     * 	<div class="col-xs-4"><a href="/xs/163283/56818254.htm">第一千三十二章 七阳截天杖</a></div>
     * 	<div class="col-xs-2">天蚕土豆</div>
     * 	<div class="col-xs-2"><span class="time">2019-07-05 16:03</span></div>
     * </li>
     */
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("clearfix");
        for (int i = 1; i < elements.size(); i++) {
            Element element = elements.get(i);
            Book book = new Book();
            Elements info = element.getElementsByTag("div");
            book.setName(info.get(1).text());
            book.setInfoUrl(info.get(1).getElementsByTag("a").attr("href"));
            book.setChapterUrl(book.getInfoUrl() + "mulu.htm");
            book.setAuthor(info.get(3).text());
            book.setNewestChapterTitle(info.get(2).text());
            book.setType(info.get(0).text());
            book.setSource(LocalBookSource.ewenxue.toString());
            SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
            books.add(sbb, book);
        }
        return books;
    }

    /**
     * 获取书籍详细信息
     *
     * @param book
     */
    public Book getBookInfo(String html, Book book) {
        Document doc = Jsoup.parse(html);
        Element img = doc.getElementsByClass("img-thumbnail").first();
        book.setImgUrl(img.attr("src"));
        Element desc = doc.getElementById("all");
        if (desc == null) {
            desc = doc.getElementById("shot");
        }
        book.setDesc(desc.text().replace("[收起]", ""));
        return book;
    }

}
