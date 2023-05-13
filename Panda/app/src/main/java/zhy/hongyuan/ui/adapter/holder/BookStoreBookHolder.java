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

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.adapter.ViewHolderImpl;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.model.sourceAnalyzer.BookSourceManager;
import zhy.hongyuan.ui.adapter.helper.IItemTouchHelperViewHolder;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;
import zhy.hongyuan.webapi.crawler.base.ReadCrawler;
import zhy.hongyuan.widget.CoverImageView;

/**
 * @author  hongyuan
 * @date 2020/9/7 7:35
 */
public class BookStoreBookHolder extends ViewHolderImpl<Book> implements IItemTouchHelperViewHolder {

    private CoverImageView tvBookImg;
    private TextView tvBookName;
    private TextView tvBookAuthor;
    private TextView tvBookTime;
    private  TextView tvBookNewestChapter;
    private TextView tvBookSource;

    private boolean hasImg;
    private Activity mActivity;

    public BookStoreBookHolder(boolean hasImg, Activity mActivity) {
        this.hasImg = hasImg;
        this.mActivity = mActivity;
    }


    @Override
    protected int getItemLayoutId() {
        return R.layout.listview_book_store_book_item;
    }

    @Override
    public void initView() {
        tvBookImg = findById(R.id.iv_book_img);
        tvBookName =  findById(R.id.tv_book_name);
        tvBookAuthor = findById(R.id.tv_book_author);
        tvBookNewestChapter = findById(R.id.tv_book_newest_chapter);
        tvBookTime =  findById(R.id.tv_book_time);
        tvBookSource = findById(R.id.tv_book_source);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, Book data, int pos) {
        tvBookName.setText(data.getName());
        tvBookAuthor.setText(data.getAuthor());
        tvBookNewestChapter.setText(StringHelper.isEmpty(data.getNewestChapterTitle()) ?
                data.getDesc() : data.getNewestChapterTitle());
        tvBookTime.setText(data.getUpdateDate());
        BookSource source = BookSourceManager.getBookSourceByStr(data.getSource());
        ReadCrawler rc = ReadCrawlerUtil.getReadCrawler(source);
        if (hasImg){
            tvBookImg.setVisibility(View.VISIBLE);
            if (!App.isDestroy(mActivity)) {
                tvBookImg.load(NetworkUtils.getAbsoluteURL(rc.getNameSpace(), data.getImgUrl()), data.getName(), data.getAuthor());
            }
        }
        if (data.getSource() != null) {
            tvBookSource.setText(String.format("书源：%s", source.getSourceName()));
        }else {
            tvBookSource.setText(data.getNewestChapterId());
        }
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
        getItemView().setTranslationZ(10);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        getItemView().setTranslationZ(0);
    }
}
