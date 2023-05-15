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

package zhy.hongyuan.ui.adapter.helper;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author  hongyuan
 * @date 2021/6/3 17:47
 */
public interface IItemTouchHelperViewHolder {

    /**
     * item被选中，在侧滑或拖拽过程中更新状态
     */
    void onItemSelected(RecyclerView.ViewHolder viewHolder);

    /**
     * item的拖拽或侧滑结束，恢复默认的状态
     */
    void onItemClear(RecyclerView.ViewHolder viewHolder);
}
