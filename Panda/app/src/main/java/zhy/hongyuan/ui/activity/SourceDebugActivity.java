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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.fycz.myreader.R;
import zhy.hongyuan.base.BaseActivity;
import zhy.hongyuan.base.observer.MyObserver;
import zhy.hongyuan.common.APPCONST;
import xyz.fycz.myreader.databinding.ActivitySourceDebugBinding;
import zhy.hongyuan.entity.SearchBookBean;
import zhy.hongyuan.entity.StrResponse;
import zhy.hongyuan.entity.sourcedebug.DebugBook;
import zhy.hongyuan.entity.sourcedebug.DebugChapter;
import zhy.hongyuan.entity.sourcedebug.DebugEntity;
import zhy.hongyuan.entity.sourcedebug.ListResult;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.entity.rule.BookSource;
import zhy.hongyuan.model.mulvalmap.ConMVMap;
import zhy.hongyuan.model.third3.Debug;
import zhy.hongyuan.ui.dialog.LoadingDialog;
import zhy.hongyuan.util.utils.GsonExtensionsKt;
import zhy.hongyuan.util.utils.NetworkUtils;
import zhy.hongyuan.util.utils.OkHttpUtils;
import zhy.hongyuan.util.utils.RxUtils;
import zhy.hongyuan.webapi.BookApi;
import zhy.hongyuan.webapi.crawler.ReadCrawlerUtil;
import zhy.hongyuan.webapi.crawler.base.BookInfoCrawler;
import zhy.hongyuan.webapi.crawler.base.ReadCrawler;
import zhy.hongyuan.webapi.crawler.source.ThirdCrawler;
import zhy.hongyuan.widget.codeview.Language;

import static zhy.hongyuan.util.utils.OkHttpUtils.getCookies;

/**
 * @author  hongyuan
 * @date 2021/2/12 18:55
 */
public class SourceDebugActivity extends BaseActivity<ActivitySourceDebugBinding> {
    private DebugEntity debugEntity;
    private ReadCrawler rc;
    private Disposable disposable;
    private LoadingDialog loadingDialog;
    private boolean isThird3;
    private StringBuilder logSb;

    @Override
    protected void bindView() {
        binding = ActivitySourceDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        switch (debugEntity.getDebugMode()) {
            case DebugEntity.SEARCH:
            default:
                getSupportActionBar().setTitle(R.string.debug_search);
                break;
            case DebugEntity.INFO:
                getSupportActionBar().setTitle(R.string.debug_info);
                break;
            case DebugEntity.TOC:
                getSupportActionBar().setTitle(R.string.debug_toc);
                break;
            case DebugEntity.CONTENT:
                getSupportActionBar().setTitle(R.string.debug_content);
                break;
        }
        initDebugEntity();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        debugEntity = getIntent().getParcelableExtra("debugEntity");
        if (debugEntity == null) finish();
        BookSource source = debugEntity.getBookSource();
        isThird3 = APPCONST.THIRD_3_SOURCE.equals(source.getSourceType());
        rc = ReadCrawlerUtil.getReadCrawler(source, true);
        loadingDialog = new LoadingDialog(this, "正在请求", () -> {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        });
        if (isThird3) {
            binding.tabLayout.getTabAt(1).setText(R.string.debug_log);
            logSb = new StringBuilder("-----仅输出使用java.log()函数打印的日志-----\n");
            Debug.INSTANCE.setCallback(msg -> logSb.append(msg).append("\n"));
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        binding.tvTip.setText(getString(R.string.debug_desc,
                debugEntity.getBookSource().getSourceName(),
                debugEntity.getBookSource().getSourceUrl(),
                debugEntity.getUrl()));
        binding.rvParseResult.setLanguage(Language.JSON);
        binding.rvSourceCode.setLanguage(Language.AUTO);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.rvSourceCode.setVisibility(View.GONE);
                    binding.rvParseResult.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    binding.rvSourceCode.setVisibility(View.VISIBLE);
                    binding.rvParseResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (isThird3) {
            Debug.INSTANCE.setCallback(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_re_request) {
            initDebugEntity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDebugEntity() {
        loadingDialog.show();
        Observable<Boolean> observable;
        if (rc instanceof ThirdCrawler) {
            observable = debugThirdSource();
        } else {
            observable = Observable.create((ObservableOnSubscribe<StrResponse>) emitter -> {
                String url = debugEntity.getUrl();
                Map<String, String> headers = rc.getHeaders();
                headers.putAll(getCookies(rc.getNameSpace()));
                if (debugEntity.getDebugMode() == DebugEntity.SEARCH && rc.isPost()) {
                    String[] urlInfo = url.split(",");
                    url = NetworkUtils.getAbsoluteURL(rc.getNameSpace(), urlInfo[0]);
                    String body = urlInfo[1];
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody requestBody = RequestBody.create(mediaType, body);
                    emitter.onNext(OkHttpUtils.getStrResponse(url, requestBody, rc.getCharset(), headers));
                } else {
                    emitter.onNext((OkHttpUtils.getStrResponse(NetworkUtils.getAbsoluteURL(rc.getNameSpace(), url), rc.getCharset(), headers)));
                }
                emitter.onComplete();

            }).flatMap(response -> OkHttpUtils.setCookie(response, rc.getNameSpace()))
                    .flatMap((Function<StrResponse, ObservableSource<Boolean>>) this::debugSource);
        }

        observable.compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                addDisposable(d);
            }

            @Override
            public void onNext(@NonNull Boolean flag) {
                binding.rvParseResult.setCode(debugEntity.getParseResult()).apply();
                if (!isThird3) {
                    binding.rvSourceCode.setCode(debugEntity.getHtml()).apply();
                } else {
                    binding.rvSourceCode.setCode(logSb.toString()).apply();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                binding.rvParseResult.setCode(String.format("{\n\b\b\b\b\"result\": \"error\", \n\b\b\b\b\"msg\": \"%s\"\n}"
                        , e.getLocalizedMessage().replace("\"", "\\\""))).apply();
                if (!isThird3) {
                    binding.rvSourceCode.setCode(debugEntity.getHtml()).apply();
                } else {
                    binding.rvSourceCode.setCode(logSb.toString()).apply();
                }
                loadingDialog.dismiss();
            }
        });
    }

    private Observable<Boolean> debugSource(StrResponse response) {
        debugEntity.setHtml(response.body());
        ListResult listResult = new ListResult();
        switch (debugEntity.getDebugMode()) {
            case DebugEntity.SEARCH:
            default:
                return rc.getBooksFromStrResponse(response)
                        .flatMap((Function<ConMVMap<SearchBookBean, Book>, ObservableSource<Boolean>>) books -> Observable.create(emitter -> {
                            List<DebugBook> debugBooks = book2DebugBook(books.values());
                            listResult.set信息(String.format("解析完毕，共%s本书籍", debugBooks.size()));
                            listResult.set结果(debugBooks);
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON()
                                    .toJson(listResult));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.INFO:
                return ((BookInfoCrawler) rc).getBookInfo(response, new Book())
                        .flatMap((Function<Book, ObservableSource<Boolean>>) book -> Observable.create(emitter -> {
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON().toJson(book2DebugBook(book)));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.TOC:
                return rc.getChaptersFromStrResponse(response)
                        .flatMap((Function<List<Chapter>, ObservableSource<Boolean>>) chapters -> Observable.create(emitter -> {
                            List<DebugChapter> debugChapters = chapter2DebugChapter(chapters);
                            listResult.set信息(String.format("解析完毕，共%s个章节", debugChapters.size()));
                            listResult.set结果(debugChapters);
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON()
                                    .toJson(listResult));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.CONTENT:
                return rc.getContentFormStrResponse(response)
                        .flatMap((Function<String, ObservableSource<Boolean>>) content -> Observable.create(emitter -> {
                            debugEntity.setParseResult(content);
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
        }
    }

    private Observable<Boolean> debugThirdSource() {
        debugEntity.setHtml("第三方书源不支持查看源码");
        ListResult listResult = new ListResult();
        Book book = new Book();
        book.setChapterUrl(debugEntity.getUrl());
        book.setInfoUrl(debugEntity.getUrl());
        switch (debugEntity.getDebugMode()) {
            case DebugEntity.SEARCH:
            default:
                return BookApi.search(debugEntity.getKey(), rc)
                        .flatMap((Function<ConMVMap<SearchBookBean, Book>, ObservableSource<Boolean>>) books -> Observable.create(emitter -> {
                            List<DebugBook> debugBooks = book2DebugBook(books.values());
                            listResult.set信息(String.format("解析完毕，共%s本书籍", debugBooks.size()));
                            listResult.set结果(debugBooks);
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON()
                                    .toJson(listResult));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.INFO:
                return BookApi.getBookInfo(book, (BookInfoCrawler) rc)
                        .flatMap((Function<Book, ObservableSource<Boolean>>) book1 -> Observable.create(emitter -> {
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON().toJson(book2DebugBook(book1)));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.TOC:
                return BookApi.getBookChapters(book, rc)
                        .flatMap((Function<List<Chapter>, ObservableSource<Boolean>>) chapters -> Observable.create(emitter -> {
                            List<DebugChapter> debugChapters = chapter2DebugChapter(chapters);
                            listResult.set信息(String.format("解析完毕，共%s个章节", debugChapters.size()));
                            listResult.set结果(debugChapters);
                            debugEntity.setParseResult(GsonExtensionsKt.getGSON()
                                    .toJson(listResult));
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
            case DebugEntity.CONTENT:
                Chapter chapter = new Chapter();
                book.setChapterUrl(rc.getNameSpace());
                chapter.setUrl(debugEntity.getUrl());
                return BookApi.getChapterContent(chapter, book, rc)
                        .flatMap((Function<String, ObservableSource<Boolean>>) content -> Observable.create(emitter -> {
                            debugEntity.setParseResult(content);
                            emitter.onNext(true);
                            emitter.onComplete();
                        }));
        }
    }

    private List<DebugBook> book2DebugBook(List<Book> books) {
        List<DebugBook> debugBooks = new ArrayList<>();
        for (Book book : books) {
            debugBooks.add(book2DebugBook(book));
        }
        return debugBooks;
    }

    private DebugBook book2DebugBook(Book book) {
        DebugBook debugBook = new DebugBook();
        /*debugBook.setName(book.getName());
        debugBook.setAuthor(book.getAuthor());
        debugBook.setType(book.getType());
        debugBook.setDesc(book.getDesc());
        debugBook.setWordCount(book.getWordCount());
        debugBook.setStatus(book.getStatus());
        debugBook.setLastChapter(book.getNewestChapterTitle());
        debugBook.setUpdateTime(book.getUpdateDate());
        debugBook.setImgUrl(book.getImgUrl());
        debugBook.setTocUrl(book.getChapterUrl());
        debugBook.setInfoUrl(book.getInfoUrl());*/
        debugBook.set书名(book.getName());
        debugBook.set作者(book.getAuthor());
        debugBook.set分类(book.getType());
        debugBook.set简介(book.getDesc());
        debugBook.set字数(book.getWordCount());
        debugBook.set连载状态(book.getStatus());
        debugBook.set最新章节(book.getNewestChapterTitle());
        debugBook.set更新时间(book.getUpdateDate());
        debugBook.set封面链接(book.getImgUrl());
        debugBook.set目录链接(book.getChapterUrl());
        debugBook.set详情链接(book.getInfoUrl());
        return debugBook;
    }

    private List<DebugChapter> chapter2DebugChapter(List<Chapter> chapters) {
        List<DebugChapter> debugChapters = new ArrayList<>();
        for (Chapter chapter : chapters) {
            DebugChapter debugChapter = new DebugChapter();
            /*debugChapter.setTitle(chapter.getTitle());
            debugChapter.setUrl(chapter.getUrl());*/
            debugChapter.set章节名称(chapter.getTitle());
            debugChapter.set章节链接(chapter.getUrl());
            debugChapters.add(debugChapter);
        }
        return debugChapters;
    }
}
