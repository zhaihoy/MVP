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

package zhy.hongyuan.greendao.service;

import android.database.Cursor;

import zhy.hongyuan.greendao.entity.SearchHistory;
import zhy.hongyuan.greendao.gen.SearchHistoryDao;
import zhy.hongyuan.util.help.DateHelper;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.greendao.DbManager;

import java.util.ArrayList;
import java.util.Date;




public class SearchHistoryService extends BaseService {
    private static volatile SearchHistoryService sInstance;
    public static SearchHistoryService getInstance() {
        if (sInstance == null){
            synchronized (SearchHistoryService.class){
                if (sInstance == null){
                    sInstance = new SearchHistoryService();
                }
            }
        }
        return sInstance;
    }
    private ArrayList<SearchHistory> findSearchHistorys(String sql, String[] selectionArgs) {
        ArrayList<SearchHistory> searchHistories = new ArrayList<>();
        try {
            Cursor cursor = selectBySql(sql, selectionArgs);
            if (cursor == null) return searchHistories;
            while (cursor.moveToNext()) {
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setId(cursor.getString(0));
                searchHistory.setContent(cursor.getString(1));
                searchHistory.setCreateDate(cursor.getString(2));
                searchHistories.add(searchHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return searchHistories;
        }
        return searchHistories;
    }

    /**
     * 返回所有历史记录（按时间从大到小）
     * @return
     */
    public ArrayList<SearchHistory> findAllSearchHistory() {
        String sql = "select * from search_history order by create_date desc";
        return findSearchHistorys(sql, null);
    }


    /**
     * 添加历史记录
     * @param searchHistory
     */
    public void addSearchHistory(SearchHistory searchHistory) {
        searchHistory.setId(StringHelper.getStringRandom(25));
        searchHistory.setCreateDate(DateHelper.longToTime(new Date().getTime()));
        addEntity(searchHistory);
    }

    /**
     * 删除历史记录
     * @param searchHistory
     */
    public void deleteHistory(SearchHistory searchHistory){
        deleteEntity(searchHistory);
    }

    /**
     * 清空历史记录
     */
    public void clearHistory(){
        SearchHistoryDao searchHistoryDao = DbManager.getInstance().getSession().getSearchHistoryDao();
        searchHistoryDao.deleteAll();
    }

    /**
     * 根据内容查找历史记录
     * @param content
     * @return
     */
    public SearchHistory findHistoryByContent(String content){
        SearchHistory searchHistory = null;
        String sql = "select * from search_history where content = ?";
        Cursor cursor = selectBySql(sql,new String[]{content});
        if (cursor == null) return null;
        if (cursor.moveToNext()){
            searchHistory = new SearchHistory();
            searchHistory.setId(cursor.getString(0));
            searchHistory.setContent(cursor.getString(1));
            searchHistory.setCreateDate(cursor.getString(2));
        }
        return searchHistory;
    }

    /**
     * 添加或更新历史记录
     * @param history
     */
    public void addOrUpadteHistory(String history){
        SearchHistory searchHistory = findHistoryByContent(history);
        if (searchHistory == null){
            searchHistory = new SearchHistory();
            searchHistory.setContent(history);
            addSearchHistory(searchHistory);
        }else {
            searchHistory.setCreateDate(DateHelper.longToTime(System.currentTimeMillis()));
            updateEntity(searchHistory);
        }
    }

}
