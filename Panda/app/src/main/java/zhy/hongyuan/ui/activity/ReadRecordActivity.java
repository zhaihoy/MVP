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

package zhy.hongyuan.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kongzue.dialogx.dialogs.BottomMenu;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import zhy.panda.myreader.R;
import zhy.hongyuan.base.BaseActivity;
import zhy.hongyuan.base.BitIntentDataManager;
import zhy.hongyuan.base.adapter.BaseListAdapter;
import zhy.hongyuan.base.adapter.IViewHolder;
import zhy.hongyuan.base.observer.MySingleObserver;
import zhy.hongyuan.common.APPCONST;
import zhy.panda.myreader.databinding.ActivityReadRecordBinding;
import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.ReadRecord;
import zhy.hongyuan.greendao.service.BookService;
import zhy.hongyuan.greendao.service.ReadRecordService;
import zhy.hongyuan.ui.adapter.holder.ReadRecordHolder;
import zhy.hongyuan.ui.dialog.DialogCreator;
import zhy.hongyuan.ui.dialog.SourceExchangeDialog;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.util.help.RelativeDateHelp;
import zhy.hongyuan.util.utils.RxUtils;

/**
 * @author  hongyuan
 * @date 2021/6/1 19:07
 */
public class ReadRecordActivity extends BaseActivity<ActivityReadRecordBinding> {
    private List<ReadRecord> records;
    private long allTime;
    private BaseListAdapter<ReadRecord> mAdapter;
    private SourceExchangeDialog mSourceDia;
    private ReadRecordService recordService;

    @Override
    protected void bindView() {
        binding = ActivityReadRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        getSupportActionBar().setTitle(getString(R.string.read_record));
    }

    @Override
    protected void onDestroy() {
        if (mSourceDia != null) {
            mSourceDia.stopSearch();
        }
        super.onDestroy();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        recordService = ReadRecordService.getInstance();
        recordService.getAllRecordsByTime().compose(RxUtils::toSimpleSingle)
                .subscribe(new MySingleObserver<List<ReadRecord>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }
                    @Override
                    public void onSuccess(@NotNull List<ReadRecord> readRecords) {
                        records = readRecords;
                        initRecords();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showError("数据加载失败\n" + e.getLocalizedMessage());
                    }
                });
    }

    private void initAllTime() {
        allTime = 0;
        for (ReadRecord record : records) {
            allTime += record.getReadTime();
        }
        binding.tvAllRecord.setText(getString(R.string.all_read_time,
                RelativeDateHelp.formatDuring(allTime)));
    }

    private void initRecords() {
        initAllTime();
        mAdapter = new BaseListAdapter<ReadRecord>() {
            @Override
            protected IViewHolder<ReadRecord> createViewHolder(int viewType) {
                return new ReadRecordHolder((itemPos, menuPos) -> {
                    ReadRecord record = records.get(itemPos);
                    switch (menuPos) {
                        case 0:
                            DbManager.getDaoSession().getReadRecordDao().delete(record);
                            records.remove(itemPos);
                            mAdapter.removeItem(record);
                            ToastUtils.showSuccess(String.format("《%s》阅读记录移除成功", record.getBookName()));
                            break;
                        case 1:
                            record.setReadTime(0);
                            record.setUpdateTime(0);
                            DbManager.getDaoSession().getReadRecordDao().insertOrReplace(record);
                            mAdapter.notifyDataSetChanged();
                            ToastUtils.showSuccess(String.format("《%s》阅读时长已清空", record.getBookName()));
                            break;
                    }
                    initAllTime();
                });
            }
        };

        //设置布局
        binding.rvRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecords.setAdapter(mAdapter);

        mAdapter.refreshItems(records);
        mAdapter.setOnItemClickListener((view, pos) -> {
            ReadRecord record = records.get(pos);
            Book book = BookService.getInstance().findBookByAuthorAndName(record.getBookName(), record.getBookAuthor());
            if (book == null) {
                DialogCreator.createCommonDialog(this, "搜索书籍",
                        "当前书籍未加入书架，是否重新搜索？", false, (dialog, which) -> {
                            Book newBook = new Book();
                            newBook.setName(record.getBookName());
                            newBook.setAuthor(record.getBookAuthor());
                            newBook.setImgUrl(record.getBookImg());
                            mSourceDia = new SourceExchangeDialog(this, newBook);
                            mSourceDia.setOnSourceChangeListener((bean, pos1) -> {
                                Intent intent = new Intent(this, BookDetailedActivity.class);
                                BitIntentDataManager.getInstance().putData(intent, mSourceDia.getaBooks());
                                intent.putExtra(APPCONST.SOURCE_INDEX, pos1);
                                ReadRecordActivity.this.startActivity(intent);
                                mSourceDia.dismiss();
                            });
                            mSourceDia.show();
                        }, null);
            } else {
                Intent intent = new Intent(this, BookDetailedActivity.class);
                BitIntentDataManager.getInstance().putData(intent, book);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.ivMore.setOnClickListener(v -> {
            String[] menu = new String[]{"移除所有记录", "清空阅读时长(不会删除记录)"};
            BottomMenu.show(menu)
                    .setTitle("总阅读记录")
                    .setOnMenuItemClickListener((dialog, text, which) -> {
                        if (records == null) {
                            ToastUtils.showWarring("数据未完成加载，无法进行操作！");
                            return false;
                        }
                        switch (which) {
                            case 0:
                                recordService.removeAll().compose(RxUtils::toSimpleSingle)
                                        .subscribe(new MySingleObserver<Boolean>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                addDisposable(d);
                                            }
                                            @Override
                                            public void onSuccess(@NotNull Boolean aBoolean) {
                                                ToastUtils.showSuccess("阅读记录已全部移除");
                                                records.clear();
                                                mAdapter.refreshItems(records);
                                                initAllTime();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                super.onError(e);
                                                ToastUtils.showError(e.getLocalizedMessage());
                                            }
                                        });
                                break;
                            case 1:
                                recordService.removeAllTime(records).compose(RxUtils::toSimpleSingle)
                                        .subscribe(new MySingleObserver<Boolean>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                addDisposable(d);
                                            }
                                            @Override
                                            public void onSuccess(@NotNull Boolean aBoolean) {
                                                ToastUtils.showSuccess("阅读时长已全部清空");
                                                mAdapter.notifyDataSetChanged();
                                                initAllTime();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                super.onError(e);
                                                ToastUtils.showError(e.getLocalizedMessage());
                                            }
                                        });
                                break;
                        }
                        return false;
                    })
                    .setCancelButton(R.string.cancel);
        });
    }
}
