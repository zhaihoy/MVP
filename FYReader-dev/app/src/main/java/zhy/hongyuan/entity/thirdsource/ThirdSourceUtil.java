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

package zhy.hongyuan.entity.thirdsource;

import java.util.ArrayList;
import java.util.List;

import zhy.hongyuan.common.APPCONST;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.greendao.entity.rule.ContentRule;
import zhy.hongyuan.greendao.entity.rule.FindRule;
import zhy.hongyuan.greendao.entity.rule.InfoRule;
import zhy.hongyuan.greendao.entity.rule.SearchRule;
import zhy.hongyuan.greendao.entity.rule.TocRule;

/**
 * @author fengyue
 * @date 2021/5/14 9:24
 */
public class ThirdSourceUtil {
    public static BookSource source2ToSource(BookSourceBean bean) {
        BookSource bookSource = new BookSource();
        bookSource.setSourceUrl(bean.getBookSourceUrl());
        bookSource.setSourceName(bean.getBookSourceName());
        bookSource.setSourceGroup(bean.getBookSourceGroup());
        bookSource.setSourceType(APPCONST.THIRD_SOURCE);
        bookSource.setLoginUrl(bean.getLoginUrl());
        bookSource.setLastUpdateTime(bean.getLastUpdateTime());
        bookSource.setOrderNum(bean.getSerialNumber());
        bookSource.setWeight(bean.getWeight());
        bookSource.setEnable(bean.getEnable());

        SearchRule searchRule = new SearchRule();
        searchRule.setSearchUrl(bean.getRuleSearchUrl());
        searchRule.setList(bean.getRuleSearchList());
        searchRule.setName(bean.getRuleSearchName());
        searchRule.setAuthor(bean.getRuleSearchAuthor());
        searchRule.setType(bean.getRuleSearchKind());
        searchRule.setDesc(bean.getRuleSearchIntroduce());
        searchRule.setLastChapter(bean.getRuleSearchLastChapter());
        searchRule.setImgUrl(bean.getRuleSearchCoverUrl());
        searchRule.setInfoUrl(bean.getRuleSearchNoteUrl());
        searchRule.setRelatedWithInfo(true);
        bookSource.setSearchRule(searchRule);

        InfoRule infoRule = new InfoRule();
        infoRule.setUrlPattern(bean.getRuleBookUrlPattern());
        infoRule.setInit(bean.getRuleBookInfoInit());
        infoRule.setName(bean.getRuleBookName());
        infoRule.setAuthor(bean.getRuleBookAuthor());
        infoRule.setType(bean.getRuleBookKind());
        infoRule.setDesc(bean.getRuleIntroduce());
        infoRule.setLastChapter(bean.getRuleBookLastChapter());
        infoRule.setImgUrl(bean.getRuleCoverUrl());
        infoRule.setTocUrl(bean.getRuleChapterUrl());
        bookSource.setInfoRule(infoRule);

        TocRule tocRule = new TocRule();
        tocRule.setChapterList(bean.getRuleChapterList());
        tocRule.setChapterName(bean.getRuleChapterName());
        tocRule.setChapterUrl(bean.getRuleContentUrl());
        tocRule.setTocUrlNext(bean.getRuleChapterUrlNext());
        bookSource.setTocRule(tocRule);

        ContentRule contentRule = new ContentRule();
        contentRule.setContent(bean.getRuleBookContent());
        contentRule.setContentUrlNext(bean.getRuleContentUrlNext());
        bookSource.setContentRule(contentRule);

        FindRule findRule = new FindRule();
        findRule.setUrl(bean.getRuleFindUrl());
        findRule.setList(bean.getRuleFindList());
        findRule.setName(bean.getRuleFindName());
        findRule.setAuthor(bean.getRuleFindAuthor());
        findRule.setType(bean.getRuleFindKind());
        findRule.setDesc(bean.getRuleFindIntroduce());
        findRule.setLastChapter(bean.getRuleFindLastChapter());
        findRule.setImgUrl(bean.getRuleFindCoverUrl());
        findRule.setInfoUrl(bean.getRuleFindNoteUrl());
        bookSource.setFindRule(findRule);
        return bookSource;
    }

    public static BookSource source3ToSource(BookSource3Bean bean) {
        BookSource bookSource = source2ToSource(bean.toBookSourceBean());
        bookSource.setSourceComment(bean.getBookSourceComment());
        return bookSource;
    }

    public static List<BookSource> source2sToSources(List<BookSourceBean> beans){
        List<BookSource> sources = new ArrayList<>();
        for (BookSourceBean bean : beans){
            sources.add(source2ToSource(bean));
        }
        return sources;
    }

    public static List<BookSource> source3sToSources(List<BookSource3Bean> beans){
        List<BookSource> sources = new ArrayList<>();
        for (BookSource3Bean bean : beans){
            sources.add(source3ToSource(bean));
        }
        return sources;
    }
}