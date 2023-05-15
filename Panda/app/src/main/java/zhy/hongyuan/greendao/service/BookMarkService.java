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

package zhy.hongyuan.greendao.service;

import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.BookMark;
import zhy.hongyuan.greendao.gen.BookMarkDao;
import zhy.hongyuan.util.help.StringHelper;

import java.util.ArrayList;
import java.util.List;


public class BookMarkService extends BaseService {
    private static volatile BookMarkService sInstance;

    public static BookMarkService getInstance() {
        if (sInstance == null){
            synchronized (BookMarkService.class){
                if (sInstance == null){
                    sInstance = new BookMarkService();
                }
            }
        }
        return sInstance;
    }

    /**
     * 通过ID查书签
     * @param id
     * @return
     */
    public BookMark getBookById(String id) {
        BookMarkDao bookMarkDao = DbManager.getInstance().getSession().getBookMarkDao();
        return bookMarkDao.load(id);
    }

    /**
     * 根据内容查找历史记录
     * @param title
     * @return
     */
    public BookMark findBookMarkByTitle(String title){
        return DbManager.getInstance().getSession().getBookMarkDao()
                .queryBuilder()
                .where(BookMarkDao.Properties.Title.eq(title))
                .unique();
    }
    /**
     * 获取书的所有书签
     *
     * @return
     */
    public List<BookMark> findBookAllBookMarkByBookId(String bookId) {
        if (bookId == null) {
            return new ArrayList<>();
        }
        return DbManager.getInstance().getSession().getBookMarkDao()
                .queryBuilder()
                .where(BookMarkDao.Properties.BookId.eq(bookId))
                .orderAsc(BookMarkDao.Properties.Number)
                .list();
    }


    /**
     * 添加书签
     * @param bookMark
     */
    public void addBookMark(BookMark bookMark) {
        bookMark.setId(StringHelper.getStringRandom(25));
        bookMark.setNumber(countBookMarkTotalNumByBookId(bookMark.getBookId()) + 1);
        addEntity(bookMark);
    }

    /**
     * 删除书的所有书签
     *
     * @param bookId
     */
    public void deleteBookALLBookMarkById(String bookId) {
        DbManager.getInstance().getSession().getBookMarkDao()
                .queryBuilder()
                .where(BookMarkDao.Properties.BookId.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
    /**
     * 批量删除书签
     *
     * @param bookMarks
     */
    public void deleteBookALLBookMarks(ArrayList<BookMark> bookMarks){
        for (BookMark bookMark : bookMarks){
            deleteBookMark(bookMark);
        }
    }

    /**
     * 通过ID删除书签
     * @param id
     */
    public void deleteBookMarkById(String id){
        BookMarkDao bookMarkDao = DbManager.getInstance().getSession().getBookMarkDao();
        bookMarkDao.deleteByKey(id);
    }

    /**
     * 删除书签
     * @param bookMark
     */
    public void deleteBookMark(BookMark bookMark){
        deleteEntity(bookMark);
    }


    /**
     * 通过id查询书籍书签总数
     * @return
     */
    public int countBookMarkTotalNumByBookId(String bookId){
        /*int num = 0;
        try {
            Cursor cursor = selectBySql("select count(*) n from book where book_id = " + bookId,null);
            if (cursor.moveToNext()){
                num = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return (int) DbManager.getInstance().getSession().getBookMarkDao()
                .queryBuilder()
                .where(BookMarkDao.Properties.BookId.eq(bookId))
                .count();
    }

    /**
     * 添加或更新书签
     * @param newBookMark
     */
    public void addOrUpdateBookMark(BookMark newBookMark){
        BookMark oldBookMark = findBookMarkByTitle(newBookMark.getTitle());
        if (oldBookMark == null){
            addBookMark(newBookMark);
        }else {
            oldBookMark.setBookId(newBookMark.getBookId());
            oldBookMark.setBookMarkReadPosition(newBookMark.getBookMarkReadPosition());
            oldBookMark.setNumber(countBookMarkTotalNumByBookId(oldBookMark.getBookId() + 1));
            updateEntity(oldBookMark);
        }
    }
}
