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

package zhy.hongyuan.webapi.crawler.source;

import org.seimicrawler.xpath.JXDocument;

import java.util.ArrayList;
import java.util.List;

import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.greendao.entity.rule.SearchRule;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.model.sourceAnalyzer.XpathAnalyzer;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.webapi.crawler.base.BaseSourceCrawler;

/**
 * @author  hongyuan
 * @date 2021/2/14 17:52
 */
public class XpathCrawler extends BaseSourceCrawler {
    private final XpathAnalyzer analyzer;

    public XpathCrawler(BookSource source) {
        super(source, new XpathAnalyzer());
        this.analyzer = (XpathAnalyzer) super.analyzer;
    }

    @Override
    public ConMVMap<SearchBookBean, Book> getBooksFromSearchHtml(String html) {
        ConMVMap<SearchBookBean, Book> books = new ConMVMap<>();
        JXDocument jxDoc = JXDocument.create(html);
        SearchRule searchRule = source.getSearchRule();
        if (StringHelper.isEmpty(searchRule.getList())) {
            getBooksNoList(jxDoc, searchRule, books);
        } else {
            getBooks(jxDoc, searchRule, books);
        }
        return books;
    }

    @Override
    public ArrayList<Chapter> getChaptersFromHtml(String html) {
        ArrayList<Chapter> chapters = new ArrayList<>();
        JXDocument jxDoc = JXDocument.create(html);
        getChapters(jxDoc, chapters);
        return chapters;
    }


    @Override
    public String getContentFormHtml(String html) {
        JXDocument jxDoc = JXDocument.create(html);
        return getContent(jxDoc);
    }

    @Override
    public Book getBookInfo(String html, Book book) {
        JXDocument jxDoc = JXDocument.create(html);
        return getBookInfo(jxDoc, book);
    }


    protected List getList(String str, Object obj) {
        return analyzer.getJXNodeList(str, obj);
    }
}
