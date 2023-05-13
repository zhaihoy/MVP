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

package zhy.hongyuan.model.sourceAnalyzer;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Function;
import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.entity.thirdsource.BookSourceBean;
import zhy.hongyuan.entity.thirdsource.ThirdSourceUtil;
import zhy.hongyuan.entity.thirdsource.source3.Source3;
import zhy.hongyuan.entity.thirdsource.source3.Third3SourceUtil;
import zhy.hongyuan.enums.LocalBookSource;
import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.SubscribeFile;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.greendao.gen.BookSourceDao;
import zhy.hongyuan.model.third3.SourceAnalyzer;
import zhy.hongyuan.util.SharedPreUtils;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.FileUtils;
import zhy.hongyuan.util.utils.GsonExtensionsKt;
import zhy.hongyuan.util.utils.GsonUtils;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.util.utils.StringUtils;
import zhy.hongyuan.webapi.LanZouApi;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;


public class BookSourceManager {
    public static final int SOURCE_LENGTH = 500;

    public static List<BookSource> getEnabledBookSource() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.Enable.eq(true))
                .orderRaw(BookSourceDao.Properties.Weight.columnName + " DESC")
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    public static List<BookSource> getAllBookSource() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .orderRaw(getBookSourceSort())
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    public static List<BookSource> getEnabledBookSourceByOrderNum() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.Enable.eq(true))
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    public static List<BookSource> getAllBookSourceByOrderNum() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    public static List<BookSource> getEnableSourceByGroup(String group) {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.Enable.eq(true))
                .where(BookSourceDao.Properties.SourceGroup.like("%" + group + "%"))
                .orderRaw(BookSourceDao.Properties.Weight.columnName + " DESC")
                .list();
    }

    /**
     * 通过book.getSource()获取书源
     *
     * @param str
     * @return
     */
    public static BookSource getBookSourceByStr(String str) {
        BookSource source = getBookSourceByEName(str);
        if (source == null) source = getBookSourceByUrl(str);
        return source;
    }

    /**
     * 通过url获取书源
     *
     * @param url
     * @return
     */
    private static BookSource getBookSourceByUrl(String url) {
        if (url == null) return getDefaultSource();
        BookSource source = DbManager.getDaoSession().getBookSourceDao().load(url);
        if (source == null) return getDefaultSource();
        return source;
    }

    /**
     * 内置书源获取
     *
     * @param ename
     * @return
     */
    private static BookSource getBookSourceByEName(String ename) {
        if (ename == null) return null;
        if ("local".equals(ename)) return getLocalSource();
        BookSource source = DbManager.getDaoSession().getBookSourceDao().
                queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.eq(ename))
                .unique();
        return source;
    }

    /**
     * 通过book.getSource()获取书源名
     *
     * @param str
     * @return
     */
    public static String getSourceNameByStr(String str) {
        return getBookSourceByStr(str).getSourceName();
    }

    /**
     * 获取默认书源
     *
     * @return
     */
    private static BookSource getDefaultSource() {
        BookSource bookSource = new BookSource();
        bookSource.setSourceUrl("zhy.hongyuan.webapi.crawler.read.FYReadCrawler");
        bookSource.setSourceName("未知书源");
        bookSource.setSourceEName("fynovel");
        bookSource.setSourceGroup("内置书源");
        return bookSource;
    }

    /**
     * 获取本地书籍
     *
     * @return
     */
    public static BookSource getLocalSource() {
        BookSource bookSource = new BookSource();
        bookSource.setSourceEName("local");
        bookSource.setSourceName("本地书籍");
        return bookSource;
    }

    /**
     * 获取所有内置书源
     *
     * @return
     */
    public static List<BookSource> getAllLocalSource() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.isNotNull())
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }


    public static List<BookSource> getAllSubSource() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.isNotNull())
                .where(BookSourceDao.Properties.SourceType.isNotNull())
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    /**
     * 获取所有非内置书源
     *
     * @return
     */
    public static List<BookSource> getAllNoLocalSource() {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.isNull())
                .where(BookSourceDao.Properties.SourceType.isNotNull())
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    public static void removeSourceBySubscribe(SubscribeFile file) {
        DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.eq("订阅书源:" + file.getId()))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static List<BookSource> getSourceBySubscribe(SubscribeFile file) {
        return DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceEName.eq("订阅书源:" + file.getId()))
                .orderAsc(BookSourceDao.Properties.OrderNum)
                .list();
    }

    /**
     * 删除书源
     *
     * @param source
     */
    public static void removeBookSource(BookSource source) {
        if (source == null) return;
        DbManager.getDaoSession().getBookSourceDao().delete(source);
    }

    public static void removeBookSources(List<BookSource> sources) {
        if (sources == null) return;
        DbManager.getDaoSession().getBookSourceDao().deleteInTx(sources);
    }

    public static boolean isBookSourceExist(BookSource source) {
        if (source == null) return false;
        return DbManager.getDaoSession().getBookSourceDao().load(source.getSourceUrl()) != null;
    }

    public static String getBookSourceSort() {
        switch (SharedPreUtils.getInstance().getInt("SourceSort", 0)) {
            case 1:
                return BookSourceDao.Properties.Weight.columnName + " DESC";
            case 2:
                return BookSourceDao.Properties.SourceName.columnName + " COLLATE LOCALIZED ASC";
            default:
                return BookSourceDao.Properties.OrderNum.columnName + " ASC";
        }
    }

    public static void addBookSource(List<BookSource> bookSources) {
        for (BookSource bookSource : bookSources) {
            addBookSource(bookSource);
        }
    }

    public static boolean addBookSource(BookSource bookSource) {
        if (TextUtils.isEmpty(bookSource.getSourceName()) || TextUtils.isEmpty(bookSource.getSourceUrl()))
            return false;
        if (bookSource.getSourceUrl().endsWith("/")) {
            bookSource.setSourceUrl(bookSource.getSourceUrl().replaceAll("/+$", ""));
        }
        BookSource temp = DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                .where(BookSourceDao.Properties.SourceUrl.eq(bookSource.getSourceUrl())).unique();
        if (temp != null) {
            bookSource.setOrderNum(temp.getOrderNum());
        } else {
            bookSource.setOrderNum((int) (DbManager.getDaoSession().getBookSourceDao().queryBuilder().count() + 1));
        }
        DbManager.getDaoSession().getBookSourceDao().insertOrReplace(bookSource);
        return true;
    }

    public static Single<Boolean> saveData(BookSource bookSource) {
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            if (bookSource.getOrderNum() == 0) {
                bookSource.setOrderNum((int) (DbManager.getDaoSession().getBookSourceDao().queryBuilder().count() + 1));
            }
            DbManager.getDaoSession().getBookSourceDao().insertOrReplace(bookSource);
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static Single<Boolean> saveDatas(List<BookSource> sources) {
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            for (BookSource source : sources) {
                if (source.getOrderNum() == 0) {
                    source.setOrderNum((int) (DbManager.getDaoSession().getBookSourceDao().queryBuilder().count() + 1));
                }
            }
            DbManager.getDaoSession().getBookSourceDao().insertOrReplaceInTx(sources);
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static Single<Boolean> toTop(BookSource source) {
        return Single.create((SingleOnSubscribe<Boolean>) e -> {
            List<BookSource> List = getAllBookSourceByOrderNum();
            for (int i = 0; i < List.size(); i++) {
                List.get(i).setOrderNum(i + 1);
            }
            source.setOrderNum(0);
            DbManager.getDaoSession().getBookSourceDao().insertOrReplaceInTx(List);
            DbManager.getDaoSession().getBookSourceDao().insertOrReplace(source);
            e.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static List<String> getEnableGroupList() {
        List<String> groupList = new ArrayList<>();
        String sql = "SELECT DISTINCT "
                + BookSourceDao.Properties.SourceGroup.columnName
                + " FROM " + BookSourceDao.TABLENAME
                + " WHERE " + BookSourceDao.Properties.Enable.name + " = 1";
        Cursor cursor = DbManager.getDaoSession().getDatabase().rawQuery(sql, null);
        if (!cursor.moveToFirst()) return groupList;
        do {
            String group = cursor.getString(0);
            if (TextUtils.isEmpty(group) || TextUtils.isEmpty(group.trim())) continue;
            for (String item : group.split("\\s*[,;，；]\\s*")) {
                if (TextUtils.isEmpty(item) || groupList.contains(item) || item.equals("内置书源"))
                    continue;
                groupList.add(item);
            }
        } while (cursor.moveToNext());
        Collections.sort(groupList);
        return groupList;
    }

    public static List<String> getGroupList(boolean isSubscribe) {
        List<String> groupList = new ArrayList<>();
        String sql = "SELECT DISTINCT " + BookSourceDao.Properties.SourceGroup.columnName + ","
                + BookSourceDao.Properties.SourceEName.columnName + " FROM " + BookSourceDao.TABLENAME;
        Cursor cursor = DbManager.getDaoSession().getDatabase().rawQuery(sql, null);
        if (!cursor.moveToFirst()) return groupList;
        do {
            String group = cursor.getString(0);
            String eName = cursor.getString(1);
            if (TextUtils.isEmpty(group) || TextUtils.isEmpty(group.trim())) continue;
            for (String item : group.split("\\s*[,;，；]\\s*")) {
                if (isSubscribe) {
                    if (TextUtils.isEmpty(eName) || TextUtils.isEmpty(item) || groupList.contains(item) || item.equals("内置书源"))
                        continue;
                } else {
                    if (!TextUtils.isEmpty(eName) || TextUtils.isEmpty(item) || groupList.contains(item) || item.equals("内置书源"))
                        continue;
                }
                groupList.add(item);
            }
        } while (cursor.moveToNext());
        Collections.sort(groupList);
        return groupList;
    }

    public static Observable<List<BookSource>> importSource(String string, String subscribeId) {
        if (StringHelper.isEmpty(string)) return null;
        string = string.trim();
        if (NetworkUtils.isIPv4Address(string)) {
            string = String.format("http://%s:65501", string);
        }
        if (StringUtils.isJsonType(string)) {
            return importBookSourceFromJson(string.trim(), subscribeId)
                    .compose(RxUtils::toSimpleSingle);
        } else if (StringUtils.isCompressJsonType(string)) {
            return importBookSourceFromJson(StringUtils.unCompressJson(string), subscribeId)
                    .compose(RxUtils::toSimpleSingle);
        } else if (new File(string).isFile()) {
            return importSource(FileUtils.readText(string), subscribeId);
        }
        if (string.matches("https://.+\\.lanzou[a-z]\\.com/[\\s\\S]*")) {
            return LanZouApi.INSTANCE.getFileUrl(string)
                    .flatMap((Function<String, ObservableSource<String>>) s -> Observable.create(emitter -> {
                        emitter.onNext(OkHttpUtils.getHtml(s));
                        emitter.onComplete();
                    })).flatMap(json -> importBookSourceFromJson(json, subscribeId))
                    .compose(RxUtils::toSimpleSingle);
        }
        if (NetworkUtils.isUrl(string)) {
            String finalString = string;
            return Observable.create((ObservableEmitter<String> e) -> e.onNext(OkHttpUtils.getHtml(finalString)))
                    .flatMap(json -> importBookSourceFromJson(json, subscribeId))
                    .compose(RxUtils::toSimpleSingle);
        }
        return Observable.error(new Exception("不是Json或Url格式或文件路径"));
    }

    private static Observable<List<BookSource>> importBookSourceFromJson(String json) {
        return importBookSourceFromJson(json, "");
    }

    private static Observable<List<BookSource>> importBookSourceFromJson(String json, String subscribeId) {
        return Observable.create(emitter -> {
            List<BookSource> successImportSources = new ArrayList<>();
            List<BookSource> bookSources = importSources(json);
            if (bookSources != null) {
                for (BookSource bookSource : bookSources) {
                    if (!TextUtils.isEmpty(subscribeId))
                        bookSource.setSourceEName("订阅书源:" + subscribeId);
                    if (bookSource.containsGroup("删除")) {
                        DbManager.getDaoSession().getBookSourceDao().queryBuilder()
                                .where(BookSourceDao.Properties.SourceUrl.eq(bookSource.getSourceUrl()))
                                .buildDelete().executeDeleteWithoutDetachingEntities();
                    } else {
                        if (addBookSource(bookSource)) {
                            successImportSources.add(bookSource);
                        }
                    }
                }
                emitter.onNext(successImportSources);
                emitter.onComplete();
                return;
            }
            emitter.onError(new Throwable("格式不对"));
        });
    }

    private static List<BookSource> importSources(String json) {
        if (StringUtils.isJsonArray(json)) {
            try {
                List<BookSource> sources = GsonUtils.parseJArray(json, BookSource.class);
                String sourcesJson = GsonExtensionsKt.getGSON().toJson(sources);
                if (sources.size() > 0 && sourcesJson.length() > sources.size() * SOURCE_LENGTH) {
                    return sources;
                }

                List<Source3> source3s = SourceAnalyzer.INSTANCE.jsonToBookSources(json);
                String source3sJson = GsonExtensionsKt.getGSON().toJson(source3s);
                if (source3s.size() > 0 && source3sJson.length() > source3s.size() * SOURCE_LENGTH) {
                    return Third3SourceUtil.INSTANCE.source3sToSources(source3s);
                }

                List<BookSourceBean> source2s = GsonUtils.parseJArray(json, BookSourceBean.class);
                String source2sJson = GsonExtensionsKt.getGSON().toJson(source2s);
                if (source2s.size() > 0 && source2sJson.length() > source2s.size() * SOURCE_LENGTH) {
                    return ThirdSourceUtil.source2sToSources(source2s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (StringUtils.isJsonObject(json)) {
            try {
                List<BookSource> sources = new ArrayList<>();

                BookSource source = GsonUtils.parseJObject(json, BookSource.class);
                String sourceJson = GsonExtensionsKt.getGSON().toJson(source);
                if (!StringHelper.isEmpty(sourceJson) && sourceJson.length() > SOURCE_LENGTH) {
                    sources.add(source);
                    return sources;
                }

                Source3 source3 = SourceAnalyzer.INSTANCE.jsonToBookSource(json);
                String source3Json = GsonExtensionsKt.getGSON().toJson(source3);
                if (!StringHelper.isEmpty(source3Json) && source3Json.length() > SOURCE_LENGTH) {
                    sources.add(Third3SourceUtil.INSTANCE.source3ToSource(source3));
                    return sources;
                }

                BookSourceBean source2 = GsonUtils.parseJObject(json, BookSourceBean.class);
                String source2Json = GsonExtensionsKt.getGSON().toJson(source2);
                if (!StringHelper.isEmpty(source2Json) && source2Json.length() > SOURCE_LENGTH) {
                    sources.add(ThirdSourceUtil.source2ToSource(source2));
                    return sources;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void initDefaultSources() {
        Log.d("initDefaultSources", "execute");
        DbManager.getDaoSession().getBookSourceDao().deleteAll();
        String searchSource = SharedPreUtils.getInstance().getString(App.getmContext().getString(R.string.searchSource));
        for (LocalBookSource source : LocalBookSource.values()) {
            if (source == LocalBookSource.local || source == LocalBookSource.fynovel) continue;
            BookSource source1 = new BookSource();
            source1.setSourceEName(source.toString());
            source1.setSourceName(source.text);
            source1.setSourceGroup("内置书源");
            source1.setEnable(false);
            source1.setSourceUrl(ReadCrawlerUtil.getReadCrawlerClz(source.toString()));
            source1.setOrderNum(0);
            DbManager.getDaoSession().getBookSourceDao().insertOrReplace(source1);
        }
        String referenceSources = FileUtils.readAssertFile(App.getmContext(),
                "ReferenceSources.json");
        Observable<List<BookSource>> observable = BookSourceManager.importBookSourceFromJson(referenceSources);
        observable.subscribe();
    }
}
