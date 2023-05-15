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

package zhy.hongyuan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import xyz.fycz.myreader.R;
import zhy.hongyuan.base.BitIntentDataManager;
import zhy.hongyuan.ui.activity.ReadActivity;
import zhy.hongyuan.ui.adapter.helper.ItemTouchCallback;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.ui.activity.BookDetailedActivity;
import zhy.hongyuan.ui.presenter.BookcasePresenter;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;
import zhy.hongyuan.webapi.crawler.base.ReadCrawler;


/**
 * @author  hongyuan
 * @date 2020/4/19 11:23
 */

public class BookcaseDetailedAdapter extends BookcaseAdapter {
    ViewHolder viewHolder = null;

    public BookcaseDetailedAdapter(Context context, int textViewResourceId, ArrayList<Book> objects,
                                   boolean editState, BookcasePresenter bookcasePresenter, boolean isGroup) {
        super(context, textViewResourceId, objects, editState, bookcasePresenter, isGroup);
        itemTouchCallbackListener = new ItemTouchCallback.OnItemTouchListener() {
            private boolean isMoved = false;

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                Collections.swap(list, srcPosition, targetPosition);
                notifyItemMoved(srcPosition, targetPosition);
                isMoved = true;
                return true;
            }

            @Override
            public void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (isMoved){
                    AsyncTask.execute(() -> onDataMove());
                }
                isMoved = false;
            }

        };
    }

    @NonNull
    @NotNull
    @Override
    public BookcaseAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(mResourceId, null));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookcaseAdapter.ViewHolder holder, int position) {
        viewHolder = (ViewHolder) holder;
        initView(position);
    }

    private void initView(int position) {
        final Book book = getItem(position);
        if (StringHelper.isEmpty(book.getImgUrl())) {
            book.setImgUrl("");
        }
        ReadCrawler rc = ReadCrawlerUtil.getReadCrawler(book.getSource());
        viewHolder.ivBookImg.load(NetworkUtils.getAbsoluteURL(rc.getNameSpace(), book.getImgUrl()), book.getName(), book.getAuthor());

        viewHolder.tvBookName.setText(book.getName());

        viewHolder.tvBookAuthor.setText(book.getAuthor());
        viewHolder.tvHistoryChapter.setText(book.getHistoryChapterId());
        if (book.getNewestChapterTitle() != null)
            viewHolder.tvNewestChapter.setText(book.getNewestChapterTitle());

        if (mEditState) {
            viewHolder.tvNoReadNum.setVisibility(View.GONE);
            viewHolder.ivBookImg.setOnClickListener(null);
            viewHolder.llBookRead.setOnClickListener(null);
            viewHolder.pbLoading.setVisibility(View.GONE);
            viewHolder.cbBookChecked.setVisibility(View.VISIBLE);
            viewHolder.cbBookChecked.setChecked(getBookIsChecked(book.getId()));
            viewHolder.llBookRead.setOnClickListener(v -> {
                setCheckedBook(book.getId());
                mListener.onItemCheckedChange(getBookIsChecked(book.getId()));
            });
            viewHolder.ivBookImg.setOnClickListener(v -> {
                setCheckedBook(book.getId());
                mListener.onItemCheckedChange(getBookIsChecked(book.getId()));
            });
            viewHolder.cbBookChecked.setOnClickListener(v -> {
                setCheckedBook(book.getId());
                mListener.onItemCheckedChange(getBookIsChecked(book.getId()));
            });
        } else {
            viewHolder.cbBookChecked.setVisibility(View.GONE);
            boolean isLoading = false;
            try {
                isLoading = isBookLoading(book.getId());
            } catch (Exception ignored) {
            }
            if (isLoading) {
                viewHolder.pbLoading.setVisibility(View.VISIBLE);
                viewHolder.tvNoReadNum.setVisibility(View.GONE);
            } else {
                viewHolder.pbLoading.setVisibility(View.GONE);
                int notReadNum = book.getChapterTotalNum() - book.getHisttoryChapterNum() + book.getNoReadNum() - 1;
                if (notReadNum != 0) {
                    viewHolder.tvNoReadNum.setVisibility(View.VISIBLE);
                    if (book.getNoReadNum() != 0) {
                        viewHolder.tvNoReadNum.setHighlight(true);
                        if (notReadNum == -1) {
                            notReadNum = book.getNoReadNum() - 1;
                        }
                    } else {
                        viewHolder.tvNoReadNum.setHighlight(false);
                    }
                    viewHolder.tvNoReadNum.setBadgeCount(notReadNum);
                } else {
                    viewHolder.tvNoReadNum.setVisibility(View.GONE);
                }
            }
            viewHolder.llBookRead.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ReadActivity.class);
                BitIntentDataManager.getInstance().putData(intent, book);
                mBookService.updateEntity(book);
                mContext.startActivity(intent);
            });
            viewHolder.ivBookImg.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, BookDetailedActivity.class);
                BitIntentDataManager.getInstance().putData(intent, book);
                mContext.startActivity(intent);
            });
            viewHolder.llBookRead.setOnLongClickListener(v -> {
                if (!ismEditState()) {
                    showBookMenu(book, position);
                    return true;
                }
                return false;
            });
        }
    }

    static class ViewHolder extends BookcaseAdapter.ViewHolder {
        TextView tvBookAuthor;
        TextView tvHistoryChapter;
        TextView tvNewestChapter;
        LinearLayout llBookRead;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cbBookChecked = itemView.findViewById(R.id.cb_book_select);
            ivBookImg = itemView.findViewById(R.id.iv_book_img);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvNoReadNum = itemView.findViewById(R.id.tv_no_read_num);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvHistoryChapter = itemView.findViewById(R.id.tv_book_history_chapter);
            tvNewestChapter = itemView.findViewById(R.id.tv_book_newest_chapter);
            llBookRead = itemView.findViewById(R.id.ll_book_read);
            pbLoading = itemView.findViewById(R.id.pb_loading);
        }
    }

}
