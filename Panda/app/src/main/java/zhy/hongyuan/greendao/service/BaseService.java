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

import android.database.Cursor;

import zhy.hongyuan.greendao.gen.DaoSession;
import zhy.hongyuan.greendao.DbManager;


public class BaseService {

    public void addEntity(Object entity){
        DaoSession daoSession  = DbManager.getInstance().getSession();
        daoSession.insert(entity);
    }

    public void updateEntity(Object entity){
        DaoSession daoSession  = DbManager.getInstance().getSession();
        daoSession.update(entity);
    }

    public void deleteEntity(Object entity){
        DaoSession daoSession  = DbManager.getInstance().getSession();
        daoSession.delete(entity);
    }

    /**
     * 通过SQL查找
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor selectBySql(String sql, String[] selectionArgs){

        Cursor cursor = null;
        try {
            DaoSession daoSession  = DbManager.getInstance().getSession();
            cursor = daoSession.getDatabase().rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cursor;
    }

    /**
     * 执行SQL进行增删改
     * @param sql
     * @param selectionArgs
     */
    public void  rawQuery(String sql, String[] selectionArgs) {
        DaoSession daoSession  = DbManager.getInstance().getSession();
        Cursor cursor = daoSession.getDatabase().rawQuery(sql, selectionArgs);
    }


}
