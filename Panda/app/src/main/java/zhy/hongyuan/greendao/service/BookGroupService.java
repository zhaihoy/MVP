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

import org.jetbrains.annotations.NotNull;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.BookGroup;
import zhy.hongyuan.greendao.gen.BookGroupDao;
import zhy.hongyuan.util.SharedPreUtils;
import zhy.hongyuan.util.help.StringHelper;

import java.util.List;

/**
 * @author  hongyuan
 * @date 2020/9/26 12:14
 */
public class BookGroupService extends BaseService{
    private static volatile BookGroupService sInstance;

    public static BookGroupService getInstance() {
        if (sInstance == null){
            synchronized (BookGroupService.class){
                if (sInstance == null){
                    sInstance = new BookGroupService();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取所有书籍分组
     * @return
     */
    public List<BookGroup> getAllGroups(){
        return DbManager.getInstance().getSession().getBookGroupDao()
                .queryBuilder()
                .orderAsc(BookGroupDao.Properties.Num)
                .list();
    }

    /**
     * 通过I的获取书籍分组
     * @param groupId
     * @return
     */
    public BookGroup getGroupById(String groupId){
        return DbManager.getInstance().getSession().getBookGroupDao()
                .queryBuilder()
                .where(BookGroupDao.Properties.Id.eq(groupId))
                .unique();
    }

    /**
     * 添加书籍分组
     * @param bookGroup
     */
    public void addBookGroup(BookGroup bookGroup){
        bookGroup.setNum(countBookGroup());
        bookGroup.setId(StringHelper.getStringRandom(25));
        addEntity(bookGroup);
    }

    /**
     * 删除书籍分组
     * @param bookGroup
     */
    public void deleteBookGroup(BookGroup bookGroup){
        deleteEntity(bookGroup);
    }

    public void deleteGroupById(String id){
        DbManager.getInstance().getSession().getBookGroupDao().deleteByKey(id);
    }

    public void createPrivateGroup(){
        BookGroup bookGroup = new BookGroup();
        bookGroup.setName("私密书架");
        addBookGroup(bookGroup);
        SharedPreUtils.getInstance().putString("privateGroupId", bookGroup.getId());
    }

    public void deletePrivateGroup(){
        String privateGroupId = SharedPreUtils.getInstance().getString("privateGroupId");
        deleteGroupById(privateGroupId);
        BookService.getInstance().deleteBooksByGroupId(privateGroupId);
    }

    /**
     * 当前是否为私密书架
     * @return
     */
    public boolean curGroupIsPrivate(){
        String curBookGroupId = SharedPreUtils.getInstance().getString(App.getmContext().getString(R.string.curBookGroupId), "");
        String privateGroupId = SharedPreUtils.getInstance().getString("privateGroupId");
        return !StringHelper.isEmpty(curBookGroupId) && curBookGroupId.equals(privateGroupId);
    }

    public int countBookGroup(){
        return (int) DbManager.getInstance().getSession().getBookGroupDao()
                .queryBuilder()
                .count();
    }

    public void updateGroups(@NotNull List<BookGroup> groups) {
        BookGroupDao bookDao = DbManager.getInstance().getSession().getBookGroupDao();
        bookDao.insertOrReplaceInTx(groups);
    }
}
