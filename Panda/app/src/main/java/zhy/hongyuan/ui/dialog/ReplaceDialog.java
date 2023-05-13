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

package zhy.hongyuan.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import xyz.fycz.myreader.R;
import zhy.hongyuan.base.observer.MySingleObserver;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.ReplaceRuleBean;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.greendao.service.BookService;
import zhy.hongyuan.model.sourceAnalyzer.BookSourceManager;
import zhy.hongyuan.model.ReplaceRuleManager;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.ToastUtils;

/**
 * @author  hongyuan
 * @date 2021/1/18 20:04
 */
public class ReplaceDialog extends DialogFragment {
    private ReplaceRuleBean replaceRule;
    private Activity activity;
    private OnSaveReplaceRule onSaveReplaceRule;
    private EditText etRuleDesc;
    private EditText etRuleOld;
    private EditText etRuleNew;
    private EditText etRuleSource;
    private EditText etRuleBook;
    private CheckBox cbUseRegex;
    private Button btSelectSource;
    private Button btSelectBook;
    private TextView tvConfirm;
    private TextView tvCancel;

    public ReplaceDialog(Activity activity, ReplaceRuleBean replaceRule, OnSaveReplaceRule onSaveReplaceRule) {
        this.activity = activity;
        this.replaceRule = replaceRule;
        this.onSaveReplaceRule = onSaveReplaceRule;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.alertDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_replace, container, false);
        etRuleDesc = v.findViewById(R.id.et_rule_desc);
        etRuleOld = v.findViewById(R.id.et_rule_old);
        etRuleNew = v.findViewById(R.id.et_rule_new);
        etRuleSource = v.findViewById(R.id.et_rule_source);
        etRuleBook = v.findViewById(R.id.et_rule_book);
        cbUseRegex = v.findViewById(R.id.cb_use_regex);
        btSelectSource = v.findViewById(R.id.bt_select_source);
        btSelectBook = v.findViewById(R.id.bt_select_book);
        tvCancel = v.findViewById(R.id.tv_cancel);
        tvConfirm = v.findViewById(R.id.tv_confirm);

        etRuleDesc.setText(replaceRule.getReplaceSummary());
        etRuleOld.setText(replaceRule.getRegex());
        cbUseRegex.setChecked(replaceRule.getIsRegex());
        etRuleNew.setText(replaceRule.getReplacement());
        String[] useTo = replaceRule.getUseTo().split(";");
        etRuleSource.setText(useTo.length > 0 ?useTo[0] : "");
        etRuleBook.setText(useTo.length > 1 ? useTo[1] : "");
        btSelectSource.setOnClickListener(v1 -> selectSource());
        btSelectBook.setOnClickListener(v1 -> selectBook());

        tvConfirm.setOnClickListener(v1 -> {
            if (StringHelper.isEmpty(etRuleOld.getText().toString())) {
                ToastUtils.showWarring("替换规则不能为空");
                return;
            }
            replaceRule.setReplaceSummary(etRuleDesc.getText().toString());
            replaceRule.setRegex(etRuleOld.getText().toString());
            replaceRule.setIsRegex(cbUseRegex.isChecked());
            replaceRule.setReplacement(etRuleNew.getText().toString());
            replaceRule.setUseTo(String.format("%s;%s", etRuleSource.getText().toString(), etRuleBook.getText().toString()));
            ReplaceRuleManager.saveData(replaceRule).subscribe(new MySingleObserver<Boolean>() {
                @Override
                public void onSuccess(@NonNull Boolean aBoolean) {
                    onSaveReplaceRule.success();
                    dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtils.showError("发生错误\n" + e.getLocalizedMessage());
                }
            });
        });
        tvCancel.setOnClickListener(v1 -> dismiss());
        return v;
    }

    /**
     * 选择书源
     */
    private void selectSource(){
        List<BookSource> mSources = new ArrayList<>();
        mSources.add(BookSourceManager.getLocalSource());
        mSources.addAll(BookSourceManager.getAllBookSourceByOrderNum());
        CharSequence[] mSourcesName = new CharSequence[mSources.size()];
        HashMap<CharSequence, Boolean> mSelectSources = new LinkedHashMap<>();
        boolean[] isSelects = new boolean[mSources.size()];
        int sSourceCount = 0;
        int i = 0;

        String selectSource = etRuleSource.getText().toString();

        for (BookSource source : mSources) {
            mSourcesName[i] = source.getSourceName();
            String sourceStr;
            if (StringHelper.isEmpty(source.getSourceEName())){
                sourceStr = source.getSourceUrl();
            }else {
                sourceStr = source.getSourceEName();
            }
            boolean isSelect = selectSource.contains(sourceStr);
            if (isSelect) sSourceCount++;
            mSelectSources.put(sourceStr, isSelect);
            isSelects[i++] = isSelect;
        }

        new MultiChoiceDialog().create(activity, "选择书源",
                mSourcesName, isSelects, sSourceCount, (dialog, which) -> {
                    StringBuilder sb = new StringBuilder();
                    for (CharSequence sourceStr : mSelectSources.keySet()) {
                        if (mSelectSources.get(sourceStr)) {
                            sb.append(sourceStr);
                            sb.append(",");
                        }
                    }
                    if (sb.lastIndexOf(",") >= 0) sb.deleteCharAt(sb.lastIndexOf(","));
                    etRuleSource.setText(sb.toString());
                }, null, new DialogCreator.OnMultiDialogListener() {
                    @Override
                    public void onItemClick(DialogInterface dialog, int which, boolean isChecked) {
                        BookSource source = mSources.get(which);
                        String sourceStr;
                        if (StringHelper.isEmpty(source.getSourceEName())){
                            sourceStr = source.getSourceUrl();
                        }else {
                            sourceStr = source.getSourceEName();
                        }
                        mSelectSources.put(sourceStr, isChecked);
                    }

                    @Override
                    public void onSelectAll(boolean isSelectAll) {
                        for (CharSequence sourceStr : mSelectSources.keySet()) {
                            mSelectSources.put(sourceStr, isSelectAll);
                        }
                    }
                });
    }

    /**
     * 选择书籍
     */
    private void selectBook(){
        List<Book> mBooks = BookService.getInstance().getAllBooksNoHide();
        HashMap<CharSequence, Boolean> mSelectBooks = new LinkedHashMap<>();

        if (mBooks == null || mBooks.size() == 0){
            ToastUtils.showWarring("当前没有任何书籍！");
            return;
        }
        String isSelect = etRuleBook.getText().toString();

        CharSequence[] mBooksName = new CharSequence[mBooks.size()];
        boolean[] isSelects = new boolean[mBooks.size()];
        int sBookCount = 0;

        for (int i = 0; i < mBooks.size(); i++) {
            Book book = mBooks.get(i);
            mBooksName[i] = book.getName() + "-" + book.getAuthor();
            isSelects[i] = isSelect.contains(book.getName());
            if (isSelects[i]) {
                mSelectBooks.put(mBooksName[i], true);
                sBookCount++;
            }else {
                mSelectBooks.put(mBooksName[i], false);
            }
        }

        new MultiChoiceDialog().create(activity, "选择书籍",
                mBooksName, isSelects, sBookCount, (dialog, which) -> {
                    StringBuilder sb = new StringBuilder();
                    for (CharSequence bookName : mSelectBooks.keySet()) {
                        if (mSelectBooks.get(bookName)) {
                            sb.append(bookName);
                            sb.append(",");
                        }
                    }
                    if (sb.lastIndexOf(",") >= 0) sb.deleteCharAt(sb.lastIndexOf(","));
                    etRuleBook.setText(sb.toString());
                }, null, new DialogCreator.OnMultiDialogListener() {
                    @Override
                    public void onItemClick(DialogInterface dialog, int which, boolean isChecked) {
                        mSelectBooks.put(mBooksName[which], isChecked);
                    }

                    @Override
                    public void onSelectAll(boolean isSelectAll) {
                        for (CharSequence bookName : mSelectBooks.keySet()) {
                            mSelectBooks.put(bookName, isSelectAll);
                        }
                    }
                });
    }
    public interface OnSaveReplaceRule{
        void success();
    }
}
