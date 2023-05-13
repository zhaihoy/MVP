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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import java.util.ArrayList;


public class ChaoXingReadCrawler extends BaseReadCrawler {
    public static final String NAME_SPACE = "http://yz4.chaoxing.com";
    public static final String NOVEL_SEARCH = "http://yz4.chaoxing.com/circlemarket/getsearch,start=0&size=25&sw={key}&channelId=52";
    public static final String CHAPTERS_URL = "https://special.zhexuezj.cn/mobile/mooc/tocourse/";
    public static final String DESC = "★★★     超星·出版     ★★★\n★★★   本书暂无简介  ★★★";
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
        Element divContent = doc.getElementById("contentBox");
        Elements ps = divContent.getElementsByTag("p");
        StringBuilder sb = new StringBuilder();
        for (Element p : ps){
            String content = Html.fromHtml(p.html()).toString();
            char c = 160;
            String spaec = "" + c;
            content = content.replace(spaec, "  ");
            sb.append(content);
            sb.append("\n");
        }
        return sb.toString();
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
        Element divList = doc.getElementsByClass("con").first();
        Elements elementsByTag = divList.getElementsByTag("a");
        int i = 0;
        for (Element a : elementsByTag) {
            String title = a.text();
            String url = a.attr("attr");
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
     * @param json
     * @return
     */
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String json) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        try {
            JSONArray booksArray = new JSONArray(json);
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject bookJson = booksArray.getJSONObject(i);
                Book book = new Book();
                book.setName(bookJson.getString("name"));
                book.setAuthor(bookJson.getString("author"));
                book.setImgUrl(bookJson.getString("coverUrl"));
                book.setNewestChapterTitle("");
                book.setChapterUrl(CHAPTERS_URL + bookJson.getInt("course_Id"));
                book.setDesc(DESC);
                book.setSource(LocalBookSource.chaoxing.toString());
                SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
                books.add(sbb, book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }


}
