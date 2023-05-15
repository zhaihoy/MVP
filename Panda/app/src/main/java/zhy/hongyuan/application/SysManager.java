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

package zhy.hongyuan.application;

import android.util.Log;

import xyz.fycz.myreader.R;
import zhy.hongyuan.common.APPCONST;
import zhy.hongyuan.entity.Setting;
import zhy.hongyuan.enums.BookcaseStyle;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.model.sourceAnalyzer.BookSourceManager;
import zhy.hongyuan.util.CacheHelper;
import zhy.hongyuan.util.SharedPreUtils;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;

import static zhy.hongyuan.application.App.getVersionCode;

import java.util.List;


public class SysManager {

    private static Setting mSetting;

    /**
     * 获取设置
     *
     * @return
     */
    public static Setting getSetting() {
        if (mSetting != null) {
            return mSetting;
        }
        mSetting = (Setting) CacheHelper.readObject(APPCONST.FILE_NAME_SETTING);
        if (mSetting == null) {
            mSetting = getDefaultSetting();
            saveSetting(mSetting);
        }
        return mSetting;
    }

    public static Setting getNewSetting() {
        Setting setting = (Setting) CacheHelper.readObject(APPCONST.FILE_NAME_SETTING);
        if (setting == null) {
            setting = getDefaultSetting();
            saveSetting(setting);
        }
        return setting;
    }


    /**
     * 保存设置
     *
     * @param setting
     */
    public static void saveSetting(Setting setting) {
        CacheHelper.saveObject(setting, APPCONST.FILE_NAME_SETTING);
    }


    /**
     * 默认设置
     *
     * @return
     */
    private static Setting getDefaultSetting() {
        Setting setting = new Setting();
        setting.setDayStyle(true);
        setting.setBookcaseStyle(BookcaseStyle.listMode);
        setting.setNewestVersionCode(getVersionCode());
        setting.setAutoSyn(false);
        setting.setMatchChapter(true);
        setting.setMatchChapterSuitability(0.7f);
        setting.setCatheGap(150);
        setting.setRefreshWhenStart(true);
        setting.setOpenBookStore(true);
        setting.setSettingVersion(APPCONST.SETTING_VERSION);
        setting.setSourceVersion(APPCONST.SOURCE_VERSION);
        setting.setHorizontalScreen(false);
        setting.initReadStyle();
        setting.setCurReadStyleIndex(1);
        return setting;
    }

    public static void regetmSetting() {
        mSetting = (Setting) CacheHelper.readObject(APPCONST.FILE_NAME_SETTING);
    }


    /**
     * 重置设置
     */

    public static void resetSetting() {
        Setting setting = getSetting();
        switch (setting.getSettingVersion()) {
            case 10:
            default:
                setting.initReadStyle();
                setting.setCurReadStyleIndex(1);
                setting.setSharedLayout(true);
                Log.d("SettingVersion", "" + 10);
            case 11:
                Log.d("SettingVersion", "" + 11);
            case 12:
                Log.d("SettingVersion", "" + 12);
        }
        setting.setSettingVersion(APPCONST.SETTING_VERSION);
        saveSetting(setting);
    }

    public static void resetSource() {
        Setting setting = getSetting();
        switch (setting.getSourceVersion()) {
            case 0:
            default:
                ReadCrawlerUtil.addReadCrawler(LocalBookSource.miaobi, LocalBookSource.dstq, LocalBookSource.xs7, LocalBookSource.du1du, LocalBookSource.paiotian);
                ReadCrawlerUtil.removeReadCrawler("cangshu99");
                Log.d("SourceVersion", "" + 0);
            case 1:
                ReadCrawlerUtil.addReadCrawler(LocalBookSource.laoyao, LocalBookSource.xingxing, LocalBookSource.shiguang, LocalBookSource.xiagu, LocalBookSource.hongchen);
                Log.d("SourceVersion", "" + 1);
            case 2:
                //ReadCrawlerUtil.addReadCrawler(LocalBookSource.rexue, LocalBookSource.chuanqi);
                ReadCrawlerUtil.addReadCrawler(LocalBookSource.chuanqi);
                Log.d("SourceVersion", "" + 2);
            case 3:
                ReadCrawlerUtil.resetReadCrawlers();
                Log.d("SourceVersion", "" + 3);
            case 4:
                ReadCrawlerUtil.removeReadCrawler("qiqi", "rexue", "pinshu");
                ReadCrawlerUtil.addReadCrawler(LocalBookSource.bijian, LocalBookSource.yanqinglou, LocalBookSource.wolong);
                Log.d("SourceVersion", "" + 4);
            case 5:
                ReadCrawlerUtil.addReadCrawler(LocalBookSource.ewenxue, LocalBookSource.shuhaige,
                        LocalBookSource.luoqiu, LocalBookSource.zw37, LocalBookSource.xbiquge,
                        LocalBookSource.zaishuyuan);
                Log.d("SourceVersion", "" + 5);
            case 6:
                SharedPreUtils.getInstance().putString(App.getmContext().getString(R.string.searchSource), "");
                Log.d("SourceVersion", "" + 5);
            case 7:
                List<BookSource> sources = BookSourceManager.getAllLocalSource();
                for (BookSource source : sources){
                    source.setEnable(false);
                }
                BookSourceManager.addBookSource(sources);
        }
        setting.setSourceVersion(APPCONST.SOURCE_VERSION);
        saveSetting(setting);
    }
}
