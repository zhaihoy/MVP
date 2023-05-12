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

package zhy.hongyuan.ui.adapter.holder;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.dialogs.BottomMenu;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.adapter.ViewHolderImpl;
import zhy.hongyuan.greendao.entity.ReadRecord;
import zhy.hongyuan.util.help.RelativeDateHelp;
import zhy.hongyuan.widget.CoverImageView;

/**
 * @author  hongyuan
 * @date 2021/6/1 19:31
 */
public class ReadRecordHolder extends ViewHolderImpl<ReadRecord> {
    private OnMenuItemClickListener onMenuItemClickListener;
    private CoverImageView ivBookImg;
    private TextView tvBookName;
    private TextView tvBookAuthor;
    private TextView tvRecord;
    private ImageView ivMore;
    private final String[] menu = new String[]{"移除此记录", "清空阅读时长(不会删除记录)"};

    public ReadRecordHolder(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_read_record;
    }

    @Override
    public void initView() {
        ivBookImg = findById(R.id.iv_book_img);
        tvBookName = findById(R.id.tv_book_name);
        tvBookAuthor = findById(R.id.tv_book_author);
        tvRecord = findById(R.id.tv_book_read_record);
        ivMore = findById(R.id.iv_more);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, ReadRecord data, int pos) {
        if (!App.isDestroy((Activity) getContext())) {
            ivBookImg.load(data.getBookImg(), data.getBookName(), data.getBookAuthor());
        }
        tvBookName.setText(data.getBookName());
        tvBookAuthor.setText(data.getBookAuthor());
        String lastTime = data.getUpdateTime() == 0 ? "无记录"
                : RelativeDateHelp.format(data.getUpdateTime());
        tvRecord.setText(String.format("%s · %s", lastTime,
                RelativeDateHelp.formatDuring(data.getReadTime())));
        ivMore.setOnClickListener(v -> BottomMenu.show(menu)
                .setTitle(data.getBookName())
                .setOnMenuItemClickListener((dialog, text, which) -> {
                    onMenuItemClickListener.onClick(pos, which);
                    return false;
                })
                .setCancelButton(R.string.cancel));
    }

    public interface OnMenuItemClickListener{
        void onClick(int itemPos, int menuPos);
    }
}
