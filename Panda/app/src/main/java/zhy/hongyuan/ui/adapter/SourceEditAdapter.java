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

package zhy.hongyuan.ui.adapter;

import zhy.hongyuan.base.adapter.BaseListAdapter;
import zhy.hongyuan.base.adapter.IViewHolder;
import zhy.hongyuan.entity.sourceedit.EditEntity;
import zhy.hongyuan.ui.adapter.holder.SourceEditHolder;

/**
 * @author  hongyuan
 * @date 2021/2/9 10:08
 */
public class SourceEditAdapter extends BaseListAdapter<EditEntity> {
    @Override
    protected IViewHolder<EditEntity> createViewHolder(int viewType) {
        return new SourceEditHolder();
    }
}
