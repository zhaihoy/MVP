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

package zhy.hongyuan.greendao.convert;

import org.greenrobot.greendao.converter.PropertyConverter;

import zhy.hongyuan.greendao.entity.rule.TocRule;
import zhy.hongyuan.util.utils.GsonExtensionsKt;

/**
 * @author  hongyuan
 * @date 2021/2/8 18:28
 */
public class TocRuleConvert implements PropertyConverter<TocRule, String> {

    @Override
    public TocRule convertToEntityProperty(String databaseValue) {
        return GsonExtensionsKt.getGSON().fromJson(databaseValue, TocRule.class);
    }

    @Override
    public String convertToDatabaseValue(TocRule entityProperty) {
        return GsonExtensionsKt.getGSON().toJson(entityProperty);
    }
}