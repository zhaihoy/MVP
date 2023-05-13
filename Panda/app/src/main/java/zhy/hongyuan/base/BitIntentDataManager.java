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

package zhy.hongyuan.base;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import zhy.hongyuan.common.APPCONST;

public class BitIntentDataManager {
    private static Map<String, Object> bigData;

    private static BitIntentDataManager instance = null;

    private BitIntentDataManager() {
        bigData = new HashMap<>();
    }

    public static BitIntentDataManager getInstance() {
        if (instance == null) {
            synchronized (BitIntentDataManager.class) {
                if (instance == null) {
                    instance = new BitIntentDataManager();
                }
            }
        }
        return instance;
    }

    public Object getData(String key) {
        Object object = bigData.get(key);
        bigData.remove(key);
        return object;
    }

    public Object getData(Intent intent){
        String dataKey = intent.getStringExtra(APPCONST.DATA_KEY);
        return getData(dataKey);
    }

    public void putData(String key, Object data) {
        bigData.put(key, data);
    }

    public void putData(Intent intent, Object data){
        String dataKey = String.valueOf(System.currentTimeMillis());
        intent.putExtra(APPCONST.DATA_KEY, dataKey);
        putData(dataKey, data);
    }
}
