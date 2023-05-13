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

package zhy.hongyuan.greendao;


import zhy.hongyuan.application.App;
import zhy.hongyuan.greendao.gen.DaoMaster;
import zhy.hongyuan.greendao.gen.DaoSession;
import zhy.hongyuan.greendao.util.MySQLiteOpenHelper;



public class DbManager {
    private static DbManager instance;
    private static DaoMaster daoMaster;
    private DaoSession mDaoSession;

    private static MySQLiteOpenHelper mySQLiteOpenHelper;

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    public DbManager(){
        mySQLiteOpenHelper = new MySQLiteOpenHelper(App.getmContext(), "read" , null);
        daoMaster = new DaoMaster(mySQLiteOpenHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return getInstance().mDaoSession;
    }

    public DaoSession getSession(){
       return mDaoSession;
    }

}
