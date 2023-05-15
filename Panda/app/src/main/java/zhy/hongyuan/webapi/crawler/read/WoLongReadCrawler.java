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
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;


public class WoLongReadCrawler extends BaseReadCrawler {
    public static final String NAME_SPACE = "http://www.paper027.com";
    public static final String NOVEL_SEARCH = "http://www.paper027.com/search.html?keyword={key}";
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
        Element divContent = doc.getElementById("contentsource");
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
        Element divList = doc.getElementsByClass("chapters").get(0);
        Elements elementsByTag = divList.getElementsByTag("a");
        int j = 0;
        for (int i = elementsByTag.size() - 1; i >= 0; i--) {
            Element a = elementsByTag.get(i);
            String title = a.text();
            String url = a.attr("href");
            Chapter chapter = new Chapter();
            chapter.setNumber(j++);
            chapter.setTitle(title);
            chapter.setUrl(url);
            chapters.add(chapter);
        }
        return chapters;
    }

    /**
     * 从搜索html中得到书列表
     * <div>
     * <a href="http://www.paper027.com/novel/75432.html" target="_blank"><img class="img-rounded" src="http://www.paper027.com/uploads/novel/20190802/9883298e2e72ecfa53fabf0ef2e03e21.jpg"/></a>
     * <h2><a  href="http://www.paper027.com/novel/75432.html" target="_blank">大主宰之混子日常</a></h2>
     * <p class="text-muted"><span>錯過过错</span> <small>2019-08-06 19:14</small></p>
     * </div>
     * <div class="clearfix searchresult-info">
     * <p><a href="http://www.paper027.com/novel/75432.html" target="_blank">作者很懒，什么也没有留下。...</a></p>
     * <ul class="list-inline text-muted">
     * <li>11635人看过</li>
     * <li>标签：</li>
     * <li><a class="text-warning">同人衍生</a></li>
     * </ul>
     * </div>
     */
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        Document doc = Jsoup.parse(html);
        Elements elementsByTag = doc.getElementsByClass("searchresult");
        for (int i = 0; i < elementsByTag.size(); i++) {
            Element element = elementsByTag.get(i);
            Elements as = element.getElementsByTag("a");
            Elements ps = element.getElementsByTag("p");
            Book book = new Book();
            book.setImgUrl(element.getElementsByTag("img").attr("src"));
            book.setName(as.get(1).text());
            book.setAuthor(ps.get(0).getElementsByTag("span").get(0).text());
            book.setType(as.get(3).text());
            book.setChapterUrl(as.get(1).attr("href").replace("novel", "home/chapter/lists/id"));
            book.setDesc(as.get(2).text());
            book.setNewestChapterTitle("");
            book.setSource(LocalBookSource.wolong.toString());
            SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
            books.add(sbb, book);
        }
        return books;
    }

}
