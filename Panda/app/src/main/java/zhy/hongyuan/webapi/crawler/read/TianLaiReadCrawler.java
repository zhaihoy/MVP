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
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;

import java.util.ArrayList;

/**
 * 天籁小说网html解析工具
 */

public class TianLaiReadCrawler extends BaseReadCrawler {
    public static final String NAME_SPACE = "https://xs.23sk.com";
    public static final String NOVEL_SEARCH = "https://xs.23sk.com/search.php?q={key}";
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
        Element divContent = doc.getElementById("content");
        String content = Html.fromHtml(divContent.html()).toString();
        char c = 160;
        String spaec = "" + c;
        content = content.replace(spaec, "  ");
        content = content.replaceAll("笔趣阁.*最新章节！", "");
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
        String readUrl = doc.select("meta[property=og:novel:read_url]").attr("content");
        Element divList = doc.getElementById("list");
        Element dl = divList.getElementsByTag("dl").get(0);
        String lastTile = null;
        int i = 0;
        for (Element dd : dl.getElementsByTag("dd")) {
            Elements as = dd.getElementsByTag("a");
            if (as.size() > 0) {
                Element a = as.get(0);
                String title = a.html();
                if (!StringHelper.isEmpty(lastTile) && title.equals(lastTile)) {
                    continue;
                }
                Chapter chapter = new Chapter();
                chapter.setNumber(i++);
                chapter.setTitle(title);
                String url = a.attr("href");
                if (url.contains("files/article/html")) {
                    url = url;
                } else {
                    url = readUrl + url;
                }
                chapter.setUrl(url);
                chapters.add(chapter);
                lastTile = title;
            }

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
//        Element node = doc.getElementById("results");
//        for (Element div : node.children()) {
        Elements divs = doc.getElementsByClass("result-list");
        Element div = divs.get(0);
//        if (!StringHelper.isEmpty(div.className()) && div.className().equals("result-list")) {
        for (Element element : div.children()) {
            Book book = new Book();
            Element img = element.child(0).child(0).child(0);
            book.setImgUrl(img.attr("src"));
            Element title = element.getElementsByClass("result-item-title result-game-item-title").get(0);
            book.setName(title.child(0).attr("title"));
            book.setChapterUrl(title.child(0).attr("href"));
            Element desc = element.getElementsByClass("result-game-item-desc").get(0);
            book.setDesc(desc.text());
            Element info = element.getElementsByClass("result-game-item-info").get(0);
            for (Element element1 : info.children()) {
                String infoStr = element1.text();
                if (infoStr.contains("作者：")) {
                    book.setAuthor(infoStr.replace("作者：", "").replace(" ", ""));
                } else if (infoStr.contains("类型：")) {
                    book.setType(infoStr.replace("类型：", "").replace(" ", ""));
                } else if (infoStr.contains("更新时间：")) {
                    book.setUpdateDate(infoStr.replace("更新时间：", "").replace(" ", ""));
                } else {
                    Element newChapter = element1.child(1);
                    book.setNewestChapterTitle(newChapter.text());
                }
            }
            book.setSource(LocalBookSource.tianlai.toString());
            SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
            books.add(sbb, book);
        }

        return books;
    }

}
