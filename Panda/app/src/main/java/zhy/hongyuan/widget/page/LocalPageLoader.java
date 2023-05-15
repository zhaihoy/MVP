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

package zhy.hongyuan.widget.page;

import android.util.Log;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import zhy.hongyuan.entity.Setting;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.greendao.entity.Chapter;
import zhy.hongyuan.greendao.service.ChapterService;
import zhy.hongyuan.util.IOUtils;
import zhy.hongyuan.util.help.StringHelper;
import zhy.hongyuan.util.utils.FileUtils;
import zhy.hongyuan.util.utils.RxUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static zhy.hongyuan.util.utils.FileUtils.BLANK;

/**
 * Created by newbiechen on 17-7-1.
 * 问题:
 * 1. 异常处理没有做好
 */

public class LocalPageLoader extends PageLoader {
    private static final String TAG = "LocalPageLoader";
    //默认从文件中获取数据的长度
    private final static int BUFFER_SIZE = 512 * 1024;
    //没有标题的时候，每个章节的最大长度
    private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;

    // "序(章)|前言"
    private final static Pattern mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE);

    //正则表达式章节匹配模式
    // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
    private static final String[] CHAPTER_PATTERNS = new String[]{"^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$",
            "^(\\s{0,4})(\\d{1,5}[：:,.，、_—\\-])(.{1,30})$",
            "^(\\s{0,4})(\u756a\u5916)(.{0,20})$"};

    //章节解析模式
    private Pattern mChapterPattern = null;
    //获取书本的文件
    private File mBookFile;
    //编码类型
    private String mCharset;

    private ChapterService mChapterService;

    public LocalPageLoader(PageView pageView, Book collBook, ChapterService mChapterService, Setting setting) {
        super(pageView, collBook, setting);
        mStatus = STATUS_PARING;
        this.mChapterService = mChapterService;
    }

    @Override
    public void refreshChapterList() {
        // 对于文件是否存在，或者为空的判断，不作处理。 ==> 在文件打开前处理过了。
        mBookFile = new File(mCollBook.getChapterUrl());
        if (!mBookFile.exists()) {
            error(STATUS_PARSE_ERROR, "书籍源文件不存在\n" + mCollBook.getChapterUrl());
            return;
        }
        mCharset = mCollBook.getInfoUrl();

        // 判断文件是否已经加载过，并具有缓存
        mChapterList = mChapterService.findBookAllChapterByBookId(mCollBook.getId());
        if (!mChapterList.isEmpty()) {

            isChapterListPrepare = true;

            //提示目录加载完成
            if (mPageChangeListener != null) {
                mPageChangeListener.onCategoryFinish(mChapterList);
            }

            // 加载并显示当前章节
            openChapter();

            return;
        }
        // 通过RxJava异步处理分章事件
        Single.create((SingleOnSubscribe<List<Chapter>>) e -> {
            e.onSuccess(loadChapters());
        }).compose(RxUtils::toSimpleSingle).subscribe(new SingleObserver<List<Chapter>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mChapterDis = d;
            }

            @Override
            public void onSuccess(List<Chapter> chapters) {
                mChapterDis = null;
                isChapterListPrepare = true;
                mCollBook.setInfoUrl(mCharset);
                mChapterList = chapters;
                mChapterService.addChapters(mChapterList);
                //提示目录加载完成
                if (mPageChangeListener != null) {
                    mPageChangeListener.onCategoryFinish(mChapterList);
                }
                // 加载并显示当前章节
                openChapter();
            }

            @Override
            public void onError(Throwable e) {
                error(STATUS_CATEGORY_ERROR, e.getLocalizedMessage());
                Log.d(TAG, "file load error:" + e.toString());
            }
        });
    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     */
    private List<Chapter> loadChapters() throws IOException {
        mBookFile = new File(mCollBook.getChapterUrl());

        //获取文件编码
        mCharset = FileUtils.getFileCharset(mBookFile);

        Log.d("mCharset", mCharset);

        List<Chapter> mChapterList = new ArrayList<>();
        //获取文件流
        RandomAccessFile bookStream = new RandomAccessFile(mBookFile, "r");
        //寻找匹配文章标题的正则表达式，判断是否存在章节名
        boolean hasChapter = checkChapterType(bookStream);
        //加载章节
        byte[] buffer = new byte[BUFFER_SIZE];
        //获取到的块起始点，在文件中的位置
        long curOffset = 0;
        //block的个数
        int blockPos = 0;
        //读取的长度
        int length;
        int allLength = 0;

        //获取文件中的数据到buffer，直到没有数据为止
        while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
            ++blockPos;
            //如果存在Chapter
            if (hasChapter) {
                //将数据转换成String
                String blockContent = new String(buffer, 0, length, mCharset);
                int lastN = blockContent.lastIndexOf("\n");
                if (lastN != 0) {
                    blockContent = blockContent.substring(0, lastN);
                    length = blockContent.getBytes(mCharset).length;
                    allLength = allLength + length;
                    bookStream.seek(allLength);
                }
                //当前Block下使过的String的指针
                int seekPos = 0;
                //进行正则匹配
                Matcher matcher = mChapterPattern.matcher(blockContent);
                //如果存在相应章节
                while (matcher.find()) {
                    //获取匹配到的字符在字符串中的起始位置
                    int chapterStart = matcher.start();

                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                    if (seekPos == 0 && chapterStart != 0) {
                        //获取当前章节的内容
                        String chapterContent = blockContent.substring(seekPos, chapterStart);
                        //设置指针偏移
                        seekPos += chapterContent.length();

                        if (mChapterList.size() == 0) { //如果当前没有章节，那么就是序章

                            //创建序章
                            Chapter preChapter = new Chapter();
                            preChapter.setTitle("序章");
                            preChapter.setStart(0);
                            preChapter.setEnd(chapterContent.getBytes(mCharset).length); //获取String的byte值,作为最终值

                            //如果序章大小大于500才添加进去
                            if (preChapter.getEnd() - preChapter.getStart() > 500) {
                                mChapterList.add(preChapter);
                            } else {
                                //加入简介
                                mCollBook.setDesc(chapterContent);
                            }


                            //创建当前章节
                            Chapter curChapter = new Chapter();
                            curChapter.setTitle(matcher.group());
                            curChapter.setStart(chapterContent.getBytes(mCharset).length);
                            mChapterList.add(curChapter);
                        } else {  //否则就block分割之后，上一个章节的剩余内容
                            //获取上一章节
                            Chapter lastChapter = mChapterList.get(mChapterList.size() - 1);
                            //将当前段落添加上一章去
                            lastChapter.setEnd(lastChapter.getEnd() + chapterContent.getBytes(mCharset).length);

                            //创建当前章节
                            Chapter curChapter = new Chapter();
                            curChapter.setTitle(matcher.group());
                            curChapter.setStart(lastChapter.getEnd());
                            mChapterList.add(curChapter);
                        }
                    } else {
                        //是否存在章节
                        if (mChapterList.size() != 0) {
                            //获取章节内容
                            String chapterContent = blockContent.substring(seekPos, matcher.start());
                            seekPos += chapterContent.length();

                            //获取上一章节
                            Chapter lastChapter = mChapterList.get(mChapterList.size() - 1);
                            lastChapter.setEnd(lastChapter.getStart() + chapterContent.getBytes(mCharset).length);

                            //创建当前章节
                            Chapter curChapter = new Chapter();
                            curChapter.setTitle(matcher.group());
                            curChapter.setStart(lastChapter.getEnd());
                            mChapterList.add(curChapter);
                        } else { //如果章节不存在则创建章节
                            Chapter curChapter = new Chapter();
                            curChapter.setTitle(matcher.group());
                            curChapter.setStart(0L);
                            curChapter.setEnd(0L);
                            mChapterList.add(curChapter);
                        }
                    }
                }
            } else { //进行本地虚拟分章
                //章节在buffer的偏移量
                int chapterOffset = 0;
                //当前剩余可分配的长度
                int strLength = length;
                //分章的位置
                int chapterPos = 0;

                while (strLength > 0) {
                    ++chapterPos;
                    //是否长度超过一章
                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                        //在buffer中一章的终止点
                        int end = length;
                        //寻找换行符作为终止点
                        for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
                            if (buffer[i] == BLANK) {
                                end = i;
                                break;
                            }
                        }
                        Chapter chapter = new Chapter();
                        chapter.setTitle("第" + blockPos + "章" + "(" + chapterPos + ")");
                        chapter.setStart(curOffset + chapterOffset + 1);
                        if (chapter.getStart() == 1) chapter.setStart(0);
                        chapter.setEnd(curOffset + end);
                        mChapterList.add(chapter);
                        //减去已经被分配的长度
                        strLength = strLength - (end - chapterOffset);
                        //设置偏移的位置
                        chapterOffset = end;
                    } else {
                        Chapter chapter = new Chapter();
                        chapter.setTitle("第" + blockPos + "章" + "(" + chapterPos + ")");
                        chapter.setStart(curOffset + chapterOffset + 1);
                        if (chapter.getStart() == 1) chapter.setStart(0);
                        chapter.setEnd(curOffset + length);
                        mChapterList.add(chapter);
                        strLength = 0;
                    }
                }
            }

            //block的偏移点
            curOffset += length;

            if (hasChapter) {
                if (mChapterList.size() != 0) {
                    //设置上一章的结尾
                    Chapter lastChapter = mChapterList.get(mChapterList.size() - 1);
                    lastChapter.setEnd(curOffset);
                }
            }

            //当添加的block太多的时候，执行GC
            if (blockPos % 15 == 0) {
                System.gc();
                System.runFinalization();
            }
        }
        int i = 0;
        for (Chapter chapter : mChapterList) {
            chapter.setTitle(chapter.getTitle().trim());
            chapter.setBookId(mCollBook.getId());
            chapter.setId(StringHelper.getStringRandom(25));
            chapter.setNumber(i++);
        }

        IOUtils.close(bookStream);

        System.gc();
        System.runFinalization();

        return mChapterList;
    }


    /**
     * 从文件中提取一章的内容
     *
     * @param chapter
     * @return
     */
    private byte[] getContent(Chapter chapter) {
        RandomAccessFile bookStream = null;
        try {
            bookStream = new RandomAccessFile(mBookFile, "r");
            bookStream.seek(chapter.getStart());
            int extent = (int) (chapter.getEnd() - chapter.getStart());
            byte[] content = new byte[extent];
            bookStream.read(content, 0, extent);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bookStream);
        }
        return null;
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    private boolean checkChapterType(RandomAccessFile bookStream) throws IOException {
        //首先获取128k的数据
        byte[] buffer = new byte[BUFFER_SIZE / 4];
        int length = bookStream.read(buffer, 0, buffer.length);
        //进行章节匹配
        for (String str : CHAPTER_PATTERNS) {
            Pattern pattern = Pattern.compile(str, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(new String(buffer, 0, length, mCharset));
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern;
                //重置指针位置
                bookStream.seek(0);
                return true;
            }
        }

        //重置指针位置
        bookStream.seek(0);
        return false;
    }


    @Override
    public String getChapterReader(Chapter chapter) throws Exception {
        if (chapter.getEnd() > 0) {
            //从文件中获取数据
            /*byte[] content = getContent(chapter);
            ByteArrayInputStream bais = new ByteArrayInputStream(content);
            BufferedReader br = new BufferedReader(new InputStreamReader(bais, mCharset));
            return br;*/
            byte[] content = getContent(chapter);
            return new String(content, mCharset);
        }
        /*File file = new File(APPCONST.BOOK_CACHE_PATH + mCollBook.getId()
                + File.separator + chapter.getTitle() + FileUtils.SUFFIX_FY);
        Log.d("getChapterReader", file.getPath());
        if (!file.exists()) return null;
        return new BufferedReader(new FileReader(file));*/
        return mChapterService.getChapterCatheContent(chapter);
    }


    @Override
    public boolean hasChapterData(Chapter chapter) {
        return chapter.getEnd() > 0 || ChapterService.isChapterCached(chapter);
    }

}
