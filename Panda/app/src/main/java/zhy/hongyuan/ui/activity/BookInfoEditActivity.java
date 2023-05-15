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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.BaseActivity;
import zhy.hongyuan.base.BitIntentDataManager;
import zhy.hongyuan.common.APPCONST;
import xyz.fycz.myreader.databinding.ActivityBookInfoEditBinding;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.service.BookService;
import zhy.hongyuan.ui.dialog.DialogCreator;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.util.UriFileUtil;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;

/**
 * @author  hongyuan
 * @date 2021/4/24 15:05
 */
public class BookInfoEditActivity extends BaseActivity<ActivityBookInfoEditBinding> {
    private Book mBook;
    private String imgUrl;
    private String bookName;
    private String author;
    private String desc;

    @Override
    protected void bindView() {
        binding = ActivityBookInfoEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        getSupportActionBar().setTitle("书籍信息编辑");
    }

    @Override
    protected boolean initSwipeBackEnable() {
        return false;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mBook = (Book) BitIntentDataManager.getInstance().getData(getIntent());
        if (mBook == null) {
            ToastUtils.showError("未读取到书籍信息");
            finish();
        }
        imgUrl = NetworkUtils.getAbsoluteURL(ReadCrawlerUtil.getReadCrawler(mBook.getSource()).getNameSpace(), mBook.getImgUrl());
        bookName = mBook.getName();
        author = mBook.getAuthor();
        desc = mBook.getDesc();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initImg(imgUrl);
        binding.tieBookName.setText(bookName);
        binding.tieBookAuthor.setText(author);
        binding.tieCoverUrl.setText(imgUrl);
        binding.tieBookDesc.setText(desc);
    }

    @Override
    protected void initClick() {
        binding.btSelectLocalPic.setOnClickListener(v -> {
            ToastUtils.showInfo("请选择一张图片");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, APPCONST.REQUEST_SELECT_COVER);
        });
    }

    // 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_info_edit, menu);
        return true;
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveInfo();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APPCONST.REQUEST_SELECT_COVER) {
            if (resultCode == RESULT_OK && null != data) {
                String imgUrl = UriFileUtil.getPath(this, data.getData());
                binding.tieCoverUrl.setText(imgUrl);
                initImg(imgUrl);
            }
        }
    }

    @Override
    public void finish() {
        if (hasChange()) {
            DialogCreator.createThreeButtonDialog(this, "退出"
                    , "当前书籍信息已更改，是否保存？", true,
                    "直接退出", "取消", "保存并退出",
                    (dialog, which) -> super.finish(), null,
                    (dialog, which) -> {
                        saveInfo();
                        super.finish();
                    }
            );
        } else {
            super.finish();
        }
    }

    private void initImg(String imgUrl) {
        if (!App.isDestroy(this)) {
            binding.ivCover.load(imgUrl, mBook.getName(), mBook.getAuthor());
        }
    }

    private void saveInfo() {
        bookName = binding.tieBookName.getText().toString();
        author = binding.tieBookAuthor.getText().toString();
        imgUrl = binding.tieCoverUrl.getText().toString();
        desc = binding.tieBookDesc.getText().toString();
        mBook.setName(bookName);
        mBook.setAuthor(author);
        mBook.setImgUrl(imgUrl);
        mBook.setDesc(desc);
        BookService.getInstance().updateEntity(mBook);
        ToastUtils.showSuccess("书籍信息保存成功");
        setResult(Activity.RESULT_OK);
    }

    private boolean hasChange() {
        return !Objects.equals(bookName, binding.tieBookName.getText().toString()) ||
                !Objects.equals(author, binding.tieBookAuthor.getText().toString()) ||
                !Objects.equals(imgUrl, binding.tieCoverUrl.getText().toString()) ||
                !Objects.equals(desc, binding.tieBookDesc.getText().toString());
    }
}
