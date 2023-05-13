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

package zhy.hongyuan.widget.page;

import java.util.List;

public class TxtPage {
    int position;
    String title;
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;
    List<TxtLine> txtLists;

    public TxtPage(int position) {
        this.position = position;
    }

    public TxtPage() {
    }

    public int getTitleLines() {
        return titleLines;
    }

    public int size() {
        return lines.size();
    }

    public String getLine(int i) {
        return lines.get(i);
    }

    public String getContent() {
        StringBuilder s = new StringBuilder();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                s.append(lines.get(i));
            }
        }
        return s.toString();
    }

}