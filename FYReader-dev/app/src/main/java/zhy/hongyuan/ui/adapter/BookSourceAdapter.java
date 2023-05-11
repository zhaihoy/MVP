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

package zhy.hongyuan.ui.adapter;


import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import zhy.hongyuan.application.App;
import zhy.hongyuan.base.adapter.IViewHolder;
import zhy.hongyuan.greendao.DbManager;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.ui.adapter.helper.ItemTouchCallback;
import zhy.hongyuan.ui.adapter.helper.OnStartDragListener;
import zhy.hongyuan.ui.adapter.holder.BookSourceHolder;

/**
 * @author fengyue
 * @date 2020/8/12 20:02
 */

public class BookSourceAdapter extends BaseSourceAdapter {
    private final FragmentActivity activity;
    private final OnSwipeListener onSwipeListener;
    private  OnStartDragListener onStartDragListener;
    private boolean mEditState;
    private final ItemTouchCallback.OnItemTouchListener itemTouchListener = new ItemTouchCallback.OnItemTouchListener() {

        private boolean isMoved = true;
        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            swapItem(srcPosition, targetPosition);
            isMoved = true;
            return true;
        }


        @Override
        public void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (isMoved) {
                App.getHandler().postDelayed(() -> notifyDataSetChanged(), 500);
                AsyncTask.execute(() -> {
                    for (int i = 1; i <= mList.size(); i++) {
                        mList.get(i - 1).setOrderNum(i);
                    }
                    DbManager.getDaoSession().getBookSourceDao().insertOrReplaceInTx(mList);
                });
            }
            isMoved = false;
        }

    };

    public BookSourceAdapter(FragmentActivity activity, OnSwipeListener onSwipeListener,
                             OnStartDragListener onStartDragListener) {
        this.activity = activity;
        this.onSwipeListener = onSwipeListener;
        this.onStartDragListener = onStartDragListener;
    }

    @Override
    protected IViewHolder<BookSource> createViewHolder(int viewType) {
        return new BookSourceHolder(activity, this, onSwipeListener, onStartDragListener);
    }

    public ItemTouchCallback.OnItemTouchListener getItemTouchListener() {
        return itemTouchListener;
    }

    public boolean ismEditState() {
        return mEditState;
    }

    public void setmEditState(boolean mEditState) {
        this.mEditState = mEditState;
        setCheckedAll(false);
    }

    public void removeItem(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
        if (pos != mList.size())
            notifyItemRangeChanged(pos, mList.size() - pos);
    }

    public void toTop(int which, BookSource bean) {
        mList.remove(bean);
        notifyItemInserted(0);
        mList.add(0, bean);
        notifyItemRemoved(which);
        notifyItemRangeChanged(0, which + 1);
    }

    public interface OnSwipeListener {
        void onDel(int which, BookSource source);

        void onTop(int which, BookSource source);
    }
}
