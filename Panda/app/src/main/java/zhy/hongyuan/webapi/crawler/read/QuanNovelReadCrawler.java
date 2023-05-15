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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;

import java.util.ArrayList;


public class QuanNovelReadCrawler extends BaseReadCrawler implements BookInfoCrawler {
    public static final String NAME_SPACE = "https://qxs.la";
    public static final String NOVEL_SEARCH = "https://qxs.la/s_{key}";
    public static final String CHARSET = "GBK";
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
        Element divContent = doc.getElementById("content");
        StringBuilder sb = new StringBuilder();
        for (TextNode textNode : divContent.textNodes()) {
            sb.append(textNode.text());
            sb.append("\n");
        }
        String content = sb.toString();
        char c = 160;
        String spaec = "" + c;
        content = content.replace(spaec, "  ").replaceAll("\\s*您可以.*最.*章节.*", "");
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
        Element divList = doc.getElementsByClass("chapters").first();
        Elements elementsByClass = divList.getElementsByClass("chapter");
        int i = 0;
        for (Element div : elementsByClass) {
            Element a = div.getElementsByTag("a").first();
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
        try {
            Document doc = Jsoup.parse(html);
            Elements divs = doc.getElementsByClass("main list");
            Element div = divs.get(0);
            Elements elementsByTag = div.getElementsByTag("ul");
            for (int i = 1; i < elementsByTag.size(); i++) {
                Element element = elementsByTag.get(i);
                Book book = new Book();
                Element info = element.getElementsByClass("cc2").first();
                book.setName(info.getElementsByTag("a").first().text());
                book.setChapterUrl(info.getElementsByTag("a").first().attr("href"));
                book.setAuthor(element.getElementsByClass("cc4").first().getElementsByTag("a").first().text());
                book.setNewestChapterTitle(element.getElementsByClass("cc3").first().getElementsByTag("a").first().text());
                book.setSource(LocalBookSource.quannovel.toString());
                SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
                books.add(sbb, book);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        String type = doc.getElementsByClass("f_l t_r w3").text().replace("类型：", "");
        book.setType(type);
        String desc = doc.getElementsByClass("desc").text().replace("简介：", "");
        book.setDesc(desc);

        return book;
    }

}
