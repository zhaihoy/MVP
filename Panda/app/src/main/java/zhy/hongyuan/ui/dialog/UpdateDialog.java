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

package zhy.hongyuan.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.observer.MyObserver;
import zhy.hongyuan.common.APPCONST;
import xyz.fycz.myreader.databinding.FragmentUpdateBinding;
import zhy.hongyuan.util.ToastUtils;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.webapi.LanZouApi;


public class UpdateDialog extends Fragment {

    private String version, content, appUrl, path;
    private boolean isContentHtml = false;
    private boolean cancelable;
    //    private OnConfirmListener listener;
    private FragmentActivity mActivity;
    private final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0x100;
    private BaseDownloadTask baseDownloadTask;
    private boolean debug = false;
    private FragmentUpdateBinding binding;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpdateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FileDownloader.setup(mActivity);
        initViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (baseDownloadTask != null && (!baseDownloadTask.isRunning() || baseDownloadTask.pause()))
            baseDownloadTask = null;
    }

    private void initViews() {
        binding.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.ivClose.setVisibility(cancelable ? View.VISIBLE : View.GONE);
        binding.btUpdate.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onConfirm(v);
//            } else {
            XXPermissions.with(mActivity)
                    .permission(APPCONST.STORAGE_PERMISSIONS)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            downloadApk(appUrl);
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            ToastUtils.showWarring("请勿拒绝储存权限，否则无法更新应用！");
                        }
                    });
//            }
        });
        binding.btBrowser.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(appUrl));
            mActivity.startActivity(intent);
        });
        binding.rlProgress.setOnClickListener(v -> {
            if (binding.barPercentView.getProgress() == 1) {
                install(new File(path), mActivity);
            }
        });
        binding.ivClose.setOnClickListener(v -> {
            if (baseDownloadTask == null) {
                dismissUpdateDialog();
                return;
            }
            if (!baseDownloadTask.isRunning() || baseDownloadTask.pause()) {
                dismissUpdateDialog();
            }
        });
        binding.tvVersion.setText(TextUtils.isEmpty(version) ? "" : version);
        if (isContentHtml) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvContent.setText(Html.fromHtml(TextUtils.isEmpty(content) ? "" : content, Html.FROM_HTML_MODE_LEGACY));
            } else {
                binding.tvContent.setText(Html.fromHtml(TextUtils.isEmpty(content) ? "" : content));
            }
        } else {
            binding.tvContent.setText(content);
        }
    }

    public static class Builder {

        private UpdateDialog updateDialog;

        public Builder() {
            this.updateDialog = new UpdateDialog();
        }

        public Builder setVersion(String version) {
            updateDialog.version = version;
            return this;
        }

        public Builder setContent(String content) {
            updateDialog.content = content;
            return this;
        }

        public Builder setContentHtml(boolean isContentHtml) {
            updateDialog.isContentHtml = isContentHtml;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            updateDialog.cancelable = cancelable;
            return this;
        }

        public Builder setDebug(boolean debug) {
            updateDialog.debug = debug;
            return this;
        }

        public Builder setDownloadUrl(String url) {
            updateDialog.appUrl = url;
            return this;
        }

//        public Builder setOnConfirmListener(OnConfirmListener listener) {
//            updateDialog.listener = listener;
//            return this;
//        }

        public UpdateDialog build() {
            return updateDialog;
        }

    }

//    public void setPercent(int progressPercent) {
//        barPercentView.setPercentage(progressPercent);
//        progressTv.setText(progressPercent + "%");
//    }

//    public interface OnConfirmListener {
//        void onConfirm(View view);
//    }

    public void showUpdateDialog(FragmentActivity activity) {
        if (this.isAdded() && this.isVisible()) return;
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(android.R.id.content, this);
        fragmentTransaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
        fragmentTransaction.show(this);
        if (activity.isFinishing() || activity.isDestroyed()) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }
    }

    public void dismissUpdateDialog() {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (this.isAdded() && this.isVisible()) {
            fragmentTransaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
            fragmentTransaction.hide(this);
            fragmentTransaction.commit();
            new Handler().postDelayed(this::destroyUpdateDialog, 500);
        }
    }

    private void destroyUpdateDialog() {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(this);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    private void downloadApk(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl)) throw new NullPointerException("url can not be empty");
        binding.rlProgress.setVisibility(View.VISIBLE);
        binding.btUpdate.setVisibility(View.GONE);
        binding.btBrowser.setVisibility(View.GONE);
        if (apkUrl.endsWith(".apk")) {
            downloadApkNormal(apkUrl);
        } else if (apkUrl.contains("wwwq.lanzouy.com")) {
            downloadWithLanzous(apkUrl);
        } else {
            downloadApkWithNoFileName(apkUrl);
        }
    }

    private void downloadApkNormal(String apkUrl) {
        String decodeUrl;
        try {
            decodeUrl = URLDecoder.decode(apkUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            decodeUrl = apkUrl;
        }
        if (debug) Log.e("downloadApk", "originUrl------->" + apkUrl);
        if (debug) Log.e("downloadApk", "decodeUrl------->" + decodeUrl);
//        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + decodeUrl.substring(decodeUrl.lastIndexOf("/") + 1);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/熊猫读书 " + version + ".apk";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists() && dir.mkdir()) {
            startDownloadingApk(apkUrl);
        } else {
            startDownloadingApk(apkUrl);
        }
    }

    private void startDownloadingApk(String decodeUrl) {
        baseDownloadTask = FileDownloader.getImpl()
                .create(decodeUrl)
                .setPath(path, new File(path).isDirectory())
                .setCallbackProgressMinInterval(30)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        if (debug) Log.d("downloadApk", "pending-------");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        float percent = 1f * soFarBytes / totalBytes * 100;
                        if (debug) Log.d("downloadApk", "progress-------" + percent);
                        if (percent >= 3) {
                            binding.barPercentView.setPercentage(percent);
                            binding.tvProgress.setText((int) percent + "%");
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        if (debug) Log.d("downloadApk", "paused-------");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (debug) Log.d("downloadApk", "completed-------");
                        binding.barPercentView.setPercentage(100);
                        binding.tvProgress.setText("100%");
                        install(new File(path), mActivity);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        if (debug) Log.e("downloadApk", "error-------" + e.getMessage());
                        UpdateDialog.this.error();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        if (debug) Log.e("downloadApk", "warn-------");
                    }
                });
        baseDownloadTask.setAutoRetryTimes(3);
        baseDownloadTask.start();
    }


    /**
     * 安装
     */
    private void install(File filePath, Context context) {
        if (filePath.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是Android N 以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d("UpdateDialog", "install: " + filePath);
                Uri contentUri = FileProvider.getUriForFile(context, mActivity.getPackageName() + ".fileprovider", filePath);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(filePath), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            downloadApk(appUrl);
        }
    }

    private void downloadApkWithNoFileName(String appUrl) {
        App.getApplication().newThread(() -> {
            try {
                URL url = new URL(appUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    URL url1 = urlConnection.getURL();
                    downloadApkNormal(url1.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void downloadWithLanzous(String apkUrl) {
        binding.tvProgress.setText("正在获取下载链接...");
        binding.barPercentView.setPercentage(0);
        LanZouApi.INSTANCE.getFileUrl(apkUrl)
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new MyObserver<String>() {
                    @Override
                    public void onNext(String directUrl) {
                        if (directUrl == null) {
                            error();
                        } else {
                            downloadApkNormal(directUrl);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        error();
                    }
                });
    }

    private void error() {
        ToastUtils.showError("下载链接获取失败，请前往浏览器下载！");
        binding.btBrowser.performClick();
        binding.rlProgress.setVisibility(View.GONE);
        binding.btUpdate.setVisibility(View.VISIBLE);
        binding.btBrowser.setVisibility(View.VISIBLE);
    }
}
