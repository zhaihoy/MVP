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

package zhy.hongyuan.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author  hongyuan
 * @date 2022/1/18 10:20
 */
@Entity
public class Cache {
    @Id
    private String key;

    private String value;

    private long deadLine;

    @Generated(hash = 1252535078)
    public Cache(String key, String value, long deadLine) {
        this.key = key;
        this.value = value;
        this.deadLine = deadLine;
    }

    @Generated(hash = 1305017356)
    public Cache() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getDeadLine() {
        return this.deadLine;
    }

    public void setDeadLine(long deadLine) {
        this.deadLine = deadLine;
    }
}
