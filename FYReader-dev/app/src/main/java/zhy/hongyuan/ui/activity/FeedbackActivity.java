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
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.widget.Toolbar;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.BaseActivity;
import xyz.fycz.myreader.databinding.ActivityFeedbackBinding;
import zhy.hongyuan.ui.dialog.DialogCreator;

/**
 * @author  hongyuan
 * @date 2020/12/24 20:48
 */
public class FeedbackActivity extends BaseActivity<ActivityFeedbackBinding> {

    @Override
    protected void bindView() {
        binding =ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        getSupportActionBar().setTitle("建议反馈");
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initWidget() {
        super.initWidget();
        //声明WebSettings子类
        WebSettings webSettings = binding.wvFeedback.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        binding.wvFeedback.loadUrl("https://www.wjx.cn/jq/102348283.aspx");
        binding.wvFeedback.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.endsWith("102348283.aspx")){
                    System.out.println(url);
                    if (url.contains("complete")){
                        DialogCreator.createCommonDialog(FeedbackActivity.this, "意见反馈",
                                "提交成功，感谢您的建议反馈！", false, "知道了",
                                (dialog, which) -> finish());
                    }
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (!App.isDestroy(FeedbackActivity.this))
                    binding.refreshLayout.showError();
            }
        });
        binding.wvFeedback.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100 && !App.isDestroy(FeedbackActivity.this)){
                    binding.refreshLayout.showFinish();
                }
            }});
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.refreshLayout.setOnReloadingListener(() -> {
            binding.wvFeedback.loadUrl("https://www.wjx.cn/jq/102348283.aspx");
        });
    }
}
