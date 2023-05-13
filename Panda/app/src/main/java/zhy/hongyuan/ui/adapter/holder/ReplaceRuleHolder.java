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

package zhy.hongyuan.ui.adapter.holder;

import android.content.Intent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import xyz.fycz.myreader.R;
import zhy.hongyuan.base.adapter.ViewHolderImpl;
import zhy.hongyuan.base.observer.MyObserver;
import zhy.hongyuan.base.observer.MySingleObserver;
import zhy.hongyuan.common.APPCONST;
import zhy.hongyuan.greendao.entity.ReplaceRuleBean;
import zhy.hongyuan.model.ReplaceRuleManager;
import zhy.hongyuan.ui.adapter.ReplaceRuleAdapter;
import zhy.hongyuan.ui.dialog.ReplaceDialog;
import zhy.hongyuan.util.ShareUtils;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.util.utils.GsonExtensionsKt;
import zhy.hongyuan.widget.swipemenu.SwipeMenuLayout;

/**
 * @author  hongyuan
 * @date 2021/1/19 9:54
 */
public class ReplaceRuleHolder extends ViewHolderImpl<ReplaceRuleBean> {
    private RelativeLayout rlContent;
    private TextView tvRuleSummary;
    private Button btTop;
    private Button btBan;
    private Button btShare;
    private Button btDelete;
    private AppCompatActivity activity;
    private ReplaceRuleAdapter.OnSwipeListener onSwipeListener;

    public ReplaceRuleHolder(AppCompatActivity activity, ReplaceRuleAdapter.OnSwipeListener onSwipeListener) {
        this.activity = activity;
        this.onSwipeListener = onSwipeListener;
    }


    @Override
    protected int getItemLayoutId() {
        return R.layout.item_replace_rule;
    }

    @Override
    public void initView() {
        rlContent = findById(R.id.rl_content);
        tvRuleSummary = findById(R.id.tv_rule_summary);
        btTop = findById(R.id.bt_top);
        btBan = findById(R.id.bt_ban);
        btShare = findById(R.id.bt_share);
        btDelete = findById(R.id.btnDelete);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, ReplaceRuleBean data, int pos) {
        banOrUse(data);

        rlContent.setOnClickListener(v -> {
            ReplaceDialog replaceDialog = new ReplaceDialog(activity, data,
                    () -> {
                        banOrUse(data);
                        ToastUtils.showSuccess("内容替换规则修改成功！");
                        refreshUI();
                    });
            replaceDialog.show(activity.getSupportFragmentManager(), "");
        });

        btTop.setOnClickListener(v -> {
            ((SwipeMenuLayout) getItemView()).smoothClose();
            ReplaceRuleManager.toTop(data)
                    .subscribe(new MySingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(@NonNull Boolean aBoolean) {
                            if (aBoolean){
                                onSwipeListener.onTop(pos, data);
                            }
                        }
                    });
        });

        btBan.setOnClickListener(v -> {
            ((SwipeMenuLayout) getItemView()).smoothClose();
            data.setEnable(!data.getEnable());
            ReplaceRuleManager.saveData(data)
                    .subscribe(new MySingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(@NonNull Boolean aBoolean) {
                            if (aBoolean) {
                                banOrUse(data);
                                refreshUI();
                            }
                        }
                    });
        });

        btShare.setOnClickListener(v -> {
            ((SwipeMenuLayout) getItemView()).smoothClose();
            List<ReplaceRuleBean> shareRuleBean = new ArrayList<>();
            shareRuleBean.add(data);
            ShareUtils.share(activity, GsonExtensionsKt.getGSON().toJson(shareRuleBean));
        });
        btDelete.setOnClickListener(v -> {
            ((SwipeMenuLayout) getItemView()).smoothClose();
            Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                ReplaceRuleManager.delData(data);
                e.onNext(true);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            onSwipeListener.onDel(pos, data);
                            refreshUI();
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showError("删除失败");
                        }
                    });

        });
    }

    private void banOrUse(ReplaceRuleBean data){
        if (data.getEnable()) {
            tvRuleSummary.setTextColor(getContext().getResources().getColor(R.color.textPrimary));
            if (StringHelper.isEmpty(data.getReplaceSummary())) {
                tvRuleSummary.setText(String.format("%s->%s", data.getRegex(), data.getReplacement()));
            }else {
                tvRuleSummary.setText(data.getReplaceSummary());
            }
            btBan.setText(getContext().getString(R.string.ban));
        } else {
            tvRuleSummary.setTextColor(getContext().getResources().getColor(R.color.textSecondary));
            if (StringHelper.isEmpty(data.getReplaceSummary())) {
                tvRuleSummary.setText(String.format("(禁用中)%s->%s", data.getRegex(), data.getReplacement()));
            }else {
                tvRuleSummary.setText(String.format("(禁用中)%s", data.getReplaceSummary()));
            }
            btBan.setText(R.string.enable_use);
        }
    }

    private void refreshUI(){
        Intent result = new Intent();
        result.putExtra(APPCONST.RESULT_NEED_REFRESH, true);
        activity.setResult(AppCompatActivity.RESULT_OK, result);
    }
}
