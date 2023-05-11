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

package zhy.hongyuan.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.BasePresenter;
import zhy.hongyuan.base.observer.MyObserver;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.common.APPCONST;
import zhy.hongyuan.ui.activity.CatalogActivity;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.service.ChapterService;
import zhy.hongyuan.ui.adapter.ChapterTitleAdapter;
import zhy.hongyuan.ui.fragment.CatalogFragment;
import zhy.hongyuan.webapi.BookApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fengyue
 * @date 2020/7/22 9:14
 */
public class CatalogPresenter implements BasePresenter {
    private static final String TAG = CatalogPresenter.class.getSimpleName();
    private CatalogFragment mCatalogFragment;
    private ChapterService mChapterService;
    private List<Chapter> mChapters = new ArrayList<>();
    private List<Chapter> mConvertChapters = new ArrayList<>();
    private int curSortflag = 0; //0正序  1倒序
    private ChapterTitleAdapter mChapterTitleAdapter;
    private Book mBook;

    public CatalogPresenter(CatalogFragment mCatalogFragment) {
        this.mCatalogFragment = mCatalogFragment;
        mChapterService = ChapterService.getInstance();
    }

    @Override
    public void start() {
        mBook = ((CatalogActivity) mCatalogFragment.getActivity()).getmBook();
        mCatalogFragment.getFcChangeSort().setOnClickListener(view -> {
            if (curSortflag == 0) {//当前正序
                curSortflag = 1;
            } else {//当前倒序
                curSortflag = 0;
            }
            if (mChapterTitleAdapter != null) {
                changeChapterSort();
            }
        });
        mChapters = mChapterService.findBookAllChapterByBookId(mBook.getId());
        if (mChapters.size() != 0) {
            initChapterTitleList();
        } else {
            if ("本地书籍".equals(mBook.getType())) {
                ToastUtils.showWarring("本地书籍请先拆分章节！");
                return;
            }
            mCatalogFragment.getPbLoading().setVisibility(View.VISIBLE);
            BookApi.getBookChapters(mBook, ReadCrawlerUtil.getReadCrawler(mBook.getSource()))
                    .compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<List<Chapter>>() {
                @Override
                public void onNext(@NotNull List<Chapter> chapters) {
                    mChapters = chapters;
                    mCatalogFragment.getPbLoading().setVisibility(View.GONE);
                    initChapterTitleList();
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    ToastUtils.showError("章节目录加载失败！\n" + e.getLocalizedMessage());
                    mCatalogFragment.getPbLoading().setVisibility(View.GONE);
                    if (App.isDebug()) e.printStackTrace();
                }
            });
        }
        mCatalogFragment.getLvChapterList().setOnItemClickListener((adapterView, view, i, l) -> {
            Chapter chapter = mChapterTitleAdapter.getItem(i);
            final int position;
            assert chapter != null;
            if (chapter.getNumber() == 0) {
                if (curSortflag == 0) {
                    position = i;
                } else {
                    position = mChapters.size() - 1 - i;
                }
            } else {
                position = chapter.getNumber();
            }
            /*LLog.i(TAG, "position = " + position);
            LLog.i(TAG, "mChapters.size() = " + mChapters.size());*/
            Intent intent = new Intent();
            intent.putExtra(APPCONST.CHAPTER_PAGE, new int[]{position, 0});
            mCatalogFragment.getActivity().setResult(Activity.RESULT_OK, intent);
            mCatalogFragment.getActivity().finish();
        });
    }


    /**
     * 初始化章节目录
     */
    private void initChapterTitleList() {
        //初始化倒序章节
        mConvertChapters.addAll(mChapters);
        Collections.reverse(mConvertChapters);
        //设置布局管理器
        int curChapterPosition;
        curChapterPosition = mBook.getHisttoryChapterNum();
        mChapterTitleAdapter = new ChapterTitleAdapter(mCatalogFragment.getContext(), R.layout.listview_chapter_title_item, mChapters, mBook);
        mCatalogFragment.getLvChapterList().setAdapter(mChapterTitleAdapter);
        mCatalogFragment.getLvChapterList().setSelection(curChapterPosition);
    }

    /**
     * 改变章节列表排序（正倒序）
     */
    private void changeChapterSort() {
        if (curSortflag == 0) {
            mChapterTitleAdapter.clear();
            mChapterTitleAdapter.addAll(mChapterTitleAdapter.getmList());
        } else {
            mChapterTitleAdapter.clear();
            mConvertChapters.clear();
            mConvertChapters.addAll(mChapterTitleAdapter.getmList());
            Collections.reverse(mConvertChapters);
            mChapterTitleAdapter.addAll(mConvertChapters);
        }
        mChapterTitleAdapter.notifyDataSetChanged();
        mCatalogFragment.getLvChapterList().setAdapter(mChapterTitleAdapter);
    }

    /**
     * 搜索过滤
     *
     * @param query
     */
    public void startSearch(String query) {
        if (mChapters.size() == 0) return;
        mChapterTitleAdapter.getFilter().filter(query);
        mCatalogFragment.getLvChapterList().setSelection(0);
    }
}
