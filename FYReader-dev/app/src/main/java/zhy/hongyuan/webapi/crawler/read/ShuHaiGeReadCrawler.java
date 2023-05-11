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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;


public class ShuHaiGeReadCrawler extends BaseReadCrawler implements BookInfoCrawler {
    public static final String NAME_SPACE = "https://www.xinshuhaige.cc";
    public static final String NOVEL_SEARCH = "https://www.xinshuhaige.cc/search.html,searchkey={key}&searchtype=all";
    public static final String CHARSET = "utf-8";
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
        return true;
    }

    @Override
    public String getSearchCharset() {
        return SEARCH_CHARSET;
    }

    @Override
    public Map<String, String> getHeaders() {
        String cookie = "Hm_lvt_7729c158621a96ffef4197e08613177a=1642731311; Hm_lpvt_7729c158621a96ffef4197e08613177a=1642731315";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        return headers;
    }

    /**
     * 从html中获取章节正文
     *
     * @param html
     * @return
     */
    public String getContentFormHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element divContent = doc.getElementsByClass("content").first();
        StringBuilder sb = new StringBuilder();
        List<TextNode> textNodes = divContent.textNodes();
        for (int i = 1; i < textNodes.size() - 6; i++) {
            TextNode textNode = textNodes.get(i);
            sb.append(textNode.text());
            sb.append("\n");
        }
        String content = sb.toString();
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
        Element divList = doc.getElementsByClass("novel_list").last();
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
            String url = a.attr("href");
            chapter.setUrl(url);
            chapters.add(chapter);
            lastTile = title;
        }
        try {
            Element pages = doc.getElementById("pagelink");
            if (pages != null) {
                String nextPageUrl = "";
                Element nextPage = doc.selectFirst(".caption")
                        .selectFirst("a");
                if ("下一页".equals(nextPage.text())) {
                    nextPageUrl = NAME_SPACE + nextPage.attr("href");
                }
                if (!StringHelper.isEmpty(nextPageUrl)) {
                    List<Chapter> nextChapters = getChaptersFromHtml(OkHttpUtils.getHtml(nextPageUrl, CHARSET));
                    for (Chapter nextChapter : nextChapters) {
                        nextChapter.setNumber(chapters.size());
                        chapters.add(nextChapter);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return <li>
     * <span class="s1">[<a href="/dushi/">都市生活</a>]</span>
     * <span class="s2"><a href="/136610/" target="_blank">都市大主宰</a></span>
     * <span class="s3"><a href="/136610/4031638.html" target="_blank">第1462章　 相似</a></span>
     * <span class="s4">赌霸</span>
     * <span class="s5">2021-02-05</span>
     * <span class="s6">连载</span>
     * </li>
     */
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementsByClass("novelslist2").first();
        Elements elements = div.getElementsByTag("li");
        for (int i = 1; i < elements.size(); i++) {
            Element element = elements.get(i);
            Book book = new Book();
            Elements info = element.getElementsByTag("span");
            book.setName(info.get(1).text());
            book.setChapterUrl(info.get(1).getElementsByTag("a").attr("href"));
            book.setAuthor(info.get(3).text());
            book.setNewestChapterTitle(info.get(2).text());
            book.setType(info.get(0).getElementsByTag("a").first().text());
            book.setSource(LocalBookSource.shuhaige.toString());
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
        String img = doc.select("meta[property=og:image]").attr("content");
        book.setImgUrl(img);
        String desc = doc.select("meta[property=og:description]").attr("content");
        book.setDesc(desc.replaceAll("新书海阁.*观看小说:", ""));
        return book;
    }

}
