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

package zhy.hongyuan.model;

import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.ReplaceRuleBean;
import zhy.hongyuan.greendao.gen.ReplaceRuleBeanDao;
import zhy.hongyuan.util.utils.GsonUtils;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.util.utils.StringUtils;

/**
 * Created by GKF on 2018/2/12.
 * 替换规则管理
 */

public class ReplaceRuleManager {
    private static List<ReplaceRuleBean> replaceRuleBeansEnabled;

    public static List<ReplaceRuleBean> getEnabled() {
        if (replaceRuleBeansEnabled == null) {
            replaceRuleBeansEnabled = DbManager.getDaoSession()
                    .getReplaceRuleBeanDao().queryBuilder()
                    .where(ReplaceRuleBeanDao.Properties.Enable.eq(true))
                    .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                    .list();
        }
        return replaceRuleBeansEnabled;
    }
    // 合并广告话术规则
    public static Single<Boolean> mergeAdRules(ReplaceRuleBean replaceRuleBean) {


        String rule = formateAdRule(replaceRuleBean.getRegex());

/*        String summary=replaceRuleBean.getReplaceSummary();
        if(summary==null)
            summary="";
        String sumary_pre=summary.split("-")[0];*/

        int sn = replaceRuleBean.getSerialNumber();
        if (sn == 0) {
            sn = (int) (DbManager.getDaoSession().getReplaceRuleBeanDao().queryBuilder().count() + 1);
            replaceRuleBean.setSerialNumber(sn);
        }

        List<ReplaceRuleBean> list = DbManager.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .where(ReplaceRuleBeanDao.Properties.Enable.eq(true))
                .where(ReplaceRuleBeanDao.Properties.ReplaceSummary.eq(replaceRuleBean.getReplaceSummary()))
                .where(ReplaceRuleBeanDao.Properties.SerialNumber.notEq(sn))
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list();
        if (list.size() < 1) {
            replaceRuleBean.setRegex(rule);
            return saveData(replaceRuleBean);
        } else {
            StringBuffer buffer = new StringBuffer(rule);
            for (ReplaceRuleBean li : list) {
                buffer.append('\n');
                buffer.append(li.getRegex());
//                    buffer.append(formateAdRule(rule.getRegex()));
            }
            replaceRuleBean.setRegex(formateAdRule(buffer.toString()));

            return Single.create((SingleOnSubscribe<Boolean>) emitter -> {

                DbManager.getDaoSession().getReplaceRuleBeanDao().insertOrReplace(replaceRuleBean);
                for (ReplaceRuleBean li : list) {
                    DbManager.getDaoSession().getReplaceRuleBeanDao().delete(li);
                }
                refreshDataS();
                emitter.onSuccess(true);
            }).compose(RxUtils::toSimpleSingle);

        }
    }

    // 把输入的规则进行预处理（分段、排序、去重）。保存的是普通多行文本。
    public static String formateAdRule(String rule) {

        if (rule == null)
            return "";
        String result = rule.trim();
        if (result.length() < 1)
            return "";

        String string = rule
//                用中文中的.视为。进行分段
                .replaceAll("(?<=([^a-zA-Z\\p{P}]{4,8}))\\.+(?![^a-zA-Z\\p{P}]{4,8})","\n")
//                用常见的适合分段的标点进行分段，句首句尾除外
//                .replaceAll("([^\\p{P}\n^])([…,，:：？。！?!~<>《》【】（）()]+)([^\\p{P}\n$])", "$1\n$3")
//                表达式无法解决句尾连续多个符号的问题
//                .replaceAll("[…,，:：？。！?!~<>《》【】（）()]+(?!\\s*\n|$)", "\n")
                .replaceAll("(?<![\\p{P}\n^])([…,，:：？。！?!~<>《》【】（）()]+)(?![\\p{P}\n$])", "\n")

                ;

        String[] lines = string.split("\n");
        List<String> list = new ArrayList<>();

        for (String s : lines) {
            s = s.trim()
//                    .replaceAll("\\s+", "\\s")
            ;
            if (!list.contains(s)) {
                list.add(s);
            }
        }
        Collections.sort(list);
        StringBuffer buffer = new StringBuffer(rule.length() + 1);
        for (int i = 0; i < list.size(); i++) {
            buffer.append('\n');
            buffer.append(list.get(i));
        }
        return buffer.toString().trim();
    }
    public static Single<List<ReplaceRuleBean>> getAll() {
        return Single.create((SingleOnSubscribe<List<ReplaceRuleBean>>) emitter -> emitter.onSuccess(DbManager.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list())).compose(RxUtils::toSimpleSingle);
    }

    public static List<ReplaceRuleBean> getAllRules() {
        return DbManager.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list();
    }

    public static Single<Boolean> saveData(ReplaceRuleBean replaceRuleBean) {
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            if (replaceRuleBean.getSerialNumber() == 0) {
                replaceRuleBean.setSerialNumber((int) (DbManager.getDaoSession().getReplaceRuleBeanDao().queryBuilder().count() + 1));
            }
            DbManager.getDaoSession().getReplaceRuleBeanDao().insertOrReplace(replaceRuleBean);
            refreshDataS();
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static void delData(ReplaceRuleBean replaceRuleBean) {
        DbManager.getDaoSession().getReplaceRuleBeanDao().delete(replaceRuleBean);
        refreshDataS();
    }

    public static void addDataS(List<ReplaceRuleBean> replaceRuleBeans) {
        if (replaceRuleBeans != null && replaceRuleBeans.size() > 0) {
            DbManager.getDaoSession().getReplaceRuleBeanDao().insertOrReplaceInTx(replaceRuleBeans);
            refreshDataS();
        }
    }

    public static void delDataS(List<ReplaceRuleBean> replaceRuleBeans) {
        if (replaceRuleBeans == null) return;
        DbManager.getDaoSession().getReplaceRuleBeanDao().deleteInTx(replaceRuleBeans);
        refreshDataS();
    }

    public static Single<Boolean> toTop(ReplaceRuleBean bean) {
        return Single.create((SingleOnSubscribe<Boolean>) e -> {
            List<ReplaceRuleBean> beans = getAllRules();
            for (int i = 0; i < beans.size(); i++) {
                beans.get(i).setSerialNumber(i + 1);
            }
            bean.setSerialNumber(0);
            DbManager.getDaoSession().getReplaceRuleBeanDao().insertOrReplaceInTx(beans);
            DbManager.getDaoSession().getReplaceRuleBeanDao().insertOrReplace(bean);
            e.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    private static void refreshDataS() {
        replaceRuleBeansEnabled = DbManager.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .where(ReplaceRuleBeanDao.Properties.Enable.eq(true))
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list();
    }

    public static Observable<Boolean> importReplaceRule(String text) {
        if (TextUtils.isEmpty(text)) return null;
        text = text.trim();
        if (text.length() == 0) return null;
        if (StringUtils.isJsonType(text)) {
            return importReplaceRuleO(text)
                    .compose(RxUtils::toSimpleSingle);
        }
        if (NetworkUtils.isUrl(text)) {
            String finalText = text;
            return Observable.create((ObservableOnSubscribe<String>) emitter -> emitter.onNext(OkHttpUtils.getHtml(finalText)))
                    .flatMap(ReplaceRuleManager::importReplaceRuleO)
                    .compose(RxUtils::toSimpleSingle);
        }
        return Observable.error(new Exception("不是Json或Url格式"));
    }

    private static Observable<Boolean> importReplaceRuleO(String json) {
        return Observable.create(e -> {
            try {
                List<ReplaceRuleBean> replaceRuleBeans = GsonUtils.parseJArray(json, ReplaceRuleBean.class);
                addDataS(replaceRuleBeans);
                e.onNext(true);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
            }
            e.onComplete();
        });
    }

}
