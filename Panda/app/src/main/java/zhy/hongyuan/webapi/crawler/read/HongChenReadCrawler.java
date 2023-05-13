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

import java.util.ArrayList;
import java.util.Collections;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.webapi.crawler.base.BaseReadCrawler;

@Deprecated
public class HongChenReadCrawler extends BaseReadCrawler {
    public static final String NAME_SPACE = "https://www.zuxs.net";
    public static final String NOVEL_SEARCH = "https://www.zuxs.net/search.php?key={key}";
    public static final String CHARSET = "gb2312";
    public static final String SEARCH_CHARSET = "gbk";

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
        Element divContent = doc.getElementById("txt");
        Elements aDiv = divContent.getElementsByTag("dd");
        StringBuilder sb = new StringBuilder();
        Collections.sort(aDiv, (o1, o2) -> Integer.parseInt(o1.attr("data-id")) -
                Integer.parseInt(o2.attr("data-id")));
        for (int i = 0; i < aDiv.size(); i++) {
            Element dd = aDiv.get(i);
            if (i == aDiv.size() - 1) break;
            sb.append(Html.fromHtml(dd.html()).toString());
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
        String readUrl = doc.select("meta[property=og:novel:read_url]").attr("content");
        Element divList = doc.getElementById("listsss");
        Elements elementsByTag = divList.getElementsByTag("a");
        int i = 0;
        for (int j = 0; j < elementsByTag.size(); j++) {
            Element a = elementsByTag.get(j);
            String title = a.text();
            String url = a.attr("href");
            Chapter chapter = new Chapter();
            chapter.setNumber(i++);
            chapter.setTitle(title);
            chapter.setUrl(readUrl + url);
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
    /*
		<dl><dt><a href="/zu/1140.html" target="_blank"><img class="lazyimg" data-original="https://www.zuxs.net/files/article/image/0/29/29s.jpg"></a></dt>
			<dd><a href="/zu/1140.html" class="bigpic-book-name" target="_blank">
					<font style="font-weight:bold;color:#f00">大主宰</font>
				</a>
				<p><a href="/author/%CC%EC%B2%CF%CD%C1%B6%B9.html" target="_blank">天蚕土豆</a> | <a href="/top/8_1.html"
					 target="_blank">其他类型</a> | 连载中</p>
				<p class="big-book-info"> 大千世界，位面交汇，万族林立，群雄荟萃，一位位来自下位面的天之至尊，在这无尽世界，演绎着令人向往的传奇，追求着那主宰之路。
					无尽火域，炎帝执掌，万火焚苍穹。
					武境之内，武祖之威……</p>
				<p><a href="/zu/1/1140/5284.html" target="_blank" class="red">最近更新 第1598章 邪神陨落（大结局）</a><span>| 09-06
						05:54更新</span></p>
			</dd>
		</dl>
     */
    @Deprecated
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        Document doc = Jsoup.parse(html);
//        try {
        Element div = doc.getElementsByClass("s-b-list").first();
        Elements dls = div.getElementsByTag("dl");
        for (Element dl : dls) {
            Elements as = dl.getElementsByTag("a");
            Book book = new Book();
            book.setName(as.get(1).text());
            book.setAuthor(as.get(2).text());
            book.setType(as.get(3).text());
            book.setNewestChapterTitle(as.get(4).text().replace("最近更新 ", ""));
            book.setDesc(dl.getElementsByClass("big-book-info").first().text());
            String imgUrl = dl.getElementsByTag("img").attr("data-original");
            book.setImgUrl(imgUrl);
            //https://www.zuxs.net/zu/1140.html -> https://www.zuxs.net/zu/1/1140/
            book.setChapterUrl(as.get(1).attr("href").replace("zu/", "zu/1/").replace(".html", "/"));
            book.setSource(LocalBookSource.hongchen.toString());
            SearchBookBean sbb = new SearchBookBean(book.getName(), book.getAuthor());
            books.add(sbb, book);
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return books;
    }


}
