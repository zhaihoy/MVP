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

package zhy.hongyuan.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.BaseActivity;
import zhy.hongyuan.base.BitIntentDataManager;
import zhy.hongyuan.common.APPCONST;
import xyz.fycz.myreader.databinding.ActiityBookstoreBinding;
import zhy.hongyuan.entity.bookstore.BookType;
import zhy.hongyuan.entity.bookstore.QDBook;
import zhy.hongyuan.entity.bookstore.RankBook;
import zhy.hongyuan.entity.bookstore.SortBook;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.service.BookService;
import zhy.hongyuan.ui.adapter.BookStoreBookAdapter;
import zhy.hongyuan.ui.adapter.BookStoreBookTypeAdapter;
import zhy.hongyuan.ui.dialog.DialogCreator;
import zhy.hongyuan.ui.dialog.SourceExchangeDialog;
import zhy.hongyuan.util.SharedPreUtils;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.webapi.BookStoreApi;
import zhy.hongyuan.webapi.ResultCallback;
import zhy.hongyuan.webapi.crawler.base.FindCrawler3;
import zhy.hongyuan.webapi.crawler.find.QiDianMobileRank;

/**
 * @author  hongyuan
 * @date 2020/9/13 21:11
 */
public class BookstoreActivity extends BaseActivity<ActiityBookstoreBinding> {

    private FindCrawler3 findCrawler3;
    private LinearLayoutManager mLinearLayoutManager;
    private BookStoreBookTypeAdapter mBookStoreBookTypeAdapter;
    private List<BookType> mBookTypes;

    private BookStoreBookAdapter mBookStoreBookAdapter;
    private List<Book> bookList = new ArrayList<>();
    private SourceExchangeDialog mSourceDia;

    private BookType curType;

    private int page = 1;

    private String title = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initTypeList();
                    binding.refreshLayout.showFinish();
                    break;
                case 2:
                    List<Book> bookList = (List<Book>) msg.obj;
                    initBookList(bookList);
                    binding.srlBookList.setEnableRefresh(true);
                    binding.srlBookList.setEnableLoadMore(true);
                    binding.pbLoading.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.pbLoading.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    binding.pbLoading.setVisibility(View.GONE);
                    binding.srlBookList.finishRefresh(false);
                    binding.srlBookList.finishLoadMore(false);
                    break;
                case 5:
                    binding.refreshLayout.showError();
                    break;
                case 6:
                    DialogCreator.createTipDialog(BookstoreActivity.this,
                            getResources().getString(R.string.top_sort_tip, title));
                    break;
            }
        }
    };

    @Override
    protected void bindView() {
        binding = ActiityBookstoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        String subTitle = "";
        if (findCrawler3 != null) {
            String name = findCrawler3.getFindName();
            title = name.substring(0, name.indexOf("["));
            subTitle = name.substring(name.indexOf("[") + 1, name.length() - 1);
        }
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subTitle);
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
        findCrawler3 = (FindCrawler3) getIntent().getSerializableExtra(APPCONST.FIND_CRAWLER);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        binding.srlBookList.setEnableRefresh(false);
        binding.srlBookList.setEnableLoadMore(false);
        //小说列表下拉加载更多事件
        binding.srlBookList.setOnLoadMoreListener(refreshLayout -> {
            page++;
            getBooksData();
        });

        //小说列表上拉刷新事件
        binding.srlBookList.setOnRefreshListener(refreshLayout -> {
            page = 1;
            getBooksData();
        });

        mBookStoreBookAdapter = new BookStoreBookAdapter(findCrawler3.hasImg(), this);
        binding.rvBookList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookList.setAdapter(mBookStoreBookAdapter);
        binding.refreshLayout.setOnReloadingListener(this::getData);
    }

    @Override
    protected void initClick() {
        super.initClick();
        mBookStoreBookAdapter.setOnItemClickListener((view, pos) -> {
            Book book = bookList.get(pos);
            if (!findCrawler3.needSearch()) {
                goToBookDetail(book);
            } else {
                if (BookService.getInstance().isBookCollected(book)) {
                    goToBookDetail(book);
                    return;
                }
                mSourceDia = new SourceExchangeDialog(this, book);
                mSourceDia.setOnSourceChangeListener((bean, pos1) -> {
                    Intent intent = new Intent(this, BookDetailedActivity.class);
                    BitIntentDataManager.getInstance().putData(intent, mSourceDia.getaBooks());
                    intent.putExtra(APPCONST.SOURCE_INDEX, pos1);
                    BookstoreActivity.this.startActivity(intent);
                    mSourceDia.dismiss();
                });
                mSourceDia.show();
            }
        });
    }

    @Override
    protected void processLogic() {
        super.processLogic();
        getData();
        if (findCrawler3.needSearch()) {
            boolean isReadTopTip = SharedPreUtils.getInstance().getBoolean(getString(R.string.isReadTopTip), false);
            if (!isReadTopTip) {
                DialogCreator.createCommonDialog(this, "提示", getResources().getString(R.string.top_sort_tip, title),
                        true, "知道了", "不再提示", null,
                        (dialog, which) -> SharedPreUtils.getInstance().putBoolean(getString(R.string.isReadTopTip), true));
            }
        }
    }

    /**
     * 获取页面数据
     */
    private void getData() {
        if (findCrawler3 instanceof QiDianMobileRank) {
            if (SharedPreUtils.getInstance().getString(getString(R.string.qdCookie), "").equals("")) {
                ((QiDianMobileRank) findCrawler3).initCookie(this, new ResultCallback() {
                    @Override
                    public void onFinish(Object o, int code) {
                        if (App.isDestroy(BookstoreActivity.this)) return;
                        SharedPreUtils.getInstance().putString(getString(R.string.qdCookie), (String) o);
                        mBookTypes = findCrawler3.getBookTypes();
                        initBooks();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (App.isDestroy(BookstoreActivity.this)) return;
                        binding.refreshLayout.showError();
                        e.printStackTrace();
                    }
                });
            } else {
                mBookTypes = findCrawler3.getBookTypes();
                initBooks();
            }
        } else if ((mBookTypes = findCrawler3.getBookTypes()) != null) {
            initBooks();
        } else {
            BookStoreApi.getBookTypeList(findCrawler3, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    mBookTypes = (ArrayList<BookType>) o;
                    initBooks();
                }

                @Override
                public void onError(Exception e) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(5);
                }
            });
        }
    }

    private void initBooks() {
        curType = mBookTypes.get(0);
        mHandler.sendMessage(mHandler.obtainMessage(1));
        page = 1;
        getBooksData();
    }

    /**
     * 获取小数列表数据
     */
    private void getBooksData() {
        if (findCrawler3.getTypePage(curType, page)) {
            binding.srlBookList.finishLoadMoreWithNoMoreData();
            return;
        }

        mHandler.sendEmptyMessage(3);
        if (findCrawler3 instanceof QiDianMobileRank) {
            ((QiDianMobileRank) findCrawler3).getRankBooks(curType, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    List<Book> books = new ArrayList<>();
                    for (QDBook rb : (List<QDBook>) o) {
                        Book book = new Book();
                        book.setName(rb.getbName());
                        book.setAuthor(rb.getbAuth());
                        book.setImgUrl(rb.getImg());
                        String cat = rb.getCat();
                        book.setType(cat.contains("小说") || cat.length() >= 4 ? cat : cat + "小说");
                        book.setNewestChapterTitle(rb.getDesc());
                        book.setDesc(rb.getDesc());
                        if (rb instanceof RankBook) {
                            boolean hasRankCnt = !((RankBook) rb).getRankCnt().equals("null");
                            book.setUpdateDate(hasRankCnt ? book.getType() + "-" + rb.getCnt() : rb.getCnt());
                            book.setNewestChapterId(hasRankCnt ? ((RankBook) rb).getRankCnt() : book.getType());
                        } else if (rb instanceof SortBook) {
                            book.setUpdateDate(rb.getCnt());
                            book.setNewestChapterId(((SortBook) rb).getState());
                        }
                        books.add(book);
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(2, books));
                }

                @Override
                public void onError(Exception e) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    mHandler.sendMessage(mHandler.obtainMessage(4));
                    ToastUtils.showError("数据加载失败！\n" + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            BookStoreApi.getBookRankList(curType, findCrawler3, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    mHandler.sendMessage(mHandler.obtainMessage(2, o));
                }

                @Override
                public void onError(Exception e) {
                    if (App.isDestroy(BookstoreActivity.this)) return;
                    mHandler.sendMessage(mHandler.obtainMessage(4));
                    ToastUtils.showError("数据加载失败！\n" + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 初始化类别列表
     */
    private void initTypeList() {

        //设置布局管理器
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvTypeList.setLayoutManager(mLinearLayoutManager);
        mBookStoreBookTypeAdapter = new BookStoreBookTypeAdapter(this, mBookTypes);
        binding.rvTypeList.setAdapter(mBookStoreBookTypeAdapter);

        //点击事件
        mBookStoreBookTypeAdapter.setOnItemClickListener((pos, view) -> {
            if (curType.equals(mBookTypes.get(pos))) {
                return;
            }
            page = 1;
            curType = mBookTypes.get(pos);
            binding.srlBookList.resetNoMoreData();
            getBooksData();
        });


    }


    /**
     * 初始化小说列表
     */
    private void initBookList(List<Book> bookList) {
        if (page == 1) {
            mBookStoreBookAdapter.refreshItems(bookList);
            this.bookList.clear();
            this.bookList.addAll(bookList);
            binding.rvBookList.scrollToPosition(0);
        } else {
            this.bookList.addAll(bookList);
            this.bookList = new ArrayList<>(new LinkedHashSet<>(this.bookList));//去重
            mBookStoreBookAdapter.refreshItems(this.bookList);
        }

        //刷新动作完成
        binding.srlBookList.finishRefresh();
        //加载更多完成
        binding.srlBookList.finishLoadMore();

    }

    /**
     * 前往书籍详情
     *
     * @param book
     */
    private void goToBookDetail(Book book) {
        Intent intent = new Intent(this, BookDetailedActivity.class);
        BitIntentDataManager.getInstance().putData(intent, book);
        BookstoreActivity.this.startActivity(intent);
    }

    /********************************Event***************************************/
    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (findCrawler3.needSearch()) {
            menu.findItem(R.id.action_tip).setVisible(true);
        }
        return true;
    }

    /**
     * 导航栏菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_tip) {
            mHandler.sendEmptyMessage(6);
            return true;
        }
        return false;
    }
}
