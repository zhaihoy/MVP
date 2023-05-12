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

package zhy.hongyuan.webapi.crawler.base;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import zhy.hongyuan.entity.FindKind;
import zhy.hongyuan.entity.StrResponse;
import zhy.hongyuan.greendao.entity.Book;

/**
 * @author  hongyuan
 * @date 2021/7/21 22:07
 */
public interface FindCrawler {
    String getName();
    String getTag();
    List<String> getGroups();
    Map<String, List<FindKind>> getKindsMap();
    List<FindKind> getKindsByKey(String key);
    Observable<Boolean> initData();
    boolean needSearch();
    Observable<List<Book>> getFindBooks(StrResponse strResponse, FindKind kind);
}
