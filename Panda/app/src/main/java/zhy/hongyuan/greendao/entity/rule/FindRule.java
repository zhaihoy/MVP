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

package zhy.hongyuan.greendao.entity.rule;

import android.os.Parcel;
import android.os.Parcelable;

import static zhy.hongyuan.util.utils.StringUtils.stringEquals;

/**
 * @author  hongyuan
 * @date 2021/2/10 8:57
 */
public class FindRule implements Parcelable, BookListRule {
    private String url;
    private String list;
    private String name;
    private String author;
    private String type;
    private String desc;
    private String wordCount;
    private String status;
    private String lastChapter;
    private String updateTime;
    private String imgUrl;
    private String tocUrl;
    private String infoUrl;

    public FindRule() {
    }

    protected FindRule(Parcel in) {
        url = in.readString();
        list = in.readString();
        name = in.readString();
        author = in.readString();
        type = in.readString();
        desc = in.readString();
        wordCount = in.readString();
        status = in.readString();
        lastChapter = in.readString();
        updateTime = in.readString();
        imgUrl = in.readString();
        tocUrl = in.readString();
        infoUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(list);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(type);
        dest.writeString(desc);
        dest.writeString(wordCount);
        dest.writeString(status);
        dest.writeString(lastChapter);
        dest.writeString(updateTime);
        dest.writeString(imgUrl);
        dest.writeString(tocUrl);
        dest.writeString(infoUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FindRule> CREATOR = new Creator<FindRule>() {
        @Override
        public FindRule createFromParcel(Parcel in) {
            return new FindRule(in);
        }

        @Override
        public FindRule[] newArray(int size) {
            return new FindRule[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getList() {
        return list;
    }

    public void setList(String bookList) {
        this.list = bookList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWordCount() {
        return wordCount;
    }

    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTocUrl() {
        return tocUrl;
    }

    public void setTocUrl(String tocUrl) {
        this.tocUrl = tocUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) o = new FindRule();
        if (getClass() != o.getClass()) return false;
        FindRule findRule = (FindRule) o;
        return  stringEquals(url, findRule.url) &&
                stringEquals(list, findRule.list) &&
                stringEquals(name, findRule.name) &&
                stringEquals(author, findRule.author) &&
                stringEquals(type, findRule.type) &&
                stringEquals(desc, findRule.desc) &&
                stringEquals(wordCount, findRule.wordCount) &&
                stringEquals(status, findRule.status) &&
                stringEquals(lastChapter, findRule.lastChapter) &&
                stringEquals(updateTime, findRule.updateTime) &&
                stringEquals(imgUrl, findRule.imgUrl) &&
                stringEquals(tocUrl, findRule.tocUrl) &&
                stringEquals(infoUrl, findRule.infoUrl);
    }

}
